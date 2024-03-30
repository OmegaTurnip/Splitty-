package server.api;

import commons.Event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class EventControllerTest {
    private TestEventRepository eventRepository;
    private EventController sut;

    private Event testEvent1;
    private List<Event> events;
    @Mock
    private SimpMessagingTemplate sim;



    @BeforeEach
    void setup() {
        eventRepository = new TestEventRepository();
        sut = new EventController(eventRepository, sim);
        testEvent1 = new Event("testEvent1");
        testEvent1.setId(100L);
        testEvent1.setInviteCode("43fabbfca0644e5db1d0c1e3cb0d5416");
        events = new ArrayList<>();
        events.add(testEvent1);
    }

//    @Test
//    void saveEventsTest() {
//        var retEvent = sut.saveEvents(events);
//        assertEquals(retEvent.getBody(), events);
//    }

    @Test
    void saveEventTest() {
        var retEvent = sut.saveEvent(testEvent1);
        assertEquals(retEvent.getBody(), testEvent1);
    }

    @Test
    void getEventTest() {
        eventRepository.save(testEvent1);
        var retEvent = sut.getEvent(testEvent1.getId());
        assertEquals(retEvent.getBody(), testEvent1);
    }
//    @Test
//    void getEventByInviteCodeTest() {
//        eventRepository.save(testEvent1);
//        var retEvents = sut.getEventByInviteCode("43fabbfca0644e5db1d0c1e3cb0d5416");
//        assertEquals(retEvents.getBody(), events);
//    }
}
