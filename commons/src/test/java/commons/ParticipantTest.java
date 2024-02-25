package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {
    private Participant testParticipant1;
    private Participant testParticipant2;
    private Participant testParticipant3;

    @BeforeEach
    void setup() {
        Event testEvent = new Event("Josh's Birthday Party");

<<<<<<< HEAD
=======
        testParticipant1 = new Participant("Josh", testEvent);
        testParticipant2 = new Participant("Amy", testEvent);
        testParticipant3 = new Participant("Josh", testEvent);

>>>>>>> main
        testEvent.addParticipant("Josh");
        testEvent.addParticipant("Amy");
        testEvent.addParticipant("Josh");
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
    @Test
    void testNotEquals() {
        assertNotEquals(testParticipant1, testParticipant2);
    }

    @Test
    void differentNamesNotEquals() {
        testParticipant1.setName("Joshua");
        assertNotEquals(testParticipant1, testParticipant3);
    }

    @Test
    void testHashCode() {
        assertEquals(testParticipant1.hashCode(), testParticipant3.hashCode());
    }

}
