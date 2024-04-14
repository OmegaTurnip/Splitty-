package server.api;

import commons.Event;
import commons.Money;
import commons.Participant;
import commons.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionControllerTest {


    private TestEventRepository eventRepo;
    private TestTransactionRepository transactionRepo;

    private TransactionController sut;

    private Event testEvent1;
    private Transaction transaction;

    private Transaction editTransaction;

    private Participant testP1;

    private List<Participant> group;
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        eventRepo = new TestEventRepository();
        transactionRepo = new TestTransactionRepository();
        sut = new TransactionController(transactionRepo, eventRepo, null, simpMessagingTemplate);
        testEvent1 = new Event("testEvent1");
        testEvent1.setId(100L);
        testEvent1.addParticipant("testP1");
        testP1 = testEvent1.getParticipants().getFirst();
        testP1.setParticipantId(500L);
        group = new ArrayList<>();
        group.add(testP1);
        transaction = testEvent1.registerDebt(testP1, "testTransaction1", new Money(new BigDecimal(100), Currency.getInstance("EUR")),
                group, null, testEvent1.getTags().get(0));
        transaction.setTransactionId(600L);
        editTransaction = Transaction.createDebt(testP1, "editTransaction",  new Money(new BigDecimal(100), Currency.getInstance("EUR")), group, testEvent1, null, testEvent1.getTags().get(0));

    }

    @Test
    void addTransaction() {
        eventRepo.save(testEvent1);
        var retPart = sut.addTransaction(testEvent1.getId(), transaction);
        assertEquals(retPart.getBody(), transaction);
    }

    @Test
    void deleteTransaction() {
        eventRepo.save(testEvent1);
        sut.addTransaction(testEvent1.getId(), transaction);
        var retPart = sut.deleteTransaction(testEvent1.getId(), transaction.getTransactionId());
        assertEquals(retPart.getBody(), transaction);
    }

    @Test
    void deleteTransactionNotFound() {
        var retTransaction = sut.deleteTransaction(testEvent1.getId(), transaction.getTransactionId());
        assertEquals(ResponseEntity.notFound().build(), retTransaction);
    }
}
