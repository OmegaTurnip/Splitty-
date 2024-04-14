package server.api;


import commons.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import commons.Participant;
import commons.Event;

import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/event/{eventId}/participants")
public class ParticipantController {
    private final ParticipantRepository repo;
    private final EventRepository eventRepo;
    private final TransactionController transactionController;

    private SimpMessagingTemplate messagingTemplate;
    /**
     * Constructor
     *
     * @param repo              the repository
     * @param eventRepo         the event repository
     * @param transactionController the transaction controller
     * @param messagingTemplate the messaging template
     */
    public ParticipantController(ParticipantRepository repo,
                                 EventRepository eventRepo,
                                 TransactionController transactionController,
                                 SimpMessagingTemplate messagingTemplate) {
        this.repo = repo;
        this.eventRepo = eventRepo;
        this.transactionController = transactionController;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Mapping for adding a participant
     *
     * @param participant the participant to add
     * @param eventId     the event that the participant belongs to
     * @return the participant response entity
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Participant>
        saveParticipant(@RequestBody Participant participant,
                    @PathVariable("eventId") long eventId) {

        if (isNullOrEmpty(participant.getName())) {
            return ResponseEntity.badRequest().build();
        }

        var event = eventRepo.findById(eventId);
        if (event.isEmpty()) return ResponseEntity.badRequest().build();

        repo.save(participant);
        messagingTemplate.convertAndSend("/topic/admin", event.get());
        return ResponseEntity.ok(participant);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Mapping for deleting a participant
     *
     * @param id      the id of the participant to remove
     * @param eventId the event that the participant belongs to
     * @return the participant that was removed or bad
     * request if no participant was found
     */
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Participant>
        removeParticipant(@PathVariable Long id,
                      @PathVariable Long eventId) {
        var optionalParticipant = repo.findByParticipantId(id);
        if (optionalParticipant.isEmpty())
            return ResponseEntity.badRequest().build();
        Participant participant = optionalParticipant.get();
        var optionalEvent = eventRepo.findById(eventId);
        if (optionalEvent.isEmpty()) return ResponseEntity.badRequest().build();
        Event event = optionalEvent.get();
        repo.delete(participant);
        event.removeParticipant(participant);
        List<Transaction> transactions = new ArrayList<>(
                event.getTransactions());
        //Remember to not
        // modify the list you're iterating over...
        for (Transaction t : transactions) {

            if (t.getPayer().equals(participant)) {
                transactionController.deleteTransaction(
                        eventId, t.getTransactionId());
                continue;
            }
            t.getParticipants().remove(participant);
            if (t.getParticipants().isEmpty()) {
                transactionController.deleteTransaction(
                        eventId, t.getTransactionId());
            }

        }
        eventRepo.save(event);
        messagingTemplate.convertAndSend("/topic/admin", event);
        return ResponseEntity.ok(participant);
    }

}