package client.utils;

import commons.Event;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javafx.util.Builder;
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
    private  UserConfig USER_SETTINGS;

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
        sut = new ServerUtils(mockURL); // <- Currently doesn't work and needs fixing!

    }

    @Test
    void createEventTest() {
    }

    @Test
    void getMyEventsTest() {
        List<String> invCodes = Arrays.asList("INV1", "INV2");

        when(USER_SETTINGS.getServerUrl()).thenReturn(mockURL);
        //Mocking the event codes
        when(USER_SETTINGS.getEventCodes()).thenReturn(invCodes);

        // Mocking Client
        when(client.target(anyString())).thenReturn(webTarget);

        // Mocking WebTarget
        when(webTarget.path(anyString())).thenReturn(webTarget);
        when(webTarget.queryParam(anyString(), anyList())).thenReturn(webTarget);

        // Mocking Invocation.Builder
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON)).thenReturn(builder);


        // Mocking Response
        List<Event> mockEvents = Arrays.asList(new Event("Test1"), new Event("Test2"));
        Response response = mock(Response.class);
        when(response.readEntity(any(GenericType.class))).thenReturn(mockEvents);
        when(builder.get()).thenReturn(response);

        List<Event> events = sut.getMyEvents();

        assertEquals(events.size(), 2);

    }
}