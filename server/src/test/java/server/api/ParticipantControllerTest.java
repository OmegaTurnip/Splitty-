package server.api;

import commons.Event;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import server.database.EventRepository;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantControllerTest {

    private TestEventRepository eventRepo;
    private TestParticipantRepository partRepo;
    private ParticipantController sut;

    private Event testEvent1;
    private Participant testP1;


    @BeforeEach
    void setup() {
        eventRepo = new TestEventRepository();
        partRepo = new TestParticipantRepository();
        sut = new ParticipantController(partRepo, eventRepo);
        testEvent1 = new Event("testEvent1");
        testEvent1.setId(100L);
        testEvent1.addParticipant("testP1");
        testP1 = testEvent1.getParticipants().get(0);
        testP1.setParticipantId(500L);

    }

    @Test
    void addParticipantTest() {
        var retPart = sut.add(testP1, testEvent1.getId());
        assertEquals(retPart.getBody(), testP1);

    }


//    @Test
//    void changeName() {
//        sut.add(testP1, testEvent1.getId());
//        sut.changeName("changeTest1", 500L, 100L);
//        assertEquals(testP1.getName(), sut.getById(testP1.getId(), testEvent1.getId()).getBody().getName());
//    }

    @Test
    void removeParticipant() {
        eventRepo.save(testEvent1);
        sut.add(testP1, testEvent1.getId());
        var retDelete = sut.removeParticipant(testP1.getParticipantId(), testEvent1.getId());
        assertEquals(retDelete.getBody(), testP1);
//        assertEquals(0, eventRepo.getAll(testEvent1.getId()).getBody().size());
    }

//    @Test
//    void changeName() {
//        sut.add(testP1, testEvent1.getId());
//        sut.changeName("changeTest1", 500L, 100L);
//        assertEquals(testP1.getName(), sut.getById(testP1.getId(), testEvent1.getId()).getBody().getName());
//    }
}