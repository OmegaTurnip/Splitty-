package server.util;

import commons.Money;
import commons.Participant;
import commons.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class DebtSimplifier {

    private final ExchangeRateFactory exchangeRateFactory;

    private final HashMap<Participant, LinkedList<Debt>> participants;
    private final HashMap<Participant, Integer> centsPayedExtra;

    // these are automatically min-heaps, yay!
    private final PriorityQueue<ParticipantMoneyPair> creditors =
            new PriorityQueue<>();
    private final PriorityQueue<ParticipantMoneyPair> debtors =
            new PriorityQueue<>();

    private final Currency base;

    private final ParticipantMoneyPair zeroMoneyInBaseCurrency;

    private final BigDecimal cent;

    /**
     * Creates a new {@code DebtSimplifier} object.
     *
     * @param   participants
     *          The participants to divide the debts over, duplicates are
     *          ignored.
     * @param   base
     *          The currency in which the debt structure should be expressed. A
     *          base currency of sorts.
     */
    public DebtSimplifier(Collection<Participant> participants, Currency base) {
        this(participants, base, ExchangeRateFactory.get());
    }

    /**
     * Creates a new {@code DebtSimplifier} object. Used for testing.
     *
     * @param   participants
     *          The participants to divide the debts over, duplicates are
     *          ignored.
     * @param   base
     *          The currency in which the debt structure should be expressed. A
     *          base currency of sorts.
     * @param   exchangeRateFactory
     *          The {@link ExchangeRateFactory}s used by this simplifier.
     */
    DebtSimplifier(Collection<Participant> participants, Currency base,
                   ExchangeRateFactory exchangeRateFactory) {
        if (base == null)
            throw new NullPointerException("base is null");

        if (participants == null)
            throw new NullPointerException("participants is null");

        if (exchangeRateFactory == null)
            throw new NullPointerException("exchangeRateFactory is null");

        if (participants.isEmpty())
            throw new IllegalArgumentException("participants is empty");

        this.participants = new HashMap<>();
        this.centsPayedExtra = new HashMap<>();
        this.base = base;
        this.zeroMoneyInBaseCurrency = new ParticipantMoneyPair(null,
                new Money(BigDecimal.ZERO, base)
        );
        this.cent =
                BigDecimal.ONE.movePointLeft(base.getDefaultFractionDigits());
        for (Participant participant : participants) {
            this.participants.put(participant, new LinkedList<>());
            this.centsPayedExtra.put(participant, 0);
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

        if (!participants.containsKey(debt.from()))
            throw new IllegalArgumentException(
                    "Debt contains unknown participant (from): " + debt);

        if (!participants.containsKey(debt.to()))
            throw new IllegalArgumentException(
                    "Debt contains unknown participant (to): " + debt);

        Debt converted =
                new Debt(debt.from, debt.to, convertToBase(debt.amount));

        participants.get(converted.to()).add(converted);
        participants.get(converted.from()).add(converted);
    }

    /**
     * Adds a transaction that should be taken into account in the calculation.
     * Throws {@link NullPointerException} if the transaction is {@code null}
     * and {@link IllegalArgumentException} if the transaction is between a
     * participant not present in the calculation.
     *
     * @param   transaction
     *          A transaction that should be taken into account in the
     *          calculation.
     */
    public void addDebt(Transaction transaction) {
        if (transaction == null)
            throw new NullPointerException();

        if (transaction.isPayoff())
            addDebt(new Debt(
                    // swap payer and receiver
                    transaction.getParticipants().getFirst(),
                    transaction.getPayer(),
                    transaction.getAmount()
            ));
        else
            divideDebts(
                    transaction.getPayer(),
                    transaction.getParticipants(),
                    transaction.getAmount()
            );
    }

    /**
     * Divides the specified amount over the debtors, paid by the creditor.
     * Distributes remainder evenly.
     *
     * @param   creditor
     *          The participant to which the debt should be paid.
     * @param   debtors
     *          The participants that owe (part of) the debt. <em>Can also
     *          include creditor.</em>
     * @param   amount
     *          The amount of money to divide.
     */
    public void divideDebts(Participant creditor,
                            Collection<Participant> debtors, Money amount) {
        Set<Participant> uniqueDebtors =
                validateParameters(creditor, debtors, amount);

        Money convertedAmount = convertToBase(amount);
        BigDecimal remainder = getRemainder(convertedAmount, uniqueDebtors);
        BigDecimal fraction = getFraction(convertedAmount, remainder,
                uniqueDebtors);

        int cents = remainder.movePointRight(
                base.getDefaultFractionDigits()).intValue();

        List<Participant> extraCentPayers =
                getNextExtraCentPayers(cents, uniqueDebtors);

        for (Participant debtor : uniqueDebtors) {
            if (!Objects.equals(creditor, debtor))  // the creditor already paid
                addDebt(new Debt(debtor, creditor,
                        new Money(
                                extraCentPayers.contains(debtor) ?
                                        fraction.add(cent) : fraction,
                                base
                        )
                ));
        }
    }

    private record ParticipantCentPair(Participant participant, int cents)
            implements Comparable<ParticipantCentPair> {
        @Override
        public int compareTo(ParticipantCentPair other) {
            return Integer.compare(this.cents, other.cents);
        }
    }

    private List<Participant> getNextExtraCentPayers(int extraCents,
                                                    Set<Participant> debtors) {
        List<ParticipantCentPair> participantCentHistory =
                debtors.stream()
                        // quick and dirty hack to make result deterministic
                        .sorted(Comparator.comparingLong(Participant::getId))
                        .map(d -> new ParticipantCentPair(d,
                                centsPayedExtra.get(d)))
                        .toList();

        PriorityQueue<ParticipantCentPair> cents =
                new PriorityQueue<>(participantCentHistory);

        List<Participant> result = new LinkedList<>();

        for (int i = 0; i < extraCents; i++) {
            ParticipantCentPair nextParticipant = cents.poll();

            if (nextParticipant == null)
                throw new NullPointerException("this shouldn't happen");

            Participant participant = nextParticipant.participant;

            centsPayedExtra.put(participant,
                    centsPayedExtra.get(participant) + 1);
            result.add(nextParticipant.participant);
        }

        return result;
    }

    private static BigDecimal getFraction(Money convertedAmount,
                                          BigDecimal remainder,
                                          Set<Participant> uniqueDebtors) {
        return convertedAmount.getAmount().subtract(remainder).divide(
                new BigDecimal(uniqueDebtors.size()), RoundingMode.DOWN);
    }

    private BigDecimal getRemainder(Money convertedAmount,
                                    Set<Participant> uniqueDebtors) {
        return convertedAmount.getAmount().remainder(
                (new BigDecimal(uniqueDebtors.size())).movePointLeft(
                        base.getDefaultFractionDigits()));
    }

    private Money convertToBase(Money amount) {
        // Will raise a NullPointerException if an exchange rate is unavailable,
        // but that (throwing an exception) is expected behaviour
        return new Money(
                exchangeRateFactory.getMostRecent(amount.getCurrency(), base)
                        .convert(amount)
                        .getAmount(),
                base
        );
    }

    private Set<Participant> validateParameters(Participant creditor,
                                                Collection<Participant> debtors,
                                                Money amount) {
        boundCheck(creditor, debtors, amount);

        if (!participants.containsKey(creditor))
            throw new IllegalArgumentException(
                    "Debt contains unknown creditor: " + creditor);

        Set<Participant> uniqueDebtors = Set.copyOf(debtors);

        if (!participants.keySet().containsAll(uniqueDebtors))
            throw new IllegalArgumentException("Debt contains unknown debtors: "
                    + uniqueDebtors);

        if (uniqueDebtors.size() != debtors.size())
            throw new IllegalArgumentException("Debtors contain duplicates: "
                    + debtors);

        return uniqueDebtors;
    }

    private static void boundCheck(Participant creditor,
                                   Collection<Participant> debtors,
                                   Money amount) {
        if (creditor == null)
            throw new NullPointerException("creditor is null");

        if (amount == null)
            throw new NullPointerException("amount is null");

        if (debtors == null)
            throw new NullPointerException("debtors is null");

        if (debtors.isEmpty())
            throw new IllegalArgumentException("No debtors");
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
     *         Let m := Σw((u, v)) - Σw((v, u)).
     *         If m > 0:
     *             Enqueue m, paired with v, to c.
     *         If m < 0:
     *             Enqueue -m, paired with v, to d.
     *
     *     While c ≠ ∅ and d ≠ ∅:
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
     * @return  The simplified version of the debts.
     *
     * @author  Maurits Sloof
     * @author  Paras Khan
     */
    public Collection<Debt> simplify() {
        creditors.clear();
        debtors.clear();

        for (Participant participant : participants.keySet())
            enqueue(new ParticipantMoneyPair(
                    participant,
                    reduce(
                            participant,
                            participants.get(participant)
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

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
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
     *
     * @return  The resulting monetary value as a {@link Money} object.
     */
    private Money reduce(Participant participant,
                         LinkedList<Debt> transactions) {
        Money result = new Money(BigDecimal.ZERO, base);

        for (Debt transaction : transactions) {
            if (transaction.from.equals(participant))
                // subtract from existing balance
                result.setAmount(result.getAmount().subtract(
                        transaction.amount.getAmount()));
            else
                // add to existing balance
                result.setAmount(result.getAmount().add(
                        transaction.amount.getAmount()));
        }

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

    private record ParticipantMoneyPair(Participant participant, Money money)
            implements Comparable<ParticipantMoneyPair> {
        @Override
        public int compareTo(ParticipantMoneyPair other) {
            return this.money.compareTo(other.money());
        }
    }

    /**
     * This class is called {@code Debt} and not {@code Transaction} to prevent
     * name collisions with the {@link Transaction} class all the time.
     * A payoff should be represented as a debt in the reverse direction.
     *
     * @param   from
     *          The {@code Participant} owing the debt.
     * @param   to
     *          The {@code Participant} that should receive the payment.
     * @param   amount
     *          The debt.
     */
    public record Debt(Participant from, Participant to, Money amount) {

        /**
         * Creates an object storing the debt between two {@link Participant}s.
         */
        public Debt {
            if (from == null || to == null || amount == null)
                throw new NullPointerException("argument is null");

            if (Objects.equals(from, to))
                throw new IllegalArgumentException(
                        "someone cannot owe themselves a debt");

            if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0)
                throw new IllegalArgumentException("Debt is not positive");
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
