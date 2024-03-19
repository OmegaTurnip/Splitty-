package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest{
    private Participant testParticipant1;
    private Participant testParticipant2;
    private Participant testParticipant3;
    @BeforeEach
        void setup() {
            Event testEvent = new Event("Josh's Birthday Party");

            testParticipant1 = new Participant("Josh", testEvent);
            testParticipant2 = new Participant("Amy", testEvent);
            testParticipant3 = new Participant("Josh", testEvent);
            testEvent.addParticipant("Josh");
            testEvent.addParticipant("Amy");
            testEvent.addParticipant("Josh");

            Iterator<Participant> participantIterator = testEvent.getParticipants().iterator();

            testParticipant1 = participantIterator.next();
            testParticipant2 = participantIterator.next();
            testParticipant3 = participantIterator.next();

            testParticipant1.setId(1L);
            testParticipant2.setId(2L);
            testParticipant3.setId(3L);

//            testParticipant1.setId((long) 2);
//            testParticipant3.setId((long) 2);
        }
        @Test
        void getName(){

        Event testEvent = new Event("Josh's Birthday Party");
        Participant participant = new Participant("Josh", testEvent);
        assertEquals("Josh", participant.getName());
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

//        @Test
//        void testEquals() {
//            assertNotEquals(testParticipant1, testParticipant3);
//        }
        @Test
        void testNotEquals() {
            assertNotEquals(testParticipant1, testParticipant2);
        }

//        @Test
//        void differentNamesNotEquals() {
//            testParticipant1.setName("Joshua");
//            assertNotEquals(testParticipant1, testParticipant3);
//        }

        @Test
        void testHashCode() {
            assertNotEquals(testParticipant1.hashCode(), testParticipant3.hashCode());
        }

    }