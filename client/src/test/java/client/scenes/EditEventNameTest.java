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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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


public class EditEventNameTest extends ApplicationTest {

    private EditEventNameCtrl sut;
    @Mock
    private ServerUtils server;
    @Mock
    private MainCtrl mainCtrlMock;
    @Mock
    private EventOverviewCtrl eventOverviewCtrlMock;
    @Mock
    private AlertWrapper alertWrapper;
    private Event event;

    private TextField eventName;
    private Button confirmButton;
    private Button cancelButton;

    @Override
    public void start(Stage stage) throws Exception {
        try (MockedStatic<UserConfig> userConfigMockedStatic = Mockito.mockStatic(UserConfig.class)) {
            try (MockedConstruction<ServerUtils> mockPaymentService = Mockito.mockConstruction(ServerUtils.class,(mock,context)-> {
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

                Pair<EditEventNameCtrl, Parent> editName = FXML.load(EditEventNameCtrl.class,
                        "client", "scenes", "EditEventName.fxml");

                this.sut = editName.getKey();
                MockitoAnnotations.openMocks(this).close();
                Mockito.when(server.connect(anyString())).thenReturn(Mockito.mock(StompSession.class));

                event = new Event("Test");
                Scene scene = new Scene(editName.getValue());
                stage.setScene(scene);
                stage.show();
                sut.setEvent(event);
                sut.setAlertWrapper(Mockito.mock(AlertWrapper.class));

                eventName = lookup("#eventName").queryAs(TextField.class);
                confirmButton = lookup("#confirmButton").queryAs(Button.class);
                cancelButton = lookup("#cancelButton").queryAs(Button.class);

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

    @Test
    void constructorTest() {
        assertNotNull(sut);
        assertNotNull(server);
        assertNotNull(mainCtrlMock);
    }

    @AfterEach
    void breakDown() {
        Mockito.reset(server );
        Mockito.reset(mainCtrlMock);
    }

    @Test
    public void testSetEmptyEvent() {
        event.setEventName(null);
        assertEquals(event.getEventName(), null);
    }

    @Test
    public void testSetEvent() {
        event.setEventName("Test party");
        assertEquals(event.getEventName(), "Test party");
    }

    @Test
    public void testSameName() {
        String newName = event.getEventName();
        eventName.setText(newName);
        MainCtrl mainCtrlMock = Mockito.mock(MainCtrl.class);
        sut.setMainCtrl(mainCtrlMock);
        doNothing().when(mainCtrlMock).showEventOverview(event);
        sut.changeName();
        assertEquals(newName, event.getEventName());
    }

//    @Test
//    public void testDifferentName() {
//        String newName = "Birthday";
//        eventName.setText(newName);
//        MainCtrl mainCtrlMock = Mockito.mock(MainCtrl.class);
//        sut.setMainCtrl(mainCtrlMock);
//        doNothing().when(mainCtrlMock).showEventOverview(event);
//        when(alertWrapper.showAlertButton(Mockito.any(Alert.AlertType.class),
//        Mockito.anyString(), Mockito.anyString())).thenReturn(ButtonType.OK);
//        sut.changeName();
//        assertEquals(newName, event.getEventName());
//    }


   }

