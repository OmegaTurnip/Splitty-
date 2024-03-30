package server.api;


import commons.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class AdminWebSocketController {

    private final EventController eventController;

    /**
     * Constructor for the AdminWebSocketController
     * @param eventController The event controller
     */
    @Autowired
    public AdminWebSocketController(EventController eventController) {
        this.eventController = eventController;
    }

    /**
     * Save an event through websocket
     * @param e The event to save
     * @return The saved event
     */
    @MessageMapping("/admin/save") // This is /app/admin/save
    @SendTo("/topic/admin")
    public Event restoreEvent(Event e) {
        return eventController.saveEvent(e).getBody();
    }

    /**
     * Delete an event through websocket
     * @param e The event to delete
     * @return The deleted event
     */
    @MessageMapping("/admin/delete") // This is /app/admin/delete
    @SendTo("/topic/admin/delete")
    public Event deleteEvent(Event e) {
        eventController.deleteEvent(e.getId());
        return e;
    }

}
