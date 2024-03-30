package server.api;

import commons.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/event")
public class EventController {

    private final EventRepository eventRepository;

    /**
     * Constructor for the EventController
     * @param eventRepository The event repository
     */
    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

//    /**
//     * Gets all the events matching the list of invite codes
//     * sent in the request body
//     * @param invCodes List of invite codes
//     * @return List of events
//     */
//    @GetMapping(path = {"/myEvents"})
//    @ResponseBody
//    public ResponseEntity<List<Event>> myEvents(@RequestParam("invCodes")
//                                                    List<String> invCodes) {
//        System.out.println(invCodes);
//        List<Event> myE = eventRepository
//                .findAllByInviteCodeIsIn(invCodes);
//        System.out.println(myE);
//        return ResponseEntity.ok(myE);
//    }

    /**
     * Save events
     * @param events The events to save
     * @return The events saved
     */
    @PutMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<List<Event>> saveEvents(
            @RequestBody List<Event> events) {
        eventRepository.saveAll(events);
        return ResponseEntity.ok(events);
        //todo refactor to admin
    }

    /**
     * Save an event
     * @param event The event to save
     * @return  The event saved
     */
    @PostMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Event> saveEvent(@RequestBody Event event) {

        eventRepository.saveAndFlush(event);

        return ResponseEntity.ok(event);
    }

    /**
     * Get an event by id
     * @param id The id of the event
     * @return The event
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Event> getEvent(@PathVariable("id") Long id) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }
//    @GetMapping("/{id}")
//    public ResponseEntity<Participant>
//    getById(@PathVariable("id") long id,
//            @PathVariable("eventId") long eventId) {
//        if (id < 0 || !repo.existsById(id)) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        Participant participant = repo.findById(id).get();
//        if (participant.getEvent().getId() != eventId)
//            return ResponseEntity.badRequest().build();
//
//        return ResponseEntity.ok(participant);
//    }

    /**
     * Get an event by invite code.
     * @param inviteCodes The list of invite codes
     * @return The event
     */
    @GetMapping("/invite/{inviteCodes}")
    @ResponseBody
    public ResponseEntity<List<Event>> getEventByInviteCode(
            @PathVariable String inviteCodes) {
        List<String> inviteCodesList = List.of(inviteCodes.split(","));
        List<Event> events = eventRepository
                .findByInviteCodeIn(inviteCodesList);
        if (events == null) {
            return ResponseEntity.ok(new ArrayList<>());
//            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(events);
    }

}
