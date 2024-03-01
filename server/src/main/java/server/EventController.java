package server;

import commons.Event;
import commons.Expense;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event")
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/")
    @ResponseBody
    public String allEvents() {
        return eventRepository.findAll().toString();
    }

    @PutMapping("/")
    @ResponseBody
    public String addEvent(@RequestBody Event event) {
        eventRepository.save(event);
        return "Event added";
    }

    @DeleteMapping("/")
    @ResponseBody
    public String deleteEvent(@RequestBody Event event) {
        eventRepository.delete(event);
        return "Event deleted";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public String getEvent(@PathVariable("id") Long id) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) {
            return "Event not found";
        }
        return event.toString();
    }

    @PostMapping("/{id}")
    @ResponseBody
    public String addExpense(@PathVariable("id") Long id, @RequestBody Expense expense) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) {
            return "Event not found";
        }
        event.addExpense(expense);
        eventRepository.save(event);
        return "Expense added";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String deleteExpense(@PathVariable("id") Long id, @RequestBody Expense expense) {
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
