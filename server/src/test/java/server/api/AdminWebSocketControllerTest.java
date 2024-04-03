package server.api;

import commons.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.EventRepository;
import server.util.DebtSimplifier;

import static org.junit.jupiter.api.Assertions.*;

class AdminWebSocketControllerTest {

    AdminWebSocketController sut;

    EventRepository eventRepository;

    AdminController adminController;
    EventController eventController;
    @Mock
    SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        eventRepository = new TestEventRepository();
        adminController = new AdminController(eventRepository, messagingTemplate);
        eventController = new EventController(eventRepository, null, messagingTemplate);

        sut = new AdminWebSocketController(eventController, adminController);

    }

    @Test
    void constructorTest() {
        AdminWebSocketController awsc = new AdminWebSocketController(eventController, adminController);
        assertNotNull(awsc);
    }

    @Test
    void restoreEventTest() {
        Event testEvent1 = new Event("testEvent1");
        Event event = sut.restoreEvent(testEvent1);
        assertEquals(event, testEvent1);
    }

    @Test
    void deleteEventTest() {
        Event testEvent1 = new Event("testEvent1");
        testEvent1.setId(100L);
        eventRepository.save(testEvent1);
        Event event = sut.deleteEvent(testEvent1);
        assertNull(eventRepository.findById(testEvent1.getId()).orElse(null));
    }
}