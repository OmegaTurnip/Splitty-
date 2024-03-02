package server.api;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import commons.Participant;
import commons.Event;

import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/participants")
public class ParticipantController {
    private final ParticipantRepository repo;
    private final EventRepository eventRepo;

    /**
     * Constructor
     * @param repo the repository
     * @param eventRepo the event repository
     */
    public ParticipantController(ParticipantRepository repo,
                                 EventRepository eventRepo) {
        this.repo = repo;
        this.eventRepo = eventRepo;
    }

    /**
     * Mapping for adding a participant
     * @param participant the participant to add
     * @param eventId the event that the participant belongs to
     * @return the participant response entity
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Participant>
        add(@RequestBody Participant participant,
            @PathVariable("eventId") long eventId) {
        if (participant.getEvent() == null ||
                isNullOrEmpty(participant.getName()) ||
                participant.getEvent().getId() != eventId) {
            return ResponseEntity.badRequest().build();
        }

        Participant saved = repo.save(participant);
        return ResponseEntity.ok(saved);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Mapping for getting a participant by id
     * @param id the id of the participant to get
     * @return the participant found or a bad request if no participant
     * @param eventId the event that the participant belongs to
     * can be found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Participant>
        getById(@PathVariable("id") long id,
                @PathVariable("eventId") long eventId) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        Participant participant = repo.findById(id).get();
        if (participant.getEvent().getId() != eventId)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(participant);
    }

    /**
     * Mapping for getting all participants
     * @return a list of all participants
     * @param eventId the event that the participant belongs to
     */
    @GetMapping(path = { "", "/" })
    public ResponseEntity< List<Participant>>
        getAll(@PathVariable("eventId") long eventId) {
        Event event = eventRepo.findById(eventId).get();
        return ResponseEntity.ok(event.getParticipants());
    }

    /**
     * Mapping for changing a Participant
     * @param name the name to change to
     * @param id the id of the participant to change
     * @param eventId the event that the participant belongs to
     * @return the participant changed or a bad request if no
     * participant can be found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Participant>
        changeName(@RequestBody String name,
              @PathVariable long id,
              @PathVariable("eventId") long eventId) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        Participant participant = repo.findById(id).get();

        if (participant.getEvent().getId() != eventId)
            return ResponseEntity.badRequest().build();

        participant.setName(name);
        return ResponseEntity.ok(repo.save(participant));
    }

    /**
     * Mapping for deleting a participant
     * @param id the id of the participant to remove
     * @param eventId the event that the participant belongs to
     * @return the participant that was removed or bad
     * request if no participant was found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Participant>
        removeParticipant(@PathVariable long id,
                          @PathVariable("eventId") long eventId) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Participant participant = repo.findById(id).get();
        Event event = eventRepo.findById(eventId).get();

        if (participant.getEvent().getId() != eventId)
            return ResponseEntity.badRequest().build();
        repo.deleteById(id);
        event.removeParticipant(participant);
        return ResponseEntity.ok(participant);
    }
}
