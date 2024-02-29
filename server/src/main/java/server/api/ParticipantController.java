package server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import commons.Participant;
import server.database.ParticipantRepository;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
public class ParticipantController {
    private final ParticipantRepository repo;

    /**
     * Constructor
     * @param repo the repository
     */
    public ParticipantController(ParticipantRepository repo) {
        this.repo = repo;
    }

    /**
     * Mapping for adding a participant
     * @param participant the participant to add
     * @return the participant response entity
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Participant>
        add(@RequestBody Participant participant) {
        if (participant.getEvent() == null ||
                isNullOrEmpty(participant.getName()) ||
                isNullOrEmpty(participant.getEvent().getEventName())) {
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
     * can be found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Participant> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * Mapping for getting all participants
     * @return a list of all participants
     */
    @GetMapping(path = { "", "/" })
    public List<Participant> getAll() {
        return repo.findAll();
    }

    /**
     * Mapping for changing a Participant
     * @param participant the updated participant to change to
     * @param id the id of the participant to change
     * @return the participant changed or a bad request if no
     * participant can be found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Participant> changeParticipant
    (@RequestBody Participant participant, @PathVariable long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return repo.findById(id)
                .map(p -> ResponseEntity.ok(repo.save(p))).get();
    }

    /**
     * Mapping for deleting a participant
     * @param id the id of the participant to remove
     * @return the participant that was removed or bad
     * request if no participant was found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Participant>
        removeParticipant(@PathVariable long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        ResponseEntity<Participant> participant =
                ResponseEntity.ok(repo.findById(id).get());
        repo.deleteById(id);
        return participant;
    }
}
