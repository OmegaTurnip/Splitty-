package commons;

import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

  private Event testEvent;

  @BeforeEach
  void setup() {
    testEvent = new Event("Josh's Birthday Party");

    LocalDate testDate1 = LocalDate.of(2023, 7, 23);
    testEvent.setEventCreationDate(testDate1);

    Participant testParticipant1 = new Participant("Josh", testEvent);
    Participant testParticipant2 = new Participant("Amy", testEvent);
    Participant testParticipant3 = new Participant("Rizwan", testEvent);

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

    testEvent.setParticipants(testAllParticipants1);

    Expense testExpense1 = new Expense(testParticipant1, "Drinks",400, testParticipants1);
    Expense testExpense2 = new Expense(testParticipant2, "Lunch", 350, testParticipants2);
    testExpense1.setDate(testDate1);
    testExpense2.setDate(testDate1);
    Collection<Expense> testExpenses1 = new ArrayList<>();
    testExpenses1.add(testExpense1);
    testExpenses1.add(testExpense2);

    testEvent.setExpenses(testExpenses1);
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
}

