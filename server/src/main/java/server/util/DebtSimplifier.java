package server.util;

import commons.Money;
import commons.Participant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class DebtSimplifier {

    private final ExchangeRateFactory exchangeRateFactory;

    private final HashMap<Participant, LinkedList<Debt>> participants;

    // these are automatically min-heaps, yay!
    private final PriorityQueue<ParticipantMoneyPair> creditors =
            new PriorityQueue<>();
    private final PriorityQueue<ParticipantMoneyPair> debtors =
            new PriorityQueue<>();

    private ParticipantMoneyPair zeroMoneyInBaseCurrency;

    /**
     * Creates a new {@code DebtSimplifier} object.
     *
     * @param   participants
     *          The participants to divide the debts over, duplicates are
     *          ignored.
     */
    public DebtSimplifier(Collection<Participant> participants) {
        this(participants, ExchangeRateFactory.get());
    }

    /**
     * Creates a new {@code DebtSimplifier} object. Used for testing.
     *
     * @param   participants
     *          The participants to divide the debts over, duplicates are
     *          ignored.
     * @param   exchangeRateFactory
     *          The {@link ExchangeRateFactory}s used by this simplifier.
     */
    DebtSimplifier(Collection<Participant> participants,
                   ExchangeRateFactory exchangeRateFactory) {
        this.participants = new HashMap<>();
        for (Participant participant : participants) {
            this.participants.put(participant, new LinkedList<>());
        }
        this.exchangeRateFactory = exchangeRateFactory;
    }

    /**
     * Adds a debt that should be taken into account in the calculation. Throws
     * {@link NullPointerException} if the debt is {@code null} and {@link
     * IllegalArgumentException} if the debt is between a participant not
     * present in the calculation.
     *
     * @param   debt
     *          A debt that should be taken into account in the calculation.
     */
    public void addDebt(Debt debt) {
        if (debt == null)
            throw new NullPointerException();

        if (!participants.containsKey(debt.getFrom()))
            throw new IllegalArgumentException(
                    "Debt contains unknown participant (from): " + debt);

        if (!participants.containsKey(debt.getTo()))
            throw new IllegalArgumentException(
                    "Debt contains unknown participant (to): " + debt);

        participants.get(debt.getTo()).add(debt);
        participants.get(debt.getFrom()).add(debt);
    }

    /**
     * Divides the specified amount over the debtors, paid by the creditor.
     * Distributes remainder randomly (but deterministically) as distributing it
     * properly is out of the scope of this project as it is too hard to
     * implement in combination with multiple currencies.
     *
     * @param   creditor
     *          The participant to which the debt should be paid.
     * @param   debtors
     *          The participants that owe (part of) the debt, duplicates are
     *          ignored. <strong>Can also include creditor.</strong>
     * @param   amount
     *          The amount of money to divide.
     */
    public void divideDebts(Participant creditor,
                            List<Participant> debtors, Money amount) {
        final Currency currency = amount.getCurrency();

        Set<Participant> uniqueDebtors = validateParameters(creditor, debtors,
                amount);

        LinkedList<Participant> debtorsShadow = new LinkedList<>(debtors);

        BigDecimal remainder = amount.getAmount().remainder(
                (new BigDecimal(uniqueDebtors.size())).movePointLeft(
                        currency.getDefaultFractionDigits()));
        BigDecimal fraction = amount.getAmount().subtract(remainder).divide(
                new BigDecimal(uniqueDebtors.size()), RoundingMode.DOWN);

        BigDecimal cent = BigDecimal.ONE.movePointLeft(
                currency.getDefaultFractionDigits());
        int cents = remainder.movePointRight(
                currency.getDefaultFractionDigits()).intValue();
        Random random = new Random(42);

        for (int i = 0; i < uniqueDebtors.size(); i++) {
            Participant debtor =
                    debtorsShadow.remove(random.nextInt(debtorsShadow.size()));
            if (!Objects.equals(creditor, debtor))  // the creditor already paid
                addDebt(new Debt(debtor, creditor,
                        new Money(i < cents ? fraction.add(cent) : fraction,
                                currency)
                ));
        }
    }

    private Set<Participant> validateParameters(Participant creditor,
                                                List<Participant> debtors,
                                                Money amount) {
        if (debtors.isEmpty())
            throw new IllegalArgumentException("No debtors");

        if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Debt is not positive");

        if (!participants.containsKey(creditor))
            throw new IllegalArgumentException(
                    "Debt contains unknown creditor: " + creditor);

        Set<Participant> uniqueDebtors = Set.copyOf(debtors);

        if (!participants.keySet().containsAll(uniqueDebtors))
            throw new IllegalArgumentException(
                    "Debt contains unknown debtors: " + uniqueDebtors);

        if (uniqueDebtors.size() != debtors.size())
            throw new IllegalArgumentException("Debtors contain duplicates : "
                    + debtors);

        return uniqueDebtors;
    }

    /**
     * Simplifies the debt structure. Returns a simplified version of the debts
     * with at most {@code n-1} payments.<br/>Pseudocode <i>(Khan, 2024)</i>:
     * <pre><code>
     *     Let G be a directed simple graph (V, E) in which the vertices are<!--
     *     --> participants and edges are debts.
     *     Let d be a min priority queue of debtors and their debt.
     *     Let c be a min priority queue of creditors and their credit.
     *     Let r be a collection of debts.
     *
     *     G := The unsimplified debt structure.
     *     d := ∅
     *     c := ∅
     *     r := ∅
     *
     *     Foreach v in V(G):
     *         Let m := Σw({u, v}) - Σw({v, u}).
     *         If m > 0:
     *             Enqueue m, paired with v, to c.
     *         If m < 0:
     *             Enqueue -m, paired with v, to d.
     *
     *     While c ≠ ∅:
     *         Poll c' from c.
     *         Poll d' from d.
     *         Let r' := c' - d'.
     *         Add min(c', d'), as a debt from d' to c', to r.
     *         If r' > 0:
     *             Enqueue r', paired with c', to c.
     *         If r' < 0:
     *             Enqueue -r', paired with d', to d.
     *
     *     r = The simplified debt structure.</code></pre>
     *
     * @param   base
     *          The currency in which the debt structure should be expressed. A
     *          base currency of sorts.
     *
     * @return  The simplified version of the debts.
     *
     * @author  Maurits Sloof
     * @author  Paras Khan
     */
    public Collection<Debt> simplify(Currency base) {
        creditors.clear();
        debtors.clear();
        setZeroMoneyInBaseCurrency(base);

        for (Participant participant : participants.keySet())
            enqueue(new ParticipantMoneyPair(
                    participant,
                    reduce(
                            participant,
                            participants.get(participant),
                            base
                    )
            ));

        return collapse(base);
    }

    /**
     * Collapses the graph into a simplified one.
     *
     * @param   base
     *          The {@link Currency} of the resulting graph.
     *
     * @return  The simplified graph.
     */
    private LinkedList<Debt> collapse(Currency base) {
        LinkedList<Debt> result = new LinkedList<>();

        while (!creditors.isEmpty()) {
            ParticipantMoneyPair creditor = creditors.poll();
            ParticipantMoneyPair debtor = debtors.poll();

            if (creditor == null || debtor == null)
                throw new NullPointerException("this shouldn't happen...");

            // simplify the cancel function
            if (creditor.money.equals(debtor.money)) {
                result.add(new Debt(debtor.participant, creditor.participant,
                        creditor.money));
                continue;
            }

            cancel(base, creditor, debtor, result);
        }
        return result;
    }

    /**
     * Cancels two debts against each other, adding the remainder to the
     * appropriate priority queue.
     *
     * @param   base
     *          The {@link Currency} of the resulting graph.
     * @param   creditor
     *          The creditor in the debt.
     * @param   debtor
     *          The debtor in the debt.
     * @param   result
     *          The {@link List} in which the resulting simplified version of
     *          the debts are stored.
     */
    private void cancel(Currency base, ParticipantMoneyPair creditor,
                        ParticipantMoneyPair debtor, List<Debt> result) {
        BigDecimal max = creditor.money().getAmount().max(
                debtor.money().getAmount());
        BigDecimal min = creditor.money().getAmount().min(
                debtor.money().getAmount());

        Money maxPayoffMoney = new Money(min, base);
        Money remainderMoney = new Money(max.subtract(min), base);

        result.add(new Debt(debtor.participant, creditor.participant,
                maxPayoffMoney));

        // re-enqueue undivided money
        if (creditor.money.equals(maxPayoffMoney))
            debtors.add(new ParticipantMoneyPair(debtor.participant,
                    remainderMoney));
        else
            creditors.add(new ParticipantMoneyPair(creditor.participant,
                    remainderMoney));
    }

    /**
     * Reduces a Participant to its monetary value.
     *
     * @param   participant
     *          The participant to reduce.
     * @param   transactions
     *          The transactions to and from the participant.
     * @param   base
     *          The {@link Currency} of the resulting monetary value.
     *
     * @return  The resulting monetary value as a {@link Money} object.
     */
    private Money reduce(Participant participant, LinkedList<Debt> transactions,
                         Currency base) {
        return normalise(calculate(participant, transactions), base);
    }

    /**
     * Calculates the netto debt/credit of a {@code Participant} as a {@code
     * HashMap} of the {@link Currency Currencies}.
     *
     * @param   participant
     *          The participant to consider.
     * @param   transactions
     *          The transactions linked to {@code participant} to consider.
     *
     * @return  The netto debt/credit.
     */
    private HashMap<Currency, Money> calculate(
            Participant participant, LinkedList<Debt> transactions) {

        // improve debts cancelling each other in non-base currencies
        HashMap<Currency, Money> debtsInCurrencies = new HashMap<>();

        for (Debt transaction : transactions) {
            Currency currency = transaction.amount.getCurrency();
            Money current = debtsInCurrencies.getOrDefault(currency,
                    new Money(BigDecimal.ZERO, currency));

            if (transaction.from.equals(participant)) {
                // subtract from (not yet) existing balance
                current.setAmount(current.getAmount().subtract(
                                    transaction.amount.getAmount()));
            } else {
                // add to (not yet) existing balance
                current.setAmount(current.getAmount().add(
                        transaction.amount.getAmount()));
            }
            debtsInCurrencies.put(currency, current);
        }
        return debtsInCurrencies;
    }

    /**
     * Normalises a {@code HashMap} of {@link Money} in different {@link
     * Currency Currencies} to a single {@code Money} object in a base {@code
     * Currency}.
     *
     * @param   debt
     *          The {@code HashMap} to consider.
     * @param   base
     *          The {@code Currency} of the result.
     *
     * @return  The resulting amount of {@code Money}.
     */
    private Money normalise(HashMap<Currency, Money> debt, Currency base) {
        Money result = new Money(BigDecimal.ZERO, base);
        for (Currency currency : debt.keySet())
            // add all converted money values, will raise a NullPointerException
            // if an exchange rate is unavailable, but that (throwing an
            // exception) is expected behaviour
            result.setAmount(result.getAmount().add(
                    exchangeRateFactory.getMostRecent(currency, base)
                            .convert(debt.get(currency))
                            .getAmount()
            ));
        return result;
    }

    /**
     * Enqueues a participant in the correct priority queue.
     *
     * @param   participantMoneyPair
     *          The participant to enqueue.
     */
    private void enqueue(ParticipantMoneyPair participantMoneyPair) {
        if (participantMoneyPair.compareTo(zeroMoneyInBaseCurrency) < 0) {
            // result is negative, aka a debt.
            // also, make the debt positive
            participantMoneyPair.money.setAmount(
                    participantMoneyPair.money.getAmount().negate());
            debtors.add(participantMoneyPair);
        }
        else if (participantMoneyPair.compareTo(zeroMoneyInBaseCurrency) > 0) {
            // result is positive, aka credits
            creditors.add(participantMoneyPair);
        }
        // else, remove the participant from the calculation (everything cancels
        // out)
    }

    private void setZeroMoneyInBaseCurrency(Currency base) {
        this.zeroMoneyInBaseCurrency = new ParticipantMoneyPair(null,
                new Money(BigDecimal.ZERO, base));
    }

    private record ParticipantMoneyPair(Participant participant, Money money)
            implements Comparable<ParticipantMoneyPair> {
        @Override
        public int compareTo(ParticipantMoneyPair other) {
            return this.money.compareTo(other.money());
        }
    }


    /**
     * This class is called {@code Debt} and not {@code Transaction} to prevent
     * name collisions with the {@link commons.Transaction} class all the time.
     * A payoff should be represented as a debt in the reverse direction.
     */
    public static class Debt {

        private final Participant from;
        private final Participant to;
        private final Money amount;

        /**
         * Creates an object storing the debt between two {@link Participant}s.
         *
         * @param   from
         *          The {@code Participant} owing the debt.
         * @param   to
         *          The {@code Participant} that should receive the payment.
         * @param   amount
         *          The debt.
         */
        public Debt(Participant from, Participant to, Money amount) {
            if (from == null || to == null || amount == null)
                throw new NullPointerException("argument is null");

            if (Objects.equals(from, to))
                throw new IllegalArgumentException(
                        "someone cannot owe themselves a debt");

            this.from = from;
            this.to = to;
            this.amount = amount;
        }

        /**
         * Gets the {@code Participant} owing the debt.
         *
         * @return  The {@code Participant} owing the debt.
         */
        public Participant getFrom() {
            return from;
        }

        /**
         * Gets the {@code Participant} that should receive the payment.
         *
         * @return  The {@code Participant} that should receive the payment.
         */
        public Participant getTo() {
            return to;
        }

        /**
         * Gets the amount of debt.
         *
         * @return  The amount of debt.
         */
        public Money getAmount() {
            return amount;
        }

        /**
         * Checks if {@code this} is equal to {@code other}.
         *
         * @param   other
         *          The object to check.
         *
         * @return  Whether {@code this} and {@code other} are equal.
         */
        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            Debt debt = (Debt) other;
            return Objects.equals(amount, debt.amount)
                    && Objects.equals(from, debt.from)
                    && Objects.equals(to, debt.to);
        }

        /**
         * Generates a hash code corresponding to {@code this}.
         *
         * @return  A hash value.
         */
        @Override
        public int hashCode() {
            return Objects.hash(from, to, amount);
        }

        /**
         * Generates a {@code String} representing {@code this}.
         *
         * @return  A {@code String} representing {@code this}.
         */
        @Override
        public String toString() {
            return "Debt { from " + from + " to " + to + " is " + amount + " }";
        }
    }
}
