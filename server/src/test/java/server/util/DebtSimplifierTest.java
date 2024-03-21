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
    private Money money3;

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

        debtSimplifier = new DebtSimplifier(event.getParticipants(), exchangeRateFactory);

        invalidParticipant = event.addParticipant("Hannah");

        money1 = //                     VVV value below VVV
        money2 = new Money(new BigDecimal(10), Currency.getInstance("EUR"));
        money3 = new Money(new BigDecimal(20), Currency.getInstance("EUR"));

        debt1 = new DebtSimplifier.Debt(participants.get(0), participants.get(1), money1);
        debt2 = new DebtSimplifier.Debt(participants.get(0), participants.get(1), money2); // == debt1
        debt3 = new DebtSimplifier.Debt(participants.get(1), participants.get(2), money3);
    }

    @Test
    void constructor() {
        assertThrows(NullPointerException.class, () -> new DebtSimplifier(null));
    }

    @Test
    void debtClass() {
        assertThrows(NullPointerException.class, () -> new DebtSimplifier.Debt(null, participants.get(1), money1));
        assertThrows(NullPointerException.class, () -> new DebtSimplifier.Debt(participants.get(2), null, money1));
        assertThrows(NullPointerException.class, () -> new DebtSimplifier.Debt(participants.get(2), participants.get(1), null));

        assertThrows(IllegalArgumentException.class, () -> new DebtSimplifier.Debt(participants.get(2), participants.get(2), money1));

        assertEquals(participants.get(0), debt1.getFrom());
        assertEquals(participants.get(1), debt1.getTo());
        assertEquals(money1, debt1.getAmount());

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

        assertThrows(NullPointerException.class, () -> debtSimplifier.addDebt(null));

        assertThrows(IllegalArgumentException.class, () -> debtSimplifier.addDebt(new DebtSimplifier.Debt(invalidParticipant, participants.get(4), money3)));
        assertThrows(IllegalArgumentException.class, () -> debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(4), invalidParticipant, money3)));
    }

    @Test
    void divideDebts() {
    }

    @Test
    void simplify0() {
        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        assertEquals(expected, debtSimplifier.simplify(Currency.getInstance("EUR")));
    }

    @Test
    void simplify1() {
        debtSimplifier.addDebt(debt1);

        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        expected.add(debt1);

        assertEquals(expected, debtSimplifier.simplify(Currency.getInstance("EUR")));
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
                money3
        ));

        assertEquals(expected, debtSimplifier.simplify(Currency.getInstance("EUR")));
    }

    @Test
    void simplify3() {
        debtSimplifier.addDebt(debt1);
        debtSimplifier.addDebt(debt2);
        debtSimplifier.addDebt(debt3);

        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        final int eur_to_usd = 2;

        exchangeRateFactory.addExchangeRate(new ExchangeRate(LocalDate.now(), EUR, USD, eur_to_usd));

        expected.add(new DebtSimplifier.Debt(
                participants.get(0),
                participants.get(2),
                exchangeRateFactory.getMostRecent(EUR, USD).convert(money3)
        ));

        assertEquals(expected, debtSimplifier.simplify(Currency.getInstance("USD")));
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

        assertEquals(expected, debtSimplifier.simplify(Currency.getInstance("EUR")));
    }

    @Test
    void simplify5() {
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(1), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(2), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(2), participants.get(3), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(3), participants.get(4), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(4), participants.get(5), money1));

        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(4), money3));

        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        expected.add(new DebtSimplifier.Debt(
                participants.get(0),
                participants.get(5),
                money1
        ));

        expected.add(new DebtSimplifier.Debt(
                participants.get(1),
                participants.get(4),
                money3
        ));

        assertEquals(expected, debtSimplifier.simplify(Currency.getInstance("EUR")));
    }

    @Test
    void simplify6() {
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(1), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(2), money1));

        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(2), participants.get(3), money3));

        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(3), participants.get(4), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(4), participants.get(5), money1));


        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        expected.add(new DebtSimplifier.Debt(
                participants.get(2),
                participants.get(3),
                money1
        ));

        expected.add(new DebtSimplifier.Debt(
                participants.get(0),
                participants.get(5),
                money1
        ));

        assertEquals(expected, debtSimplifier.simplify(Currency.getInstance("EUR")));
    }

    @Test
    void simplify7() {
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(1), money3));
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

        assertEquals(expected, debtSimplifier.simplify(Currency.getInstance("EUR")));
    }

    @Test
    void simplify8() {
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(0), participants.get(1), money1));
        debtSimplifier.addDebt(new DebtSimplifier.Debt(participants.get(1), participants.get(2), money3));

        LinkedList<DebtSimplifier.Debt> expected = new LinkedList<>();

        expected.add(new DebtSimplifier.Debt(
                participants.get(1),
                participants.get(2),
                money1
        ));

        expected.add(new DebtSimplifier.Debt(
                participants.get(0),
                participants.get(2),
                money1
        ));

        assertEquals(expected, debtSimplifier.simplify(Currency.getInstance("EUR")));
    }

}