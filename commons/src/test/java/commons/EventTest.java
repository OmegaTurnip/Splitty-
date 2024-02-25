package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    private Event testEvent;

    @BeforeEach
    void setup() {
        Participant testParticipant1 = new Participant("Josh");
        Participant testParticipant2 = new Participant("Amy");
        Participant testParticipant3 = new Participant("Rizwan");
        Date testDate1 = new Date(2023, Calendar.JULY, 23);

        Collection<Participant> testParticipants1 = new ArrayList<>();
        Collection<Participant> testParticipants2 = new ArrayList<>();
        Collection<Participant> testAllParticipants1 = new ArrayList<>();

        testParticipants1.add(testParticipant2);
        testParticipants1.add(testParticipant3);

        testParticipants2.add(testParticipant1);
        testParticipants2.add(testParticipant3);

        testAllParticipants1.add(testParticipant1);
        testAllParticipants1.add(testParticipant2);
        testAllParticipants1.add(testParticipant3);

        Expense testExpense1 = new Expense(testParticipant1, "Drinks", testDate1, 400, testParticipants1, new Tag("food", "blue"));
        Expense testExpense2 = new Expense(testParticipant2, "Lunch", testDate1, 350, testParticipants2, new Tag("food", "blue"));
        Collection<Expense> testExpenses1 = new ArrayList<>();
        testExpenses1.add(testExpense1);
        testExpenses1.add(testExpense2);

        testEvent = new Event("Josh's Birthday Party", testDate1, testExpenses1, testAllParticipants1);
    }

    @Test
    void generateInviteCodeTest() {
        String test1 = Event.generateInviteCode();
        String test2 = Event.generateInviteCode();
        assertFalse(Objects.equals(test1, test2));
    }

    @Test
    void sumAllExpensesTest() {
        assertTrue(testEvent.totalSumOfExpenses() == 750);
    }

    @Test
    void initializationOfTags1(){
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("food", "blue"));
        tags.add(new Tag("entrance fees", "green"));
        tags.add(new Tag("Travel", "yellow"));
        assertEquals(tags, testEvent.getTags());
    }

    @Test
    void addAditionalTag(){
        ArrayList<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag("food", "blue"));
        tags.add(new Tag("entrance fees", "green"));
        tags.add(new Tag("Travel", "yellow"));
        tags.add(new Tag("school", "white"));
        testEvent.addTag(new Tag("school", "white"));
        assertEquals(tags, testEvent.getTags());
    }
}
