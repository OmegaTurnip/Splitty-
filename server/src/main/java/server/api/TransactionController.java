package server.api;

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
        if (transaction.getEvent() == null
                || isNullOrEmpty(transaction.getTransactionName())
                || transaction.getPayer() == null
                || transaction.getPrice() == 0
                || transaction.getParticipants() == null
                || transaction.getParticipants().isEmpty()
                || !(transaction.getEvent().getId().equals(id))) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(repo.save(transaction));
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

}
