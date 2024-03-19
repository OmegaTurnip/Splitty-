package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
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

        testTransaction1 = new Transaction(testParticipant1, "Drinks", new Money(new BigDecimal(400), Currency.getInstance("EUR")),
                testParticipants, testEvent, new Tag("food", "blue"));
        testTransaction2 = new Transaction(testParticipant2, "Bowling", new Money(new BigDecimal(200), Currency.getInstance("EUR")),
                testEvent.getParticipants(), testEvent, new Tag("activities", "red"));
        testTransaction3 = new Transaction(testParticipant1, "Drinks", new Money(new BigDecimal(400), Currency.getInstance("EUR")),
                testParticipants, testEvent, new Tag("food", "blue"));
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
