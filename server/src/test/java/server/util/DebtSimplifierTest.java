package server.util;

import commons.Event;
import commons.Money;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;

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

    private DebtSimplifier.Debt debt1;
    private DebtSimplifier.Debt debt2;
    private DebtSimplifier.Debt debt3;

    @BeforeEach
    void setup() {
        event = new Event("test event");
        for (int i = 0; i < 7; i++) {
            event.addParticipant(new String[]{"Anna", "Bram", "Carin", "Deborah", "Esha", "Filip", "Greet"}[i]).setId((long) i);
        }

        participants = new LinkedList<>(event.getParticipants());

        exchangeRateFactory = new ExchangeRateFactory(null);
        exchangeRateFactory.addExchangeRate(new ExchangeRate(LocalDate.now(), EUR, EUR, 1d));
        exchangeRateFactory.addExchangeRate(new ExchangeRate(LocalDate.now(), USD, USD, 1d));

        debtSimplifier = new DebtSimplifier(event.getParticipants(), EUR,  exchangeRateFactory);

        invalidParticipant = event.addParticipant("Hannah");

        money1 = new Money(new BigDecimal(10), EUR);
        money2 = new Money(new BigDecimal(20), EUR);

        debt1 = new DebtSimplifier.Debt(participants.get(0), participants.get(1), money1);
        debt2 = new DebtSimplifier.Debt(participants.get(0), participants.get(1), money1); // == debt1
        debt3 = new DebtSimplifier.Debt(participants.get(1), participants.get(2), money2);
    }

    @Test
    void constructor() {
        assertThrows(NullPointerException.class, () -> new DebtSimplifier(null, EUR));
        assertThrows(NullPointerException.class, () -> new DebtSimplifier(new LinkedList<>(), null));
        assertThrows(IllegalArgumentException.class, () -> new DebtSimplifier(new LinkedList<>(), EUR));
    }

    @Test
    void debtClass() {
        assertThrows(NullPointerException.class, () -> new DebtSimplifier.Debt(null, participants.get(1), money1));
        assertThrows(NullPointerException.class, () -> new DebtSimplifier.Debt(participants.get(2), null, money1));
        assertThrows(NullPointerException.class, () -> new DebtSimplifier.Debt(participants.get(2), participants.get(1), null));

        assertThrows(IllegalArgumentException.class, () -> new DebtSimplifier.Debt(participants.get(2), participants.get(1), new Money(new BigDecimal(-3), EUR)));

        assertThrows(IllegalArgumentException.class, () -> new DebtSimplifier.Debt(participants.get(2), participants.get(2), money1));

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
        assertDoesNotThrow(() -> debtSimplifier.addDebt(debt1));
        assertDoesNotThrow(() -> debtSimplifier.addDebt(debt2));
        assertDoesNotThrow(() -> debtSimplifier.addDebt(debt3));

        assertThrows(NullPointerException.class, () -> debtSimplifier.addDebt((DebtSimplifier.Debt) null));

        assertThrows(IllegalArgumentException.class, () -> debtSimplifier.addDebt(new DebtSimplifier.Debt(invalidParticipant, participants.get(4), money2)));
        assertThrows(IllegalArgumentException.class, () -> debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(4), invalidParticipant, money2)));
    }

    @Test
    void divideDebtsAdding() {
        assertThrows(NullPointerException.class, () -> debtSimplifier.divideDebts(null, participants, new Money(new BigDecimal(participants.size()), EUR)));
        assertThrows(NullPointerException.class, () -> debtSimplifier.divideDebts(participants.getFirst(), null, new Money(new BigDecimal(participants.size()), EUR)));
        assertThrows(NullPointerException.class, () -> debtSimplifier.divideDebts(participants.getFirst(), participants, null));
        assertThrows(IllegalArgumentException.class, () -> debtSimplifier.divideDebts(participants.getFirst(), new LinkedList<>(), new Money(new BigDecimal(participants.size()), EUR)));

        List<Participant> invalidParticipants = new LinkedList<>(participants);
        invalidParticipants.add(invalidParticipant);

        assertThrows(IllegalArgumentException.class, () -> debtSimplifier.divideDebts(participants.getFirst(), invalidParticipants, new Money(new BigDecimal(participants.size()), EUR)));
        assertThrows(IllegalArgumentException.class, () -> debtSimplifier.divideDebts(invalidParticipant, participants, new Money(new BigDecimal(participants.size()), EUR)));

        List<Participant> invalidParticipants2 = new LinkedList<>(participants);
        invalidParticipants2.add(participants.getFirst());

        assertThrows(IllegalArgumentException.class, () -> debtSimplifier.divideDebts(participants.getFirst(), invalidParticipants2, new Money(new BigDecimal(participants.size()), EUR)));


        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), participants, new Money(new BigDecimal(participants.size()), EUR)));
        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), participants, new Money(new BigDecimal(101), EUR)));
        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), participants, new Money(new BigDecimal(24.49), EUR)));

        exchangeRateFactory.addExchangeRate(new ExchangeRate(LocalDate.now(), USD, EUR, 2));
        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), participants, new Money(new BigDecimal(24.49), USD)));

        List<Participant> debtors = new LinkedList<>();
        debtors.add(participants.get(1));
        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(101), EUR)));
        debtors.add(participants.get(2));
        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(42), EUR)));
        debtors.add(participants.get(0));
        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(8), EUR)));
        debtors.clear();
        debtors.add(participants.get(0));
        assertDoesNotThrow(() -> debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(5364), EUR)));
    }

    @Test
    void divideDebtsResult1() {
        List<Participant> debtors = new LinkedList<>();
        List<DebtSimplifier.Debt> excepted = new LinkedList<>();

        debtors.add(participants.get(1));
        excepted.add(new DebtSimplifier.Debt(
                participants.get(1),
                participants.get(0),
                new Money(new BigDecimal(10), EUR)
        ));

        debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(10), EUR));
        assertEquals(excepted, debtSimplifier.simplify());
    }

    @Test
    void divideDebtsResult2() {
        List<Participant> debtors = new LinkedList<>();
        List<DebtSimplifier.Debt> excepted = new LinkedList<>();

        debtors.add(participants.get(1));
        debtors.add(participants.get(2));

        excepted.add(new DebtSimplifier.Debt(
                participants.get(1),
                participants.get(0),
                new Money(new BigDecimal(5), EUR)
        ));
        excepted.add(new DebtSimplifier.Debt(
                participants.get(2),
                participants.get(0),
                new Money(new BigDecimal(5), EUR)
        ));

        debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(10), EUR));
        assertEquals(excepted, debtSimplifier.simplify());
    }

    @Test
    void divideDebtsResult3() {
        List<Participant> debtors = new LinkedList<>();
        List<DebtSimplifier.Debt> excepted = new LinkedList<>();

        debtors.add(participants.get(1));
        debtors.add(participants.get(2));
        debtors.add(participants.get(3));

        excepted.add(new DebtSimplifier.Debt(
                participants.get(2),
                participants.get(0),
                new Money(new BigDecimal(3.33), EUR)
        ));
        excepted.add(new DebtSimplifier.Debt(
                participants.get(3),
                participants.get(0),
                new Money(new BigDecimal(3.33), EUR)
        ));
        excepted.add(new DebtSimplifier.Debt(
                participants.get(1),
                participants.get(0),
                new Money(new BigDecimal(3.34), EUR)
        ));


        debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(10), EUR));
        assertEquals(excepted, debtSimplifier.simplify());
    }

    @Test
    void divideDebtsResult4() {
        List<Participant> debtors = new LinkedList<>();
        List<DebtSimplifier.Debt> excepted = new LinkedList<>();

        debtors.add(participants.get(1));
        debtors.add(participants.get(2));
        debtors.add(participants.get(3));

        exchangeRateFactory.addExchangeRate(new ExchangeRate(LocalDate.now(), USD, EUR, 1.1));

        excepted.add(new DebtSimplifier.Debt(
                participants.get(2),
                participants.get(0),
                new Money(new BigDecimal(3.66), EUR)
        ));
        excepted.add(new DebtSimplifier.Debt(
                participants.get(3),
                participants.get(0),
                new Money(new BigDecimal(3.67), EUR)
        ));
        excepted.add(new DebtSimplifier.Debt(
                participants.get(1),
                participants.get(0),
                new Money(new BigDecimal(3.67), EUR)
        ));

        debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(10), USD));
        assertEquals(excepted, debtSimplifier.simplify());
    }

    @Test
    void divideDebtsResult5() {
        List<Participant> debtors = new LinkedList<>();
        List<DebtSimplifier.Debt> excepted = new LinkedList<>();

        debtors.add(participants.get(1));
        debtors.add(participants.get(2));
        debtors.add(participants.get(3));

        exchangeRateFactory.addExchangeRate(new ExchangeRate(LocalDate.now(), USD, EUR, 1.1));

        excepted.add(new DebtSimplifier.Debt(
                participants.get(1),
                participants.get(0),
                new Money(new BigDecimal(7d), EUR)
        ));
        excepted.add(new DebtSimplifier.Debt(
                participants.get(3),
                participants.get(0),
                new Money(new BigDecimal(7d), EUR)
        ));
        excepted.add(new DebtSimplifier.Debt(
                participants.get(2),
                participants.get(0),
                new Money(new BigDecimal(7d), EUR)
        ));

        debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(10), EUR));
        debtSimplifier.divideDebts(participants.getFirst(), debtors, new Money(new BigDecimal(10), USD));
        assertEquals(excepted, debtSimplifier.simplify());
    }

    @Test
    void simplify0() {
        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify1() {
        debtSimplifier.addDebt(debt1);

        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        expected.add(debt1);

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify2() {
        debtSimplifier.addDebt(debt1);
        debtSimplifier.addDebt(debt2);
        debtSimplifier.addDebt(debt3);

        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        expected.add(new DebtSimplifier.Debt(
                participants.get(0),
                participants.get(2),
                money2
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify3() {
        final int eur_to_usd = 2;
        exchangeRateFactory.addExchangeRate(new ExchangeRate(LocalDate.now(), EUR, USD, eur_to_usd));

        debtSimplifier = new DebtSimplifier(event.getParticipants(), USD,  exchangeRateFactory);
        debtSimplifier.addDebt(debt1);
        debtSimplifier.addDebt(debt2);
        debtSimplifier.addDebt(debt3);

        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        expected.add(new DebtSimplifier.Debt(
                participants.get(0),
                participants.get(2),
                exchangeRateFactory.getMostRecent(EUR, USD).convert(money2)
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify4() {
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(1), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(2), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(2), participants.get(3), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(3), participants.get(4), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(4), participants.get(5), money1));

        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        expected.add(new DebtSimplifier.Debt(
                participants.get(0),
                participants.get(5),
                money1
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify5() {
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(1), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(2), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(2), participants.get(3), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(3), participants.get(4), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(4), participants.get(5), money1));

        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(4), money2));

        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        expected.add(new DebtSimplifier.Debt(
                participants.get(0),
                participants.get(5),
                money1
        ));

        expected.add(new DebtSimplifier.Debt(
                participants.get(1),
                participants.get(4),
                money2
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify6() {
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(1), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(2), money1));

        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(2), participants.get(3), money2));

        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(3), participants.get(4), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(4), participants.get(5), money1));


        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        expected.add(new DebtSimplifier.Debt(
                participants.get(0),
                participants.get(3),
                money1
        ));

        expected.add(new DebtSimplifier.Debt(
                participants.get(2),
                participants.get(5),
                money1
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify7() {
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(1), money2));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(2), money1));

        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        expected.add(new DebtSimplifier.Debt(
                participants.get(0),
                participants.get(1),
                money1
        ));

        expected.add(new DebtSimplifier.Debt(
                participants.get(0),
                participants.get(2),
                money1
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify8() {
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(1), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(2), money2));

        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        expected.add(new DebtSimplifier.Debt(
                participants.get(0),
                participants.get(2),
                money1
        ));

        expected.add(new DebtSimplifier.Debt(
                participants.get(1),
                participants.get(2),
                money1
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplify9() {
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(1), new Money(new BigDecimal(10), EUR)));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(2), new Money(new BigDecimal(20), EUR)));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(3), participants.get(2), new Money(new BigDecimal(30), EUR)));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(0), new Money(new BigDecimal(20), EUR)));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(6), participants.get(3), new Money(new BigDecimal(15), EUR)));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(6), new Money(new BigDecimal( 5), EUR)));

        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        expected.add(new DebtSimplifier.Debt(
                participants.get(6),
                participants.get(0),
                new Money(new BigDecimal(5), EUR)
        ));

        expected.add(new DebtSimplifier.Debt(
                participants.get(6),
                participants.get(2),
                new Money(new BigDecimal(5), EUR)
        ));

        expected.add(new DebtSimplifier.Debt(
                participants.get(3),
                participants.get(2),
                new Money(new BigDecimal(15), EUR)
        ));

        expected.add(new DebtSimplifier.Debt(
                participants.get(1),
                participants.get(2),
                new Money(new BigDecimal(30), EUR)
        ));

        assertEquals(expected, debtSimplifier.simplify());
    }

    @Test
    void simplifyCycle0() {
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(1), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(0), money1));
        assertEquals(new LinkedList<>(), debtSimplifier.simplify());
    }

    @Test
    void simplifyCycle1() {
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(1), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(2), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(2), participants.get(0), money1));
        assertEquals(new LinkedList<>(), debtSimplifier.simplify());
    }

    @Test
    void simplifyCycle2() {
        final double eur_to_usd = 1.5;

        exchangeRateFactory.addExchangeRate(new ExchangeRate(LocalDate.now(), EUR, USD, eur_to_usd));
        exchangeRateFactory.addExchangeRate(new ExchangeRate(LocalDate.now(), USD, EUR, 1/eur_to_usd));

        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(1), new Money(new BigDecimal(2), EUR)));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(2), new Money(new BigDecimal(3), USD)));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(2), participants.get(0), new Money(new BigDecimal(2), EUR)));

        assertEquals(new LinkedList<>(), debtSimplifier.simplify());
    }

    @Test
    void simplifyCycle2_1() {
        debtSimplifier = new DebtSimplifier(event.getParticipants(), USD,  exchangeRateFactory);

        final double eur_to_usd = 1.5;

        exchangeRateFactory.addExchangeRate(new ExchangeRate(LocalDate.now(), EUR, USD, eur_to_usd));
        exchangeRateFactory.addExchangeRate(new ExchangeRate(LocalDate.now(), USD, EUR, 1/eur_to_usd));

        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(1), new Money(new BigDecimal(2), EUR)));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(2), new Money(new BigDecimal(3), USD)));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(2), participants.get(0), new Money(new BigDecimal(2), EUR)));

        assertEquals(new LinkedList<>(), debtSimplifier.simplify());
    }


    @Test
    void simplifyCycle3() {
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(1), new Money(new BigDecimal(30), EUR)));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(2), new Money(new BigDecimal(20), EUR)));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(2), participants.get(0), new Money(new BigDecimal(30), EUR)));

        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        expected.add(new DebtSimplifier.Debt(
                participants.get(2),
                participants.get(1),
                new Money(new BigDecimal(10), EUR)
        ));
        assertEquals(expected, debtSimplifier.simplify());
    }
}
