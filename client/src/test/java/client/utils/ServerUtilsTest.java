package client.utils;


import commons.Event;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class ServerUtilsTest {

    private ServerUtils sut;

    private String mockURL;

    @Mock
    private UserConfig USER_SETTINGS;

    @Mock
    private Client client;

    @Mock
    private WebTarget webTarget;

    @Mock
    private Invocation.Builder builder;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockURL = "www.mockTest.com/";
        sut = new ServerUtils(USER_SETTINGS, mockURL, client);


    }

    @Test
    void createEventTest() {
        //Mocking the URL
        when(USER_SETTINGS.getServerUrl()).thenReturn(mockURL);

        // Mock behavior of client
        when(client.target(anyString())).thenReturn(webTarget);

        // Mocking WebTarget
        when(webTarget.path(anyString())).thenReturn(webTarget);
        when(webTarget.queryParam(anyString(), anyList())).thenReturn(webTarget);

        // Mocking Invocation.Builder
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON)).thenReturn(builder);

        //Mocking Response
        Event testEvent1 = new Event("testEvent1");
        when(builder.post(Entity.entity(testEvent1, MediaType.APPLICATION_JSON_TYPE), Event.class)).thenReturn(testEvent1);

        Event event = sut.saveEvent(testEvent1);

        assertEquals(event, testEvent1);

    }

    @Test
    void getMyEventsTest() {

        //Mocking the URL
        when(USER_SETTINGS.getServerUrl()).thenReturn(mockURL);

        //Mocking the event codes
        List<String> invCodes = Arrays.asList("INV1", "INV2");
        when(USER_SETTINGS.getEventCodes()).thenReturn(invCodes);

        // Mock behavior of client
        when(client.target(anyString())).thenReturn(webTarget);

        // Mocking WebTarget
        when(webTarget.path(anyString())).thenReturn(webTarget);
        when(webTarget.queryParam(anyString(), anyList())).thenReturn(webTarget);

        // Mocking Invocation.Builder
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON)).thenReturn(builder);


        // Mocking Response
        Event test1 = new Event("Test1");
        Event test2 = new Event("Test2");
        when(builder.get(any(GenericType.class))).thenReturn(Arrays.asList(test1, test2));

        List<Event> events = sut.getMyEvents();

        assertEquals(events.size(), 2);
        assertEquals(events.get(0), test1);

    }
}