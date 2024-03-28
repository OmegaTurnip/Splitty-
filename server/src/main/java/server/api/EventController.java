package server.api;

import commons.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;

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
     * Get all events
     * @return  All events
     */
    @GetMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<List<Event>> allEvents() {
        List<Event> events = eventRepository.findAll();
        return ResponseEntity.ok(events);
    }

//    /**
//     * Save events
//     * @param events The events to save
//     * @return The events saved
//     */
//    @PutMapping(path = { "", "/" })
//    @ResponseBody
//    public ResponseEntity<List<Event>> saveEvents(
//            @RequestBody List<Event> events) {
//        eventRepository.saveAll(events);
//        return ResponseEntity.ok(events);
//    }

    /**
     * Create an event
     * @param event The event to create
     * @return  The created event
     */
    @PostMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {

        eventRepository.saveAndFlush(event);

        return ResponseEntity.ok(event);
    }

    /**
     * Save an event
     * @param event The event to save
     * @return The saved event
     */
    @PutMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Event> saveEvent(@RequestBody Event event) {
        eventRepository.save(event);
        return ResponseEntity.ok(event);
    }

    /**
     * Delete an event
     * @param id The id of the event to delete
     * @return Confirmation of deletion.
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id) {
        if (!eventRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        eventRepository.deleteById(id);
        return ResponseEntity.noContent().build();
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

    /**
     * Get an event by invite code.
     * @param inviteCode The invite code
     * @return The event
     */
    @GetMapping("/invite/{inviteCode}")
    @ResponseBody
    public ResponseEntity<Event> getEventByInviteCode(
            @PathVariable("inviteCode") String inviteCode) {
        Event event = eventRepository.findByInviteCode(inviteCode);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }

}
