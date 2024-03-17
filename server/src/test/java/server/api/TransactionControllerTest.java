package server.api;

import commons.Event;
import commons.Participant;
import commons.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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

    @BeforeEach
    void setup() {
        eventRepo = new TestEventRepository();
        transactionRepo = new TestTransactionRepository();
        sut = new TransactionController(transactionRepo, eventRepo);
        testEvent1 = new Event("testEvent1");
        testEvent1.setId(100L);
        testEvent1.addParticipant("testP1");
        testP1 = testEvent1.getParticipants().getFirst();
        testP1.setId(500L);
        group = new ArrayList<>();
        group.add(testP1);
        transaction = testEvent1.registerTransaction(testP1, "testTransaction1", 100, group, testEvent1.getTags().get(0));
        transaction.setId(600L);
        editTransaction = new Transaction(testP1, "editTransaction",  100, group, testEvent1,testEvent1.getTags().get(0));

    }

    @Test
    void getTransactionById() {
        eventRepo.save(testEvent1);
        sut.addTransaction(testEvent1.getId(), transaction);
        var retPart = sut.getTransaction(testEvent1.getId(), transaction.getId());
        assertEquals(retPart.getBody(), transaction);
    }

    @Test
    void getTransactionIdIsNull(){
        var retPart = sut.getTransaction(testEvent1.getId(), transaction.getId());
        assertNull(retPart.getBody());
    }

    @Test
    void editTransaction() {
        eventRepo.save(testEvent1);
        sut.addTransaction(testEvent1.getId(), transaction);
        var retPart = sut.editTransaction(testEvent1.getId(), transaction.getId(), editTransaction);
        assertEquals(retPart.getBody().getTransactionName(), editTransaction.getTransactionName());
        assertEquals(retPart.getBody().getPrice(), editTransaction.getPrice());
        assertEquals(retPart.getBody().getPayer(), editTransaction.getPayer());
        assertEquals(retPart.getBody().getParticipants(), editTransaction.getParticipants());
        assertEquals(retPart.getBody().getDate(), editTransaction.getDate());
        assertEquals(retPart.getBody().getTag(), editTransaction.getTag());
        assertEquals(retPart.getBody().getEvent(), editTransaction.getEvent());
    }

    @Test
    void addTransaction() {
        var retPart = sut.addTransaction(testEvent1.getId(), transaction);
        assertEquals(retPart.getBody(), transaction);
    }

    @Test
    void deleteTransaction() {
        eventRepo.save(testEvent1);
        sut.addTransaction(testEvent1.getId(), transaction);
        var retPart = sut.deleteTransaction(testEvent1.getId(), transaction.getId());
        assertEquals(retPart.getBody(), transaction);
    }
}
