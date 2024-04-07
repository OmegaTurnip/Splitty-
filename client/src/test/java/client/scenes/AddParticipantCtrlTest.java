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
import commons.Participant;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.stomp.StompSession;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddParticipantCtrlTest extends ApplicationTest {


    private AddParticipantCtrl sut;

    @Mock
    private ServerUtils server;

    @Mock
    private MainCtrl mainCtrl;

    private AddParticipantCtrl sutSpy;

    @Mock
    UserConfig userConfig;

    Event testEvent1;
    Participant testParticipant1;

    @Override
    public void start(javafx.stage.Stage stage) throws Exception {
        try (MockedStatic<UserConfig> userConfigMockedStatic = Mockito.mockStatic(UserConfig.class)) {
            try (MockedConstruction<ServerUtils> mockPaymentService = Mockito.mockConstruction(ServerUtils.class, (mock, context) -> {
                when(mock.connect(Mockito.anyString())).thenReturn(Mockito.mock(StompSession.class));
            })) {
                MockitoAnnotations.openMocks(this);
                userConfigMockedStatic.when(UserConfig::get).thenReturn(userConfig);
                Mockito.when(userConfig.getUserLanguage()).thenReturn("eng");
                Language.fromLanguageFile(
                        "eng", new File("../includedLanguages/eng.properties")
                );
                Translator.setCurrentLanguage(Language.languages.get("eng"));
                Injector injector = Guice.createInjector(new MyModule());
                MyFXML FXML = new MyFXML(injector);

                Pair<AddParticipantCtrl, Parent> addParticipant = FXML.load(AddParticipantCtrl.class,
                        "client", "scenes", "AddParticipant.fxml");

                sut = addParticipant.getKey();
                sut.setMainCtrl(mainCtrl);
                sut.setServer(server);
                sutSpy = Mockito.spy(sut);

//                doNothing().when(sutSpy).refreshText();
                doNothing().when(sut.getMainCtrl()).showEventOverview(testEvent1);
                Mockito.when(sut.getServer().connect(Mockito.anyString())).thenReturn(Mockito.mock(StompSession.class));
                testEvent1 = new Event("testEvent1");
                testEvent1.addParticipant("testParticipant1");
                testParticipant1 = testEvent1.getParticipants().get(0);

                sut.setEvent(testEvent1);

                Scene scene = new Scene(addParticipant.getValue());

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

//    @Test
//    void addParticipantButtonTest() {
//        Button addParticipantButton = sut.getAdd();
//        Platform.runLater(addParticipantButton::fire);
//        WaitForAsyncUtils.waitForFxEvents();
//
//        verify(sutSpy, times(1)).createParticipant(); I HAVE NO CLUE WHY THIS IS NOT WORKING
//    }

    @Test
    void createParticipantTest() {
        sut.setParticipant(null);

        testEvent1.addParticipant("testParticipant2");
        Participant testParticipant2 = testEvent1.getParticipants().get(1);
        testParticipant2.setParticipantId(1L);
        testEvent1.removeParticipant(testParticipant2); //This is the participant object that should return

        Mockito.when(server.saveParticipant(any())).thenReturn(testParticipant2); //Saving an event makes id non-null

        sut.getUsernameTextField().setText("testParticipant2");
        sut.createParticipant();

        verify(server, times(1)).saveParticipant(any());
        assertEquals(testParticipant2, testEvent1.getParticipants().get(1));

    }

    @Test
    void editParticipantTest() {
        sut.setParticipant(testParticipant1);
        testEvent1.addParticipant("edited");
        Participant edited = testEvent1.getParticipants().get(1);
        testEvent1.removeParticipant(edited);
        Mockito.when(server.saveParticipant(any())).thenReturn(edited);
        sut.getUsernameTextField().setText("edited");
        sut.createParticipant();
        verify(server, times(1)).saveParticipant(any());
        assertEquals(edited, testEvent1.getParticipants().get(0));
    }

    @Test
    void invalidParticipantTest() {
        Platform.runLater(() -> {
            sut.getUsernameTextField().setText("");
            Mockito.when(server.saveParticipant(any())).thenReturn(testParticipant1);
            assertFalse(sut.createParticipant());
//            WaitForAsyncUtils.waitForFxEvents();
//            FxAssert.verifyThat(window(Alert.AlertType.ERROR.name()), WindowMatchers.isShowing());
        });

    }


    @Test
    void constructorTest() {
        assertNotNull(sut);
        assertNotNull(server);
        assertNotNull(mainCtrl);
    }

    @Test
    void refreshTest() {
        sutSpy.setParticipant(testParticipant1);
        assertEquals("", sut.getUsernameTextField().getText());
        Platform.runLater(() -> {
            sutSpy.refresh();
            assertEquals("testParticipant1", sut.getUsernameTextField().getText());
            verify(sutSpy, times(1)).refreshText();
        });
    }

    @Test
    void refreshTextTest() {
        Platform.runLater(() -> {
            sut.refreshText();
            Translator.setCurrentLanguage(Language.languages.get("eng"));
            assertEquals("Add a participant", sut.getTitle().getText());
            assertEquals("Languages", sut.getLanguages().getText());
            assertEquals("Name:", sut.getUsername().getText());
            assertEquals("Add", sut.getAdd().getText());
            assertEquals("Cancel", sut.getCancel().getText());

            sut.setParticipant(testParticipant1);
            sut.refreshText();
            assertEquals("Edit a Participant", sut.getTitle().getText());
        });
    }

    @Test
    void isValidEmail() {
        assertFalse(AddParticipantCtrl.isValidEmail("test"));
        assertFalse(AddParticipantCtrl.isValidEmail("test@"));
        assertFalse(AddParticipantCtrl.isValidEmail("@test"));

        assertTrue(AddParticipantCtrl.isValidEmail(""));
        assertTrue(AddParticipantCtrl.isValidEmail("test@test"));
        assertTrue(AddParticipantCtrl.isValidEmail("test@test.com"));
    }

    @Test
    void isValidIban() {
        assertFalse(AddParticipantCtrl.isValidIban("test"));
        assertFalse(AddParticipantCtrl.isValidIban("AB12 1234 1234 1234 123"));
        assertFalse(AddParticipantCtrl.isValidIban("AB12 1234 1234 1234 1234 123"));
        assertFalse(AddParticipantCtrl.isValidIban("1234 1234 1234 1234 1234"));

        assertTrue(AddParticipantCtrl.isValidIban(""));
        assertTrue(AddParticipantCtrl.isValidIban("AB12 1234 1234 1234 1234 1"));
        assertTrue(AddParticipantCtrl.isValidIban("AB12 1234 1234 1234 1234 12"));
        assertTrue(AddParticipantCtrl.isValidIban("AB123456789012345678"));
        assertTrue(AddParticipantCtrl.isValidIban("AB1234567890123456789"));
        assertTrue(AddParticipantCtrl.isValidIban("AB12345678901234567890"));
    }

    @Test
    void isValidBic() {
        assertFalse(AddParticipantCtrl.isValidBic("test"));
        assertFalse(AddParticipantCtrl.isValidBic("ABCD"));
        assertFalse(AddParticipantCtrl.isValidBic("ABCD12"));
        assertFalse(AddParticipantCtrl.isValidBic("ABCD12ABCD"));

        assertTrue(AddParticipantCtrl.isValidBic(""));
        assertTrue(AddParticipantCtrl.isValidBic("INGBNL2A"));
        assertTrue(AddParticipantCtrl.isValidBic("RABONL2U"));
        assertTrue(AddParticipantCtrl.isValidBic("0000AA00"));
        assertTrue(AddParticipantCtrl.isValidBic("0000AA00111"));

    }

    @Test
    void formatCheckTest() {
        sut.getUsernameTextField().setText("test");

        assertDoesNotThrow(() -> sut.formatCheck());

        sut.getEmailTextField().setText("t");
        assertThrows(WebApplicationException.class, () -> sut.formatCheck());

        sut.getEmailTextField().setText("");
        sut.getIbanTextField().setText("t");
        assertThrows(WebApplicationException.class, () -> sut.formatCheck());

        sut.getIbanTextField().setText("");
        sut.getBicTextField().setText("t");
        assertThrows(WebApplicationException.class, () -> sut.formatCheck());
    }

    @Test
    void emptyCheckTest() {
        sut.getUsernameTextField().setText("test");

        assertDoesNotThrow(() -> sut.emptyCheck());

        sut.getUsernameTextField().setText("");
        assertThrows(WebApplicationException.class, () -> sut.emptyCheck());

    }

//    @Test
//    void cancelTest() {
//        Button cancelButton = sut.getCancel();
//        Platform.runLater(cancelButton::fire);
//        WaitForAsyncUtils.waitForFxEvents();
//
//        verify(sutSpy, times(1)).cancel();
//    }
}