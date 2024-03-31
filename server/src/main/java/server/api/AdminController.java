package server.api;

import commons.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;

import java.util.List;
import java.util.Random;

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

    /**
     * Generates a new password
     * @return the password as a String
     */
    private static String generatePassword() {
        char[] passwordCharacters = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "abcdefghijklmnopqrstuvwxyz0123456789!#$%&*()?/").toCharArray();
        StringBuilder password = new StringBuilder();
        int passwordLength = new Random().nextInt(5) + 8;
        for (int i = 0; i < passwordLength; i++) {
            int index = new Random().nextInt(passwordCharacters.length);
            password.append(passwordCharacters[index]);
        }
        return password.toString();
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
