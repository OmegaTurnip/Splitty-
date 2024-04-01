package server.api;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import commons.Participant;
import commons.Event;

import server.database.EventRepository;
import server.database.ParticipantRepository;

@RestController
@RequestMapping("/api/event/{eventId}/participants")
public class ParticipantController {
    private final ParticipantRepository repo;
    private final EventRepository eventRepo;

    /**
     * Constructor
     *
     * @param repo      the repository
     * @param eventRepo the event repository
     */
    public ParticipantController(ParticipantRepository repo,
                                 EventRepository eventRepo) {
        this.repo = repo;
        this.eventRepo = eventRepo;
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
        add(@RequestBody Participant participant,
        @PathVariable("eventId") long eventId) {

        if (isNullOrEmpty(participant.getName())) {
            return ResponseEntity.badRequest().build();
        }

        var event = eventRepo.findById(eventId);
        if (event.isEmpty()) return ResponseEntity.badRequest().build();

        participant.setEvent(event.get());
        Participant saved = repo.save(participant);
        return ResponseEntity.ok(saved);
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
        eventRepo.save(event);
        return ResponseEntity.ok(participant);
    }

}