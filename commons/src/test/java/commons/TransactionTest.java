package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {
    Transaction testTransaction1;
    Transaction testTransaction2;
    Transaction testTransaction3;
    Participant testParticipant2;
    Event testEvent;

    @BeforeEach
    void setup() {
        testEvent = new Event("Josh's Birthday Party");

        Participant testParticipant1 = testEvent.addParticipant("Josh");
        testParticipant2 = testEvent.addParticipant("Amy");
        testEvent.addParticipant("Josh");

        List<Participant> testParticipants = new ArrayList<>();
        testParticipants.add(testParticipant1);
        testParticipants.add(testParticipant2);

        testTransaction1 = Transaction.createDebt(testParticipant1, "Drinks", Money.fromLong(400, "EUR"),
                testParticipants, testEvent, null, new Tag("food", "blue"));
        testTransaction2 = Transaction.createDebt(testParticipant2, "Bowling", Money.fromLong(200, "EUR"),
                testEvent.getParticipants(), testEvent, null, new Tag("activities", "red"));
        testTransaction3 = Transaction.createDebt(testParticipant1, "Drinks", Money.fromLong(400, "EUR"),
                testParticipants, testEvent, null, new Tag("food", "blue"));
    }

    @Test
    void setTag() {
        Tag testTag = new Tag("Phone", "yellow");
        testTransaction1.setTag(testTag);
        assertEquals(testTag, testTransaction1.getTag());
    }

    @Test
    void setPayer() {
        testTransaction1.setPayer(testParticipant2);
        assertEquals(testParticipant2, testTransaction1.getPayer());
    }

    @Test
    void setExpenseName() {
        testTransaction1.setName("Food");
        assertEquals("Food", testTransaction1.getName());
    }

    @Test
    void setDate() {
        testTransaction1.setDate(LocalDate.of(2003, 3, 21));
        assertEquals(LocalDate.of(2003, 3, 21), testTransaction1.getDate());
    }

    @Test
    void setAmount() {
        testTransaction1.setAmount(Money.fromLong(736574, "EUR"));
        assertEquals(Money.fromLong(736574, "EUR"), testTransaction1.getAmount());
    }

    @Test
    void getId() {
        testTransaction3.setTransactionId((long) 2);
        assertEquals(2, testTransaction3.getTransactionId());
    }

    @Test
    void testNotEquals() {
        testTransaction1.setTransactionId((long) 3);
        testTransaction3.setTransactionId((long) 2);
        assertNotEquals(testTransaction1, testTransaction3);
    }

    @Test
    void testEquals() {
        testTransaction1.setTransactionId((long) 2);
        testTransaction3.setTransactionId((long) 2);
        assertEquals(testTransaction1, testTransaction3);
    }

    @Test
    void testHashCode() {
        testTransaction1.setTransactionId((long) 2);
        testTransaction3.setTransactionId((long) 2);
        assertEquals(testTransaction3.hashCode(), testTransaction1.hashCode());
    }
}
