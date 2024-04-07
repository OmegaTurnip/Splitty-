package server.api;

import commons.Event;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantControllerTest {

    private TestEventRepository eventRepo;
    private TestParticipantRepository partRepo;
    private ParticipantController sut;

    private Event testEvent1;
    private Participant testP1;

    @Mock
    SimpMessagingTemplate sim;



    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        eventRepo = new TestEventRepository();
        partRepo = new TestParticipantRepository();
        sut = new ParticipantController(partRepo, eventRepo, sim);
        testEvent1 = new Event("testEvent1");
        testEvent1.setId(100L);
        testEvent1.addParticipant("testP1");
        testP1 = testEvent1.getParticipants().get(0);
        testP1.setParticipantId(500L);

        Mockito.doNothing().when(sim).convertAndSend("/topic/admin", testEvent1);

    }

    @Test
    void constructorTest() {
        ParticipantController pc = new ParticipantController(partRepo, eventRepo, null);
        assertNotNull(pc);
    }

    @Test
    void addParticipantTest() {
        eventRepo.save(testEvent1);
        var retPart = sut.saveParticipant(testP1, testEvent1.getId());
        assertEquals(retPart.getBody(), testP1);

    }

    @Test
    void removeParticipant() {
        eventRepo.save(testEvent1);
        sut.saveParticipant(testP1, testEvent1.getId());
        var retDelete = sut.removeParticipant(testP1.getParticipantId(),
                testEvent1.getId());
        assertEquals(retDelete.getBody(), testP1);
//        assertEquals(0, eventRepo.getAll(testEvent1.getId()).getBody().size());
    }

    @Test
    void removeParticipantBadRequestTest() {
        var retPart = sut.removeParticipant(-123L, -2423L);
        assertTrue(retPart.getStatusCode().is4xxClientError());
        partRepo.save(testP1);
        var retPart2 = sut.removeParticipant(testP1.getParticipantId(), -2424234L);
        assertTrue(retPart2.getStatusCode().is4xxClientError());
    }

    @Test
    void addParticipantBadRequest() {
        testP1.setName("");
        var retPart = sut.saveParticipant(testP1, testEvent1.getId());
        assertTrue(retPart.getStatusCode().is4xxClientError());
    }
}