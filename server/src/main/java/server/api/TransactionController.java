package server.api;

import commons.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.EventRepository;
import server.database.TransactionRepository;
import server.financial.ExchangeRateFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/event/{eventId}/transactions")
public class TransactionController {
    private final TransactionRepository repo;
    private final EventRepository eventRepository;

    private final ExchangeRateFactory exchangeRateFactory;

    private Map<Object, Consumer<Transaction>> listeners;
    private SimpMessagingTemplate messagingTemplate;


    /**
     * Constructor for the EventController.
     *
     * @param   repo
     *          The transaction repository.
     * @param   eventRepository
     *          The event repository.
     * @param   exchangeRateFactory
     *          The {@link ExchangeRateFactory} used in this controller.
     *
     * @param  messagingTemplate
     *         The messaging template used in this controller.
     */
    public TransactionController(TransactionRepository repo,
                                 EventRepository eventRepository,
                                 ExchangeRateFactory exchangeRateFactory,
                                 SimpMessagingTemplate messagingTemplate) {
        this.eventRepository = eventRepository;
        this.repo = repo;
        this.exchangeRateFactory = exchangeRateFactory;
        this.listeners = new ConcurrentHashMap<>();
        this.messagingTemplate = messagingTemplate;
    }


    /**
     * Updates of add expense using long-polling
     * Usage of deferred result makes it
     * automatically in waiting stage (asynchronous)
     * @param eventId eventId
     * @return deferred result of response-entity transaction
     */
    @GetMapping("/updates")
    @ResponseBody
    public DeferredResult<ResponseEntity<Transaction>> getUpdates(
            @PathVariable("eventId") Long eventId){
        Event event = eventRepository.findById(eventId).orElse(null);

        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res = new DeferredResult<ResponseEntity<Transaction>>(
                500L, noContent);

        var key = new Object();
        listeners.put(key, t -> res.setResult(ResponseEntity.ok(t)));
        res.onCompletion(() -> listeners.remove(key));

        return res;
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
        if(transaction == null){
            return ResponseEntity.badRequest().build();
        }
        var optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        Event event = optionalEvent.get();
        transaction.setPayer(event.getParticipantById(
                transaction.getPayer().getParticipantId()
        ));
        List<Participant> participants = new ArrayList<>();
        for (Participant participant : transaction.getParticipants()) {
            participants.add(event.getParticipantById(
                    participant.getParticipantId()
            ));
        }
        transaction.setParticipants(participants);
        if (transaction.getTag() != null) {
            transaction.setTag(event.getTagById(
                    transaction.getTag().getTagId()
            ));
        }
        transaction.setLongPollingEventId(eventId);
        Transaction response = repo.save(transaction);
        event.addTransaction(response);
        eventRepository.save(event);
        listeners.forEach((k, l) -> l.accept(response));
        return ResponseEntity.ok(response);
    }

    /**
     * Basically same as addEvent but using websockets instead of
     * polling
     *
     * @param eventId     The id of the event
     * @param transaction The transaction to add
     * @return Transaction added
     */
    @PostMapping("/undoDelete")
    @ResponseBody
    public ResponseEntity<Transaction>
        undoDelete(@PathVariable("eventId") Long eventId,
               @RequestBody Transaction transaction) {
        if (transaction == null) {
            return ResponseEntity.badRequest().build();
        }
        var event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        transaction.setPayer(event.get().getParticipantById(
                transaction.getPayer().getParticipantId()
        ));
        List<Participant> participants = new ArrayList<>();
        for (Participant participant : transaction.getParticipants()) {
            participants.add(event.get().getParticipantById(
                    participant.getParticipantId()
            ));
        }
        transaction.setParticipants(participants);
        if (transaction.getTag() != null) {
            transaction.setTag(event.get().getTagById(
                    transaction.getTag().getTagId()
            ));
        }
        transaction.setLongPollingEventId(eventId);
        Transaction response = repo.save(transaction);
        messagingTemplate.convertAndSend("/topic/undoDelete", transaction);
        return ResponseEntity.ok(response);
    }


    /**
     * Delete a transaction from an event
     * @param eventId The id of the event
     * @param transactionId the id of the transaction to delete
     * @return The transaction deleted
     */
    @Transactional
    @DeleteMapping("/{transactionId}")
    @ResponseBody
    public ResponseEntity<Transaction> deleteTransaction(
            @PathVariable("eventId") Long eventId,
            @PathVariable("transactionId") Long transactionId) {

        var optionalTransaction = repo.findByTransactionId(transactionId);
        if(optionalTransaction.isEmpty())
            return ResponseEntity.notFound().build();
        Transaction transaction = optionalTransaction.get();
        transaction.getParticipants().size(); //terrible workaround

        var optionalEvent = eventRepository.findById(eventId);
        if(optionalEvent.isEmpty()) return ResponseEntity.badRequest().build();
        Event event = optionalEvent.get();

        event.deleteTransaction(transaction);

        repo.delete(transaction);
        event.removeTransaction(transaction);
        Event test = eventRepository.save(event);

        messagingTemplate.convertAndSend("/topic/transaction/delete",
                transaction);

        return ResponseEntity.ok(transaction);
    }
}
