package server.api;

import commons.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.EventRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdminControllerTest {

    AdminController sut;
    EventRepository eventRepo;
    @Mock
    SimpMessagingTemplate messagingTemplate;

    List<Event> allEvents;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        eventRepo = new TestEventRepository();
        sut = new AdminController(eventRepo, messagingTemplate);
        allEvents = List.of(new Event("testEvent1"), new Event("testEvent2"));
    }

    @Test
    void allEvents() {
        eventRepo.saveAll(allEvents);
        var retEvents = sut.allEvents();
        assertEquals(retEvents.getBody(), allEvents);
    }

    @Test
    void getPasswordTest() {
        String pass = sut.getPassword();
        assertNotNull(pass);
    }

    @Test
    void deleteEvent() {
        var retEvent = sut.deleteEvent(100L);
        assertTrue(retEvent.getStatusCode().is4xxClientError());
    }
}