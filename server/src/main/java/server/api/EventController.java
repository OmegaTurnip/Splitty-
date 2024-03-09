package server.api;

import commons.Event;
import commons.Transaction;
import org.springframework.data.repository.query.Param;
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

    /**
     * Gets all the events matching the list of invite codes
     * sent in the request body
     * @param invCodes List of invite codes
     * @return List of events
     */
    @GetMapping(path = {"/myEvents"})
    @ResponseBody
    public ResponseEntity<List<Event>> myEvents(@RequestParam("invCodes") List<String> invCodes) {
        List<Event> myE = eventRepository.findAllByInviteCodeIsIn(invCodes);
        return ResponseEntity.ok(myE);
    }

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

    /**
     * Add an event
     * @param event The event to add
     * @return  The event added
     */
    @PostMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {

        eventRepository.save(event); //<---CAUSES PROBLEMS!
        eventRepository.flush();

        return ResponseEntity.ok(event);
    }

    /**
     * Delete an event
     * @param event The event to delete
     * @return The event deleted
     */
    @DeleteMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Event> deleteEvent(@RequestBody Event event) {
        eventRepository.delete(event);
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

    /**
     * Delete a transaction from an event
     * @param id The id of the event
     * @param transaction the transaction to delete
     * @return The transaction deleted
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Event>
        deleteTransaction(@PathVariable("id") Long id,
                          @RequestBody Transaction transaction) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        boolean deleted = event.deleteTransaction(transaction);
        if(!deleted) {
            return ResponseEntity.badRequest().build();
        }
        eventRepository.save(event);
        return ResponseEntity.ok(event);
    }

}
