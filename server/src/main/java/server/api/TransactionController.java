package server.api;

import commons.Event;
import commons.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;
import server.database.TransactionRepository;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/event/{eventId}/transactions")
public class TransactionController {
    private final TransactionRepository repo;
    private final EventRepository eventRepository;

    /**
     * Constructor for the EventController
     * @param repo the transaction repository
     * @param eventRepository The event repository
     */
    public TransactionController(TransactionRepository repo,
                                 EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.repo = repo;
    }

    /**
     * Method returns all the transactions of the event
     * @param eventId eventId
     * @return
     */

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<List<Transaction>> getAllTransactions(@PathVariable("eventId") Long eventId){
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event.getTransactions());
    }
    /**
     * Get transaction
     * @param eventId id of the event
     * @param transactionId  id of the transaction
     * @return  Transaction
     */
    @GetMapping("/{transactionId}")
    @ResponseBody
    public ResponseEntity<Transaction> getTransaction(
            @PathVariable("eventId") Long eventId,
            @PathVariable("transactionId") Long transactionId) {
        Transaction transaction = repo.findById(transactionId).orElse(null);
        Event event = eventRepository.findById(eventId).orElse(null);

        if (event == null || transaction == null) {
            return ResponseEntity.notFound().build();
        }

        if( transaction.getEvent() == null
                || !Objects.equals(transaction.getEvent().getId(), eventId)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(transaction);

    }

    /**
     * Edit a transaction
     * @param eventId The id of the event
     * @param transactionId The id of the transaction
     * @param transaction the transaction to what it should be updated
     * @return updated transaction
     */
    @PutMapping("/{transactionId}")
    @ResponseBody
    public ResponseEntity<Transaction> editTransaction(
            @PathVariable("eventId") Long eventId,
            @PathVariable("transactionId") Long transactionId,
            @RequestBody Transaction transaction) {
        Transaction updateTransaction = repo.findById(transactionId)
                                        .orElse(null);
        Event event = eventRepository.findById(eventId).orElse(null);
        if(updateTransaction == null || event == null) {
            return ResponseEntity.notFound().build();
        }
        if (!transaction.isValid()
                || !(transaction.getEvent().getId().equals(eventId))) {
            return ResponseEntity.badRequest().build();
        }

        updateTransaction.setName(transaction.getName());
        updateTransaction.setAmount(transaction.getAmount());
        updateTransaction.setPayer(transaction.getPayer());
        updateTransaction.setParticipants(transaction.getParticipants());
        updateTransaction.setDate(transaction.getDate());
        updateTransaction.setTag(transaction.getTag());

        repo.save(updateTransaction);

        return ResponseEntity.ok(updateTransaction);
    }

    /**
     * Add a transaction to an event
     * @param eventId The id of the event
     * @param transaction The transaction to add
     * @return Transaction added
     */
    @PostMapping(path = {"", "/"})
    @ResponseBody
    public ResponseEntity<Transaction>
        addTransaction(@PathVariable("eventId") Long eventId,
                       @RequestBody Transaction transaction) {
        if (!transaction.isValid()
                || !(transaction.getEvent().getId().equals(eventId))) {

            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(repo.save(transaction));
    }

    /**
     * Delete a transaction from an event
     * @param eventId The id of the event
     * @param transactionId the id of the transaction to delete
     * @return The transaction deleted
     */
    @DeleteMapping("/{transactionId}")
    @ResponseBody
    public ResponseEntity<Transaction> deleteTransaction(
            @PathVariable("eventId") Long eventId,
            @PathVariable("transactionId") Long transactionId) {

        Transaction transaction = repo.findById(transactionId).orElse(null);
        Event event = eventRepository.findById(eventId).orElse(null);

        if (event == null || transaction == null) {
            return ResponseEntity.notFound().build();
        }

        if( transaction.getEvent() == null
                || !Objects.equals(transaction.getEvent().getId(), eventId)){
            return ResponseEntity.badRequest().build();
        };
        boolean deleted = event.deleteTransaction(transaction);
        repo.deleteById(transaction.getId());

        if(!deleted) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(transaction);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

}
