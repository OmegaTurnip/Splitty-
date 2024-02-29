package server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import commons.Participant;
import server.database.ParticipantRepository;

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
    public ResponseEntity<Participant> add(@RequestBody Participant participant) {
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

}
