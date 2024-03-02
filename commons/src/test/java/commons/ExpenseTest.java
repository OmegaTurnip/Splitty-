package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseTest {
    Expense testExpense1;
    Expense testExpense2;
    Expense testExpense3;
    Participant testParticipant2;
    Event testEvent;

    @BeforeEach
    void setup() {
        testEvent = new Event("Josh's Birthday Party");

        Participant testParticipant1 = testEvent.addParticipant("Josh");
        testParticipant2 = testEvent.addParticipant("Amy");
        testEvent.addParticipant("Josh");

        List<Participant> testDebtors = new ArrayList<>();
        testDebtors.add(testParticipant1);
        testDebtors.add(testParticipant2);

        testExpense1 = new Expense(testParticipant1, "Drinks", 400, testDebtors, testEvent, new Tag("food", "blue"));
        testExpense2 = new Expense(testParticipant2, "Bowling", 200, testEvent.getParticipants(), testEvent, new Tag("activities", "red"));
        testExpense3 = new Expense(testParticipant1, "Drinks", 400, testDebtors, testEvent, new Tag("food", "blue"));
    }

    @Test
    void setTag() {
        Tag testTag = new Tag("Phone", "yellow");
        testExpense1.setTag(testTag);
        assertEquals(testTag, testExpense1.getTag());
    }

    @Test
    void setPayer() {
        testExpense1.setPayer(testParticipant2);
        assertEquals(testParticipant2, testExpense1.getPayer());
    }

    @Test
    void setExpenseName() {
        testExpense1.setExpenseName("Food");
        assertEquals("Food", testExpense1.getExpenseName());
    }

    @Test
    void setDate() {
        testExpense1.setDate(LocalDate.of(2003, 3, 21));
        assertEquals(LocalDate.of(2003, 3, 21), testExpense1.getDate());
    }

    @Test
    void setPrice() {
        testExpense1.setPrice(90);
        assertEquals(90, testExpense1.getPrice());
    }

    @Test
    void getEvent() {
        assertEquals(testEvent, testExpense1.getEvent());
    }

    @Test
    void getId() {
        testExpense3.setId((long) 2);
        assertEquals(2, testExpense3.getId());
    }

    @Test
    void testNotEquals() {
        testExpense1.setId((long) 3);
        testExpense3.setId((long) 2);
        assertNotEquals(testExpense1, testExpense3);
    }

    @Test
    void testEquals() {
        testExpense1.setId((long) 2);
        testExpense3.setId((long) 2);
        assertEquals(testExpense1, testExpense3);
    }

    @Test
    void testHashCode() {
        testExpense1.setId((long) 2);
        testExpense3.setId((long) 2);
        assertEquals(testExpense3.hashCode(), testExpense1.hashCode());
    }
}
