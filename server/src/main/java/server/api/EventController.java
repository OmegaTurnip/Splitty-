package server.api;

import commons.Event;
import commons.Expense;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;

import java.util.List;

@RestController
@RequestMapping("/event")
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
    @PutMapping(path = { "", "/" })
    @ResponseBody
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        eventRepository.save(event);
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
     * Add an expense to an event
     * @param id The id of the event
     * @param expense The expense to add
     * @return Expense added
     */
    @PostMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Event> addExpense(@PathVariable("id") Long id,
                             @RequestBody Expense expense) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        event.addExpense(expense);
        eventRepository.save(event);
        return ResponseEntity.ok(event);
    }

    /**
     * Delete an expense from an event
     * @param id The id of the event
     * @param expense the expense to delete
     * @return The expense deleted
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Event> deleteExpense(@PathVariable("id") Long id,
                                @RequestBody Expense expense) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        boolean deleted = event.deleteExpense(expense);
        if(!deleted) {
            return ResponseEntity.badRequest().build();
        }
        eventRepository.save(event);
        return ResponseEntity.ok(event);
    }

}
