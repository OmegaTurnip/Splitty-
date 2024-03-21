package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    private Event testEvent;
    Participant testParticipant1;
    Participant testParticipant2;
    Participant testParticipant3;
    Transaction testTransaction1;
    Transaction testTransaction2;


  @BeforeEach
  void setup() {
    testEvent = new Event("Josh's Birthday Party");

    LocalDate testDate1 = LocalDate.of(2023, 7, 23);
    testEvent.setEventCreationDate(testDate1);

    testParticipant1 = new Participant("Josh", testEvent);
    testParticipant1.setId(1L);
    testParticipant2 = new Participant("Amy", testEvent);
    testParticipant1.setId(2L);
    testParticipant3 = new Participant("Rizwan", testEvent);
    testParticipant1.setId(3L);

    List<Participant> testParticipants1 = new ArrayList<>();
    List<Participant> testParticipants2 = new ArrayList<>();
    List<Participant> testAllParticipants1 = new ArrayList<>();

    testParticipants1.add(testParticipant2);
    testParticipants1.add(testParticipant3);

    testParticipants2.add(testParticipant1);
    testParticipants2.add(testParticipant3);

    testAllParticipants1.add(testParticipant1);
    testAllParticipants1.add(testParticipant2);
    testAllParticipants1.add(testParticipant3);

    testEvent.setParticipants(testAllParticipants1);

    testTransaction1 = Transaction.createDebt(testParticipant1, "Drinks", Money.fromLong(2, "EUR"),
            testParticipants1, testEvent, new Tag("food", "blue"));
    testTransaction2 = Transaction.createDebt(testParticipant2, "Lunch", Money.fromLong(2, "EUR"),

            testParticipants2, testEvent, new Tag("food", "blue"));
    testTransaction1.setDate(testDate1);
    testTransaction2.setDate(testDate1);
    Collection<Transaction> testTransactions1 = new ArrayList<>();
    testTransactions1.add(testTransaction1);
    testTransactions1.add(testTransaction2);

    testTransaction1.setId(1L);
    testTransaction2.setId(2L);

    testEvent.setTransactions(testTransactions1);
  }

    @Test
    void generateInviteCodeTest() {
        String test1 = Event.generateInviteCode();
        String test2 = Event.generateInviteCode();
        assertFalse(Objects.equals(test1, test2));
    }

//    @Test
//    void sumAllExpensesTest() {
//        assertTrue(testEvent.totalSumOfExpenses() == 750);
//    }

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
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(new Tag("food", "blue"));
        tags.add(new Tag("entrance fees", "green"));
        tags.add(new Tag("Travel", "yellow"));
        tags.add(new Tag("school", "white"));
        testEvent.addTag(new Tag("school", "white"));
        assertEquals(tags, testEvent.getTags());
    }

    @Test
    void removeParticipant() {
      testEvent.removeParticipant(testParticipant1);
      assertFalse(testEvent.getParticipants().contains(testParticipant1));
    }

    @Test
    void registerTransaction() {
      Transaction testTransaction3 = Transaction.createDebt(testParticipant1,
              "Movies",
                Money.fromLong(2, "EUR"), List.of(testParticipant1, testParticipant2, testParticipant3),
              testEvent,testEvent.getTags().getFirst());
      testEvent.registerDebt(testParticipant1,
              "Movies",
              Money.fromLong(200, "EUR"), List.of(testParticipant1, testParticipant2, testParticipant3),
              testEvent.getTags().getFirst());

      assertEquals(testEvent.getTransactions().get(2),  testTransaction3);
    }

    @Test
    void deleteTransaction() {
      assertTrue(testEvent.deleteTransaction(testTransaction1));
      assertFalse(testEvent.getTransactions().contains(testTransaction1));
    }
}
