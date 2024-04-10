package client.scenes;

import ch.qos.logback.core.net.server.Client;
import client.MyFXML;
import client.MyModule;
import client.language.Language;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.Event;
import commons.Participant;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.stomp.StompSession;
import org.testfx.framework.junit5.ApplicationTest;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class ParticipantCellControllerTest extends ApplicationTest {

    private ParticipantCellController sut;
    @Mock
    private ServerUtils server;
    @Mock
    private MainCtrl mainCtrlMock;
    @Mock
    private Client client;

    @Mock
    UserConfig userConfig;

    @Mock
    private AlertWrapper alertWrapper;

    Event event;


    @Override
    public void start(Stage stage) throws Exception {
        try (MockedStatic<UserConfig> userConfigMockedStatic = Mockito.mockStatic(UserConfig.class)) {
            try (MockedConstruction<ServerUtils> mockPaymentService = Mockito.mockConstruction(ServerUtils.class, (mock, context) -> {
                when(mock.connect(anyString())).thenReturn(Mockito.mock(StompSession.class));
            })) {
                UserConfig userConfig = Mockito.mock(UserConfig.class);
                userConfigMockedStatic.when(UserConfig::get).thenReturn(userConfig);
                Mockito.when(userConfig.getUserLanguage()).thenReturn("eng");
                Language.fromLanguageFile(
                        "eng", new File("../includedLanguages/eng.properties")
                );
                Translator.setCurrentLanguage(Language.languages.get("eng"));
                Injector injector = Guice.createInjector(new MyModule());
                MyFXML FXML = new MyFXML(injector);

                Pair<ParticipantCellController, Parent> editName = FXML.load(ParticipantCellController.class,
                        "client", "scenes", "ParticipantCell.fxml");

                this.sut = editName.getKey();
                MockitoAnnotations.openMocks(this).close();
                Mockito.when(server.connect(anyString())).thenReturn(Mockito.mock(StompSession.class));

                event = new Event("Test");
                Scene scene = new Scene(editName.getValue());
                stage.setScene(scene);
                stage.show();
                sut.setEvent(event);
                sut.setServer(server);
                sut.setAlertWrapper(Mockito.mock(AlertWrapper.class));


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
        Mockito.reset(server);
        Mockito.reset(mainCtrlMock);
    }

    @Test
    void constructorTest() {
        assertNotNull(sut);
        assertNotNull(server);
        assertNotNull(mainCtrlMock);
        assertNotNull(alertWrapper);
    }

    @Test
    public void deleteParticipantTest(){
        Mockito.when(sut.showDeletionAlert()).thenReturn(ButtonType.OK);
        event.addParticipant("Test");
        Participant testParticipant = event.getParticipants().get(0);
        assertEquals(event.getParticipants().size(), 1);
        sut.deleteParticipant(testParticipant);
        assertEquals(event.getParticipants().size(), 0);
    }


}