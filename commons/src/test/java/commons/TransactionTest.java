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

        testTransaction1 = new Transaction(testParticipant1, "Drinks", 400, testParticipants, testEvent, new Tag("food", "blue"));
        testTransaction2 = new Transaction(testParticipant2, "Bowling", 200, testEvent.getParticipants(), testEvent, new Tag("activities", "red"));
        testTransaction3 = new Transaction(testParticipant1, "Drinks", 400, testParticipants, testEvent, new Tag("food", "blue"));
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
        testTransaction1.setTransactionName("Food");
        assertEquals("Food", testTransaction1.getTransactionName());
    }

    @Test
    void setDate() {
        testTransaction1.setDate(LocalDate.of(2003, 3, 21));
        assertEquals(LocalDate.of(2003, 3, 21), testTransaction1.getDate());
    }

    @Test
    void setPrice() {
        testTransaction1.setPrice(90);
        assertEquals(90, testTransaction1.getPrice());
    }

    @Test
    void getEvent() {
        assertEquals(testEvent, testTransaction1.getEvent());
    }

    @Test
    void getId() {
        testTransaction3.setId((long) 2);
        assertEquals(2, testTransaction3.getId());
    }

    @Test
    void testNotEquals() {
        testTransaction1.setId((long) 3);
        testTransaction3.setId((long) 2);
        assertNotEquals(testTransaction1, testTransaction3);
    }

    @Test
    void testEquals() {
        testTransaction1.setId((long) 2);
        testTransaction3.setId((long) 2);
        assertEquals(testTransaction1, testTransaction3);
    }

    @Test
    void testHashCode() {
        testTransaction1.setId((long) 2);
        testTransaction3.setId((long) 2);
        assertEquals(testTransaction3.hashCode(), testTransaction1.hashCode());
    }
}