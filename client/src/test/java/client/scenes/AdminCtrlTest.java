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

import java.io.File;
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

    @Mock
    File file;
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
        sut = new AdminCtrl(server, mainCtrl, file);
        Event test1 = new Event("B");
        Event test2 = new Event("A");
        events = List.of(test1, test2);
        sut.setEvents(events);
        sut.setObjectMapper(objectMapper);
    }
}