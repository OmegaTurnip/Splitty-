package server.api;

import commons.Event;
import commons.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;
import server.database.TransactionRepository;

@RestController
@RequestMapping("/event")
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

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Transaction> getTransaction(@PathVariable("id") Long id, @PathVariable Long transactionId) {
        Transaction transaction = repo.findById(transactionId).orElse(null);
        Event event = eventRepository.findById(id).orElse(null);

        if (event == null || transaction == null) {
            return ResponseEntity.notFound().build();
        }

        if( transaction.getEvent() == null
                || transaction.getEvent().getId() != id){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(transaction);

    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Transaction> editTransaction(@PathVariable("id") Long id, @PathVariable Long transactionId, @RequestBody Transaction transaction) {
        Transaction updateTransaction = repo.findById(transactionId).orElse(null);
        Event event = eventRepository.findById(id).orElse(null);
        if(updateTransaction == null || event == null) {
            return ResponseEntity.notFound().build();
        }
        if(transaction.hasNull(transaction)
                || !(transaction.getEvent().getId().equals(id))) {
            return ResponseEntity.badRequest().build();
        }

        updateTransaction.setTransactionName(transaction.getTransactionName());
        updateTransaction.setPrice(transaction.getPrice());
        updateTransaction.setPayer(transaction.getPayer());
        updateTransaction.setParticipants(transaction.getParticipants());
        updateTransaction.setDate(transaction.getDate());
        updateTransaction.setTag(transaction.getTag());

        repo.save(updateTransaction);

        return ResponseEntity.ok(updateTransaction);
    }

    /**
     * Add a transaction to an event
     * @param id The id of the event
     * @param transaction The transaction to add
     * @return Transaction added
     */
    @PostMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Transaction>
        addTransaction(@PathVariable("id") Long id,
                       @RequestBody Transaction transaction) {
        if (transaction.hasNull(transaction)
                || !(transaction.getEvent().getId().equals(id))) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(repo.save(transaction));
    }

    /**
     * Delete a transaction from an event
     * @param id The id of the event
     * @param transactionId the id of the transaction to delete
     * @return The transaction deleted
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Transaction>
    deleteTransaction(@PathVariable("id") Long id,
                      @PathVariable Long transactionId) {

        Transaction transaction = repo.findById(transactionId).orElse(null);
        Event event = eventRepository.findById(id).orElse(null);

        if (event == null || transaction == null) {
            return ResponseEntity.notFound().build();
        }

        if( transaction.getEvent() == null
                || transaction.getEvent().getId() != id){
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
