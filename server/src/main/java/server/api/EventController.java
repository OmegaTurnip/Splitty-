package server.api;

import commons.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;

import java.util.List;


@RestController
@RequestMapping("/api/event")
public class EventController {

    private final EventRepository eventRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Constructor for the EventController
     *
     * @param eventRepository   The event repository
     * @param messagingTemplate The messaging template
     */
    public EventController(EventRepository eventRepository,
                           SimpMessagingTemplate messagingTemplate) {
        this.eventRepository = eventRepository;
        this.messagingTemplate = messagingTemplate;
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
        messagingTemplate.convertAndSend("/topic/admin", event);
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
        if (event.getEventName().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        eventRepository.save(event);
        messagingTemplate.convertAndSend("/topic/admin", event);
        return ResponseEntity.ok(event);
        //tbf this might not be the proper way to do PUT.
        // PUT methods should specify the URI exactly,
        // so a proper pathing would be /{id}
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
        Event event = getEvent(id).getBody();
        eventRepository.deleteById(id);
        messagingTemplate.convertAndSend("/topic/admin/delete", event);
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
