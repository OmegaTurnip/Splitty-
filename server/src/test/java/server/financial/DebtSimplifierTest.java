package server.financial;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.financial.DebtSimplifier;
import server.financial.ExchangeRate;
import server.financial.ExchangeRateFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DebtSimplifierTest {

    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency USD = Currency.getInstance("USD");

    private DebtSimplifier debtSimplifier;
    private Event event;
    private LinkedList<Participant> participants;
    private ExchangeRateFactory exchangeRateFactory;

    private Participant invalidParticipant;

    private Money money1;
    private Money money2;

    private Debt debt1;
    private Debt debt2;
    private Debt debt3;

    private LocalDate today = LocalDate.now();

    @BeforeEach
    void setup() {
        event = new Event("test event");
        for (int i = 0; i < 7; i++) {
            event.addParticipant(new String[]{"Anna", "Bram", "Carin", "Deborah", "Esha", "Filip", "Greet"}[i]).setParticipantId((long) i);
        }

        participants = new LinkedList<>(event.getParticipants());

        exchangeRateFactory = new ExchangeRateFactory(null, null);
        exchangeRateFactory.addExchangeRate(new ExchangeRate(today, EUR, EUR, 1d));
        exchangeRateFactory.addExchangeRate(new ExchangeRate(today, USD, USD, 1d));

        debtSimplifier = new DebtSimplifier(exchangeRateFactory);
        debtSimplifier.setup(EUR, participants);

        invalidParticipant = event.addParticipant("Hannah");

        money1 = new Money(new BigDecimal(10), EUR);
        money2 = new Money(new BigDecimal(20), EUR);

        debt1 = new Debt(participants.get(0), participants.get(1), money1);
        debt2 = new Debt(participants.get(0), participants.get(1), money1); // == debt1
        debt3 = new Debt(participants.get(1), participants.get(2), money2);
    }

    @Test
    void constructor() {
        assertThrows(NullPointerException.class, () -> new DebtSimplifier(null));
        assertThrows(NullPointerException.class, () -> debtSimplifier.setup(null, participants));
        assertThrows(NullPointerException.class, () -> debtSimplifier.setup(EUR, null));
        assertThrows(IllegalArgumentException.class, () ->debtSimplifier.setup(EUR, new LinkedList<>()));
    }

    @Test
    void debtClass() {
        assertThrows(NullPointerException.class, () -> new Debt(null, participants.get(1), money1));
        assertThrows(NullPointerException.class, () -> new Debt(participants.get(2), null, money1));
        assertThrows(NullPointerException.class, () -> new Debt(participants.get(2), participants.get(1), null));

        assertThrows(IllegalArgumentException.class, () -> new Debt(participants.get(2), participants.get(1), new Money(new BigDecimal(-3), EUR)));

        assertThrows(IllegalArgumentException.class, () -> new Debt(participants.get(2), participants.get(2), money1));

        assertEquals(participants.get(0), debt1.from());
        assertEquals(participants.get(1), debt1.to());
        assertEquals(money1, debt1.amount());

        assertEquals(debt1, debt2);
        assertNotEquals(debt2, debt3);

        assertEquals(debt1.hashCode(), debt2.hashCode());
        assertEquals("Debt { from Participant { 'Anna' (id: 0) in the event 'test event' } to Participant { 'Bram' (id: 1) in the event 'test event' } is Money { 10.00 EUR } }",
                debt1.toString());
    }

    @Test
    void addDebt() {
        assertDoesNotThrow(() -> debtSimplifier.addDebt(debt1, today));
        assertDoesNotThrow(() -> debtSimplifier.addDebt(debt2, today));
        assertDoesNotThrow(() -> debtSimplifier.addDebt(debt3, today));

        assertThrows(NullPointerException.class, () -> debtSimplifier.addDebt((Debt) null, today));

        assertThrows(IllegalArgumentException.class, () -> debtSimplifier.addDebt(new Debt(invalidParticipant, participants.get(4), money2), today));
        assertThrows(IllegalArgumentException.class, () -> debtSimplifier.addDebt(new Debt(participants.get(4), invalidParticipant, money2), today));
    }

    @Test
    void divideDebtsAdding() {
        assertThrows(NullPointerException.class, () -> debtSimplifier.divideDebts(null, participants, new Money(new BigDecimal(participants.size()), EUR), today));
        assertThrows(NullPointerException.class, () -> debtSimplifier.divideDebts(participants.getFirst(), null, new Money(new BigDecimal(participants.size()), EUR), today));
        assertThrows(NullPointerException.class, () -> debtSimplifier.divideDebts(participants.getFirst(), participants, null, today));
        assertThrows(IllegalArgumentException.class, () -> debtSimplifier.divideDebts(participants.getFirst(), new LinkedList<>(), new Money(new BigDecimal(participants.size()), EUR), today));

        List<Participant> invalidParticipants = new LinkedList<>(participants);
        invalidParticipants.add(invalidParticipant);

        assertThrows(IllegalArgumentException.class, () -> debtSimplifier.divideDebts(participants.getFirst(), invalidParticipants, new Money(new BigDecimal(participants.size()), EUR), today));
        assertThrows(IllegalArgumentException.class, () -> debtSimplifier.divideDebts(invalidParticipant, participants, new Money(new BigDecimal(participants.size()), EUR), today));

        List<Participant> invalidParticipants2 = new LinkedList<>(participants);
        invalidParticipants2.add(participants.getFirst());

        assertThrows(IllegalArgumentException.class, () -> debtSimplifier.divideDebts(participants.getFirst(), invalidParticipants2, new Money(new BigDecimal(participants.size()), EUR), today));


        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), participants, new Money(new BigDecimal(participants.size()), EUR), today));
        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), participants, new Money(new BigDecimal(101), EUR), today));
        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), participants, new Money(new BigDecimal(24.49), EUR), today));

        exchangeRateFactory.addExchangeRate(new ExchangeRate(today, USD, EUR, 2));
        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), participants, new Money(new BigDecimal(24.49), USD), today));

        List<Participant> debtors = new LinkedList<>();
        debtors.add(participants.get(1));
        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(101), EUR), today));
        debtors.add(participants.get(2));
        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(42), EUR), today));
        debtors.add(participants.get(0));
        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(8), EUR), today));
        debtors.clear();
        debtors.add(participants.get(0));
        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(5364), EUR), today));
    }

    @Test
    void divideDebtsResult1() {
        List<Participant> debtors = new LinkedList<>();
        Set<Debt> expected = new HashSet<>();

        debtors.add(participants.get(1));
        expected.add(new Debt(
                participants.get(1),
                participants.get(0),
                new Money(new BigDecimal(10), EUR)
        ));

        debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(10), EUR), today);
        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void divideDebtsResult2() {
        List<Participant> debtors = new LinkedList<>();
        Set<Debt> expected = new HashSet<>();

        debtors.add(participants.get(1));
        debtors.add(participants.get(2));

        expected.add(new Debt(
                participants.get(1),
                participants.get(0),
                new Money(new BigDecimal(5), EUR)
        ));
        expected.add(new Debt(
                participants.get(2),
                participants.get(0),
                new Money(new BigDecimal(5), EUR)
        ));

        debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(10), EUR), today);
        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void divideDebtsResult3() {
        List<Participant> debtors = new LinkedList<>();
        Set<Debt> expected = new HashSet<>();

        debtors.add(participants.get(1));
        debtors.add(participants.get(2));
        debtors.add(participants.get(3));

        expected.add(new Debt(
                participants.get(2),
                participants.get(0),
                new Money(new BigDecimal(3.33), EUR)
        ));
        expected.add(new Debt(
                participants.get(3),
                participants.get(0),
                new Money(new BigDecimal(3.33), EUR)
        ));
        expected.add(new Debt(
                participants.get(1),
                participants.get(0),
                new Money(new BigDecimal(3.34), EUR)
        ));


        debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(10), EUR), today);
        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void divideDebtsResult4() {
        List<Participant> debtors = new LinkedList<>();
        Set<Debt> expected = new HashSet<>();

        debtors.add(participants.get(1));
        debtors.add(participants.get(2));
        debtors.add(participants.get(3));

        exchangeRateFactory.addExchangeRate(new ExchangeRate(today, USD, EUR, 1.1));

        expected.add(new Debt(
                participants.get(2),
                participants.get(0),
                new Money(new BigDecimal(3.66), EUR)
        ));
        expected.add(new Debt(
                participants.get(3),
                participants.get(0),
                new Money(new BigDecimal(3.67), EUR)
        ));
        expected.add(new Debt(
                participants.get(1),
                participants.get(0),
                new Money(new BigDecimal(3.67), EUR)
        ));

        debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(10), USD), today);
        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void divideDebtsResult5() {
        List<Participant> debtors = new LinkedList<>();
        Set<Debt> expected = new HashSet<>();

        debtors.add(participants.get(1));
        debtors.add(participants.get(2));
        debtors.add(participants.get(3));

        exchangeRateFactory.addExchangeRate(new ExchangeRate(today, USD, EUR, 1.1));

        expected.add(new Debt(
                participants.get(1),
                participants.get(0),
                new Money(new BigDecimal(7d), EUR)
        ));
        expected.add(new Debt(
                participants.get(3),
                participants.get(0),
                new Money(new BigDecimal(7d), EUR)
        ));
        expected.add(new Debt(
                participants.get(2),
                participants.get(0),
                new Money(new BigDecimal(7d), EUR)
        ));

        debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(10), EUR), today);
        debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(10), USD), today);
        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify0() {
        Set<Debt> expected = new HashSet<>();

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify1() {
        debtSimplifier.addDebt(debt1, today);

        Set<Debt> expected = new HashSet<>();

        expected.add(debt1);

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify2() {
        debtSimplifier.addDebt(debt1, today);
        debtSimplifier.addDebt(debt2, today);
        debtSimplifier.addDebt(debt3, today);

        Set<Debt> expected = new HashSet<>();

        expected.add(new Debt(
                participants.get(0),
                participants.get(2),
                money2
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify3() {
        final int eur_to_usd = 2;
        exchangeRateFactory.addExchangeRate(new ExchangeRate(today, EUR, USD, eur_to_usd));

        debtSimplifier = new DebtSimplifier(exchangeRateFactory);
        debtSimplifier.setup(USD, event.getParticipants());

        debtSimplifier.addDebt(debt1, today);
        debtSimplifier.addDebt(debt2, today);
        debtSimplifier.addDebt(debt3, today);

        Set<Debt> expected = new HashSet<>();

        expected.add(new Debt(
                participants.get(0),
                participants.get(2),
                exchangeRateFactory.getMostRecent(EUR, USD).convert(money2)
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify4() {
        debtSimplifier.addDebt(new Debt(participants.get(0), participants.get(1), money1), today);
        debtSimplifier.addDebt(new Debt(participants.get(1), participants.get(2), money1), today);
        debtSimplifier.addDebt(new Debt(participants.get(2), participants.get(3), money1), today);
        debtSimplifier.addDebt(new Debt(participants.get(3), participants.get(4), money1), today);
        debtSimplifier.addDebt(new Debt(participants.get(4), participants.get(5), money1), today);

        Set<Debt> expected = new HashSet<>();

        expected.add(new Debt(
                participants.get(0),
                participants.get(5),
                money1
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify5() {
        debtSimplifier.addDebt(new Debt(participants.get(0), participants.get(1), money1), today);
        debtSimplifier.addDebt(new Debt(participants.get(1), participants.get(2), money1), today);
        debtSimplifier.addDebt(new Debt(participants.get(2), participants.get(3), money1), today);
        debtSimplifier.addDebt(new Debt(participants.get(3), participants.get(4), money1), today);
        debtSimplifier.addDebt(new Debt(participants.get(4), participants.get(5), money1), today);

        debtSimplifier.addDebt(new Debt(participants.get(1), participants.get(4), money2), today);

        Set<Debt> expected = new HashSet<>();

        expected.add(new Debt(
                participants.get(0),
                participants.get(5),
                money1
        ));

        expected.add(new Debt(
                participants.get(1),
                participants.get(4),
                money2
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify6() {
        debtSimplifier.addDebt(new Debt(participants.get(0), participants.get(1), money1), today);
        debtSimplifier.addDebt(new Debt(participants.get(1), participants.get(2), money1), today);

        debtSimplifier.addDebt(new Debt(participants.get(2), participants.get(3), money2), today);

        debtSimplifier.addDebt(new Debt(participants.get(3), participants.get(4), money1), today);
        debtSimplifier.addDebt(new Debt(participants.get(4), participants.get(5), money1), today);


        Set<Debt> expected = new HashSet<>();

        expected.add(new Debt(
                participants.get(0),
                participants.get(3),
                money1
        ));

        expected.add(new Debt(
                participants.get(2),
                participants.get(5),
                money1
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify7() {
        debtSimplifier.addDebt(new Debt(participants.get(0), participants.get(1), money2), today);
        debtSimplifier.addDebt(new Debt(participants.get(1), participants.get(2), money1), today);

        Set<Debt> expected = new HashSet<>();

        expected.add(new Debt(
                participants.get(0),
                participants.get(1),
                money1
        ));

        expected.add(new Debt(
                participants.get(0),
                participants.get(2),
                money1
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify8() {
        debtSimplifier.addDebt(new Debt(participants.get(0), participants.get(1), money1), today);
        debtSimplifier.addDebt(new Debt(participants.get(1), participants.get(2), money2), today);

        Set<Debt> expected = new HashSet<>();

        expected.add(new Debt(
                participants.get(0),
                participants.get(2),
                money1
        ));

        expected.add(new Debt(
                participants.get(1),
                participants.get(2),
                money1
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify9() {
        debtSimplifier.addDebt(new Debt(participants.get(0), participants.get(1), new Money(new BigDecimal(10), EUR)), today);
        debtSimplifier.addDebt(new Debt(participants.get(1), participants.get(2), new Money(new BigDecimal(20), EUR)), today);
        debtSimplifier.addDebt(new Debt(participants.get(3), participants.get(2), new Money(new BigDecimal(30), EUR)), today);
        debtSimplifier.addDebt(new Debt(participants.get(1), participants.get(0), new Money(new BigDecimal(20), EUR)), today);
        debtSimplifier.addDebt(new Debt(participants.get(6), participants.get(3), new Money(new BigDecimal(15), EUR)), today);
        debtSimplifier.addDebt(new Debt(participants.get(0), participants.get(6), new Money(new BigDecimal( 5), EUR)), today);

        Set<Debt> expected = new HashSet<>();

        expected.add(new Debt(
                participants.get(6),
                participants.get(0),
                new Money(new BigDecimal(5), EUR)
        ));

        expected.add(new Debt(
                participants.get(6),
                participants.get(2),
                new Money(new BigDecimal(5), EUR)
        ));

        expected.add(new Debt(
                participants.get(3),
                participants.get(2),
                new Money(new BigDecimal(15), EUR)
        ));

        expected.add(new Debt(
                participants.get(1),
                participants.get(2),
                new Money(new BigDecimal(30), EUR)
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplifyCycle0() {
        debtSimplifier.addDebt(new Debt(participants.get(0), participants.get(1), money1), today);
        debtSimplifier.addDebt(new Debt(participants.get(1), participants.get(0), money1), today);
        assertEquals(new HashSet<>(), debtSimplifier.simplify());
    }

    @Test
    void simplifyCycle1() {
        debtSimplifier.addDebt(new Debt(participants.get(0), participants.get(1), money1), today);
        debtSimplifier.addDebt(new Debt(participants.get(1), participants.get(2), money1), today);
        debtSimplifier.addDebt(new Debt(participants.get(2), participants.get(0), money1), today);
        assertEquals(new HashSet<>(), debtSimplifier.simplify());
    }

    @Test
    void simplifyCycle2() {
        final double eur_to_usd = 1.5;

        exchangeRateFactory.addExchangeRate(new ExchangeRate(today, EUR, USD, eur_to_usd));
        exchangeRateFactory.addExchangeRate(new ExchangeRate(today, USD, EUR, 1/eur_to_usd));

        debtSimplifier.addDebt(new Debt(participants.get(0), participants.get(1), new Money(new BigDecimal(2), EUR)), today);
        debtSimplifier.addDebt(new Debt(participants.get(1), participants.get(2), new Money(new BigDecimal(3), USD)), today);
        debtSimplifier.addDebt(new Debt(participants.get(2), participants.get(0), new Money(new BigDecimal(2), EUR)), today);

        assertEquals(new HashSet<>(), debtSimplifier.simplify());
    }

    @Test
    void simplifyCycle2_1() {
        debtSimplifier = new DebtSimplifier(exchangeRateFactory);
        debtSimplifier.setup(USD, event.getParticipants());


        final double eur_to_usd = 1.5;

        exchangeRateFactory.addExchangeRate(new ExchangeRate(today, EUR, USD, eur_to_usd));
        exchangeRateFactory.addExchangeRate(new ExchangeRate(today, USD, EUR, 1/eur_to_usd));

        debtSimplifier.addDebt(new Debt(participants.get(0), participants.get(1), new Money(new BigDecimal(2), EUR)), today);
        debtSimplifier.addDebt(new Debt(participants.get(1), participants.get(2), new Money(new BigDecimal(3), USD)), today);
        debtSimplifier.addDebt(new Debt(participants.get(2), participants.get(0), new Money(new BigDecimal(2), EUR)), today);

        assertEquals(new HashSet<>(), debtSimplifier.simplify());
    }


    @Test
    void simplifyCycle3() {
        debtSimplifier.addDebt(new Debt(participants.get(0), participants.get(1), new Money(new BigDecimal(30), EUR)), today);
        debtSimplifier.addDebt(new Debt(participants.get(1), participants.get(2), new Money(new BigDecimal(20), EUR)), today);
        debtSimplifier.addDebt(new Debt(participants.get(2), participants.get(0), new Money(new BigDecimal(30), EUR)), today);


        Set<Debt> expected = new HashSet<>();

        expected.add(new Debt(
                participants.get(2),
                participants.get(1),
                new Money(new BigDecimal(10), EUR)
        ));
        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void sumOfExpenses() {
        assertThrows(NullPointerException.class, () -> debtSimplifier.sumOfExpenses(null, EUR));
        assertThrows(NullPointerException.class, () -> debtSimplifier.sumOfExpenses(event, null));

        assertEquals(new Money(new BigDecimal(0), EUR), debtSimplifier.sumOfExpenses(event, EUR));

        event.addTransaction(Transaction.createDebt(participants.get(0), "thee", new Money(new BigDecimal(10), EUR), participants, event, null));

        assertEquals(new Money(new BigDecimal(10), EUR), debtSimplifier.sumOfExpenses(event, EUR));

        event.addTransaction(Transaction.createPayoff(participants.get(0), "thee", new Money(new BigDecimal(10), EUR), participants.get(2), event, null));

        assertEquals(new Money(new BigDecimal(10), EUR), debtSimplifier.sumOfExpenses(event, EUR));

        event.addTransaction(Transaction.createDebt(participants.get(0), "coffee", new Money(new BigDecimal(5), EUR), participants, event, null));

        assertEquals(new Money(new BigDecimal(15), EUR), debtSimplifier.sumOfExpenses(event, EUR));

        event.addTransaction(Transaction.createDebt(participants.get(0), "coffee", new Money(new BigDecimal(5), Currency.getInstance("IDR")), participants, event, null));

        assertNull(debtSimplifier.sumOfExpenses(event, EUR));
    }

}
