package client.scenes;

import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Event;
import jakarta.ws.rs.client.Client;
import javafx.collections.transformation.SortedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AdminCtrlTest {

    @Mock
    UserConfig userConfig;
    @Mock
    Client client;
    @InjectMocks
    ServerUtils server;

    @Mock
    MainCtrl mainCtrl;

    @Mock
    ObjectMapper objectMapper;

    AdminCtrl sut;

    List<Event> events;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        server.setServer("http://localhost:8080");
        sut = new AdminCtrl(server, mainCtrl);
        Event test1 = new Event("B");
        Event test2 = new Event("A");
        events = List.of(test1, test2);
        sut.setEvents(events);
        sut.setObjectMapper(objectMapper);
    }

    @Test
    void saveToJson() throws JsonProcessingException {
        StringWriter stringWriter = new StringWriter();
        String expectedJson = "expectedJson";
        when(objectMapper.writeValueAsString(events)).thenReturn(expectedJson);
        sut.saveToJsonProper(stringWriter);

        assertEquals(expectedJson, stringWriter.toString());
        Mockito.verify(objectMapper).writeValueAsString(events);
    }
    @Test
    void sortEventsOnName() {
        SortedList<Event> sortedList = sut.sortEvents(Comparator.comparing(Event::getEventName));
        assertEquals("A", sortedList.get(0).getEventName());

    }

    @Test
    void sortEventsOnLastActivity() {
        // B, A. A is latest.
        sut.getEvents().get(0).updateLastActivity();
        // B, A. B is latest.
        SortedList<Event> sortedList = sut.sortEvents(Comparator.comparing(Event::getLastActivity).reversed());
        assertEquals("B", sortedList.get(0).getEventName());
    }

    @Test
    void sortEventsOnCreationDate() {
        LocalDate creationDate = LocalDate.of(1993, 4, 5);
        sut.getEvents().get(0).setEventCreationDate(creationDate);
        SortedList<Event> sortedList = sut.sortEvents(Comparator.comparing(Event::getEventCreationDate).reversed());
        assertEquals(creationDate, sortedList.get(1).getEventCreationDate());
    }
}