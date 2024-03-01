package server;

import commons.Event;
import commons.Expense;
import org.springframework.web.bind.annotation.*;

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
     * @return  String of all events
     */
    @GetMapping("/")
    @ResponseBody
    public String allEvents() {
        return eventRepository.findAll().toString();
    }

    /**
     * Add an event
     * @param event The event to add
     * @return  String of the event added
     */
    @PutMapping("/")
    @ResponseBody
    public String addEvent(@RequestBody Event event) {
        eventRepository.save(event);
        return "Event added";
    }

    /**
     * Delete an event
     * @param event The event to delete
     * @return String of the event deleted
     */
    @DeleteMapping("/")
    @ResponseBody
    public String deleteEvent(@RequestBody Event event) {
        eventRepository.delete(event);
        return "Event deleted";
    }

    /**
     * Get an event by id
     * @param id The id of the event
     * @return String of the event
     */
    @GetMapping("/{id}")
    @ResponseBody
    public String getEvent(@PathVariable("id") Long id) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) {
            return "Event not found";
        }
        return event.toString();
    }

    /**
     * Add an expense to an event
     * @param id The id of the event
     * @param expense The expense to add
     * @return String of the expense added
     */
    @PostMapping("/{id}")
    @ResponseBody
    public String addExpense(@PathVariable("id") Long id,
                             @RequestBody Expense expense) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) {
            return "Event not found";
        }
        event.addExpense(expense);
        eventRepository.save(event);
        return "Expense added";
    }

    /**
     * Delete an expense from an event
     * @param id The id of the event
     * @param expense the expense to delete
     * @return String of the expense deleted
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public String deleteExpense(@PathVariable("id") Long id,
                                @RequestBody Expense expense) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) {
            return "Event not found";
        }
        boolean deleted = event.deleteExpense(expense);
        if(!deleted) {
            return "Expense not found";
        }
        eventRepository.save(event);
        return "Expense deleted";
    }

}
