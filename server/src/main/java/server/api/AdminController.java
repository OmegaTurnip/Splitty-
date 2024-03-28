package server.api;

import commons.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;

import java.util.List;

@Controller
@RequestMapping("/api/admin")
public class AdminController {
    private static final String password = generatePassword();
    private EventRepository eventRepo;


    /**
     * Constructor
     * @param eventRepo the event repository
     */
    public AdminController(EventRepository eventRepo) {
        this.eventRepo = eventRepo;
    }

    private static String generatePassword() {
        //ToDO generate password
        return "hello";
    }
    /**
     * Get all events
     * @return  All events
     */
    @GetMapping(path = {  "/events" })
    @ResponseBody
    public ResponseEntity<List<Event>> allEvents() {
        List<Event> events = eventRepo.findAll();
        return ResponseEntity.ok(events);
    }
    public static String getPassword() { return password;}
    /**
     * Delete an event
     * @param eventId The event to delete
     * @return The event deleted
     */
    @DeleteMapping(path = { "/events/{eventId}" })
    @ResponseBody
    public ResponseEntity<Event> deleteEvent(@PathVariable Long eventId) {
        Event event = eventRepo.findById(eventId).orElse(null);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        eventRepo.delete(event);
        return ResponseEntity.ok(event);
    }
}
