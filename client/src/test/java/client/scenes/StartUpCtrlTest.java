package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.language.Language;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.Event;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.stomp.StompSession;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StartUpCtrlTest extends ApplicationTest {

    List<Event> events;
    StartUpCtrl sut;
    @Mock
    MainCtrl mainCtrl;
    @Mock
    UserConfig userSettings;
    @Mock
    ServerUtils server;

    @Mock
    AlertWrapper alertWrapper;

    @Override
    public void start(Stage stage) throws Exception {
        try (MockedStatic<UserConfig> userConfigMockedStatic = Mockito.mockStatic(UserConfig.class)) {
            try (MockedConstruction<ServerUtils> mockPaymentService = Mockito.mockConstruction(ServerUtils.class, (mock, context) -> {
                when(mock.connect(Mockito.anyString())).thenReturn(Mockito.mock(StompSession.class));
            })) {
                this.server = mock(ServerUtils.class);
                this.mainCtrl = mock(MainCtrl.class);
                UserConfig userConfig = Mockito.mock(UserConfig.class);
                userConfigMockedStatic.when(UserConfig::get).thenReturn(userConfig);
                Mockito.when(userConfig.getUserLanguage()).thenReturn("eng");
                Language.fromLanguageFile(
                        "eng", new File("../includedLanguages/eng.properties")
                );
                Translator.setCurrentLanguage(Language.languages.get("eng"));
                Injector injector = Guice.createInjector(new MyModule());
                MyFXML FXML = new MyFXML(injector);
                Pair<StartUpCtrl, Parent> startUp = FXML.load(StartUpCtrl.class,
                        "client", "scenes", "StartUp.fxml");
                // sut = new StartUpCtrl(server, mainCtrl);
                this.sut = startUp.getKey();
                sut.setAlertWrapper(Mockito.mock(AlertWrapper.class));
                MockitoAnnotations.openMocks(this).close();
                Mockito.when(server.connect(Mockito.anyString())).thenReturn(Mockito.mock(StompSession.class));
                Scene scene = new Scene(startUp.getValue());
                stage.setScene(scene);
                stage.show();
            }
        }
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
    void createEvent() throws IOException {
        TextField textField = new TextField("Test");
        sut.setNewEvent1(textField);
        sut.setJoinEvent1(new TextField());
        sut.setYourEvents(new ListView<>());
        List<String> eventCodes = new ArrayList<>();
        Event test = new Event("Test");
        sut.setEvents(events);
        sut.setServer(server);;
        when(server.createEvent(any())).thenReturn(test);
        // Mock the behavior for getUserSettings()
        UserConfig userConfigMock = mock(UserConfig.class);
        when(userConfigMock.getEventCodes()).thenReturn(eventCodes);
        when(server.getUserSettings()).thenReturn(userConfigMock);
        sut.createEvent();

        // Assert
        assertEquals(test, sut.getCurrentEvents().get(0));
    }

}
