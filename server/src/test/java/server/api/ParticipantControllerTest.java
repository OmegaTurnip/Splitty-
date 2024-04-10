package server.api;

import commons.Event;
import commons.Money;
import commons.Participant;
import commons.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantControllerTest {

    private TestEventRepository eventRepo;
    private TestParticipantRepository partRepo;
    @Mock
    private TransactionController transactionController;
    private ParticipantController sut;

    private Event testEvent1;
    private Transaction transaction;
    private Participant testP1;
    private Participant testP2;

    @Mock
    SimpMessagingTemplate sim;



    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        eventRepo = new TestEventRepository();
        partRepo = new TestParticipantRepository();
        sut = new ParticipantController(partRepo, eventRepo, transactionController, sim);

        testEvent1 = new Event("testEvent1");
        testEvent1.setId(100L);

        testEvent1.addParticipant("testP1");
        testP1 = testEvent1.getParticipants().get(0);
        testP1.setParticipantId(500L);

        testEvent1.addParticipant("testP2");
        testP2 = testEvent1.getParticipants().get(1);
        testP2.setParticipantId(501L);

        Mockito.doNothing().when(sim).convertAndSend("/topic/admin", testEvent1);

    }

    @Test
    void constructorTest() {
        ParticipantController pc = new ParticipantController(partRepo, eventRepo, transactionController, null);
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

    @Test
    void removeParticipantThenDeleteTransactionTest() {
        List<Participant> group = new ArrayList<>();
        group.add(testP1);
        transaction = testEvent1.registerDebt(testP1, "testTransaction1", new Money(new BigDecimal(100), Currency.getInstance("EUR")),
                group, testEvent1.getTags().get(0));
        transaction.setTransactionId(600L);

        Mockito.when(transactionController.deleteTransaction(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
        eventRepo.save(testEvent1);
        sut.saveParticipant(testP1, testEvent1.getId());
        var retDelete = sut.removeParticipant(testP1.getParticipantId(),
                testEvent1.getId());
        assertEquals(retDelete.getBody(), testP1);
        Mockito.verify(transactionController, Mockito.times(1)).deleteTransaction(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void removeParticipantThenDeleteTransactionTest2() {
        List<Participant> group = new ArrayList<>();
        group.add(testP1);
//        group.add(testP2);
        transaction = testEvent1.registerDebt(testP2, "testTransaction1", new Money(new BigDecimal(100), Currency.getInstance("EUR")),
                group, testEvent1.getTags().get(0));
        transaction.setTransactionId(600L);

        Mockito.when(transactionController.deleteTransaction(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
        eventRepo.save(testEvent1);
        sut.saveParticipant(testP1, testEvent1.getId());
        var retDelete = sut.removeParticipant(testP1.getParticipantId(),
                testEvent1.getId());
        assertEquals(retDelete.getBody(), testP1);
        Mockito.verify(transactionController, Mockito.times(1)).deleteTransaction(Mockito.anyLong(), Mockito.anyLong());
    }
}