package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import commons.Event;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;


import static org.junit.jupiter.api.Assertions.assertEquals;



public class EditEventNameTest extends FxRobot {

    @Mock
    private EditEventNameCtrl sut;
    @InjectMocks
    private ServerUtils server;
    @Mock
    private MainCtrl mainCtrlMock;
    @Mock
    private EventOverviewCtrl eventOverviewCtrlMock;
    @Mock
    private Event event;

    @BeforeEach
    public void setup() throws Exception{
        MockitoAnnotations.openMocks(this);
        server.setServer("http://localhost:8080");
        sut = new EditEventNameCtrl(server, mainCtrlMock, eventOverviewCtrlMock);
        event = new Event();
        sut.setEvent(event);
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(Main.class);

        // Create a new Stage and pass it to the start method
        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // Wait for JavaFX thread to finish
        WaitForAsyncUtils.waitForFxEvents();
    }

    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("EditEventName.fxml"));
        loader.setController(sut);
        Parent mainNode = loader.load();
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.cleanupStages();
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    public <T extends Node> T find(final String query){
        return (T) lookup(query).queryAll().iterator().next();
    }

//    @Test
//    public void testSetEmptyEvent() {
//        assertEquals(event.getEventName(), null);
//    }
//
//    @Test
//    public void testSetEvent() {
//        event.setEventName("Test party");
//        assertEquals(event.getEventName(), "Test party");
//    }

//    @Test
//    public void testEditEventName(){
//        String inputText = "Test party";
//        clickOn("#eventName");
//        write(inputText);
//        verifyThat("#eventName", hasText(inputText));
//        }
//
   }

