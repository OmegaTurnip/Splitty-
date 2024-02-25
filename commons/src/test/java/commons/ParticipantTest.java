package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {
    private Participant testParticipant1;
    private Participant testParticipant2;
    private Participant testParticipant3;

    @BeforeEach
    void setup() {
        Event testEvent = new Event("Josh's Birthday Party");

        testParticipant1 = new Participant("Josh", testEvent);
        testParticipant2 = new Participant("Amy", testEvent);
        testParticipant3 = new Participant("Josh", testEvent);

        testEvent.addParticipant(testParticipant1);
        testEvent.addParticipant(testParticipant2);
        testEvent.addParticipant(testParticipant3);
    }
    @Test
    void getName() {
        assertEquals("Josh", testParticipant1.getName());
    }

    @Test
    void setName() {
        testParticipant1.setName("Joshua");
        assertEquals("Joshua", testParticipant1.getName());
    }

    @Test
    void getEvent() {
        assertEquals(testParticipant1.getEvent(), testParticipant2.getEvent());
    }

    @Test
    void testEquals() {
        assertEquals(testParticipant1, testParticipant3);
    }

}
