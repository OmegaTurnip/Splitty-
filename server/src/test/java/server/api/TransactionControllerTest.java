package server.api;

import commons.Event;
import commons.Money;
import commons.Participant;
import commons.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

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

    @BeforeEach
    void setup() {
        eventRepo = new TestEventRepository();
        transactionRepo = new TestTransactionRepository();
        sut = new TransactionController(transactionRepo, eventRepo, null);
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
    void getTransactionById() {
        eventRepo.save(testEvent1);
        sut.addTransaction(testEvent1.getId(), transaction);
        var retPart = sut.getTransaction(testEvent1.getId(), transaction.getTransactionId());
        assertEquals(retPart.getBody(), transaction);
    }

    @Test
    void getTransactionIdIsNull(){
        var retPart = sut.getTransaction(testEvent1.getId(), transaction.getTransactionId());
        assertNull(retPart.getBody());
    }

    @Test
    void editTransaction() {
        eventRepo.save(testEvent1);
        sut.addTransaction(testEvent1.getId(), transaction);
        var retPart = sut.editTransaction(testEvent1.getId(), transaction.getTransactionId(), editTransaction);
        assertEquals(retPart.getBody().getName(), editTransaction.getName());
        assertEquals(retPart.getBody().getAmount(), editTransaction.getAmount());
        assertEquals(retPart.getBody().getPayer(), editTransaction.getPayer());
        assertEquals(retPart.getBody().getParticipants(), editTransaction.getParticipants());
        assertEquals(retPart.getBody().getDate(), editTransaction.getDate());
        assertEquals(retPart.getBody().getTag(), editTransaction.getTag());
        assertEquals(retPart.getBody().getEvent(), editTransaction.getEvent());
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

    @Test
    void deleteTransactionBadRequest() {
        eventRepo.save(testEvent1);
        sut.addTransaction(testEvent1.getId(), transaction);
        transaction.setEvent(new Event());
        var retPart = sut.deleteTransaction(testEvent1.getId(), transaction.getTransactionId());
        assertEquals(retPart, ResponseEntity.badRequest().build());
    }
}
