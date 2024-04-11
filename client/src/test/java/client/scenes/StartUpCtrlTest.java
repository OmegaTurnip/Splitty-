package client.scenes;

import client.MyFXML;
import client.language.Language;
import client.language.Translator;
import client.utils.ServerUtils;
import commons.Event;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class StartUpCtrlTest {

    List<Event> events;
    StartUpCtrl sut;
    @Mock
    MainCtrl mainCtrl;
    @Mock
    ServerUtils server;
    @Mock
    AlertWrapper alertWrapper;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
//        server.setServer("http://localhost:8080");
        server = mock(ServerUtils.class);
        mainCtrl = mock(MainCtrl.class);
        sut = new StartUpCtrl(server, mainCtrl);
        events = new ArrayList<>();
        alertWrapper = mock(AlertWrapper.class);
        sut.setEvents(events);
        sut.setServer(server);
        sut.setAlertWrapper(alertWrapper);
        sut.setEvents(new ArrayList<>());

        Language.fromLanguageFile(
                "eng", new File("../includedLanguages/eng.properties")
        );
        Translator.setCurrentLanguage(Language.languages.get("eng"));
    }

    @BeforeAll
    public static void setupSpec() throws Exception {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    @AfterEach
    void breakDown() {
        Mockito.reset(server );
        Mockito.reset(mainCtrl);
    }
    @Test
    void createEvent() {
        // make sure that Textfield of newEvent 1 can be adjusted
    }
}
