package client.scenes;

import client.utils.ServerUtils;
import client.utils.UserConfig;
import commons.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

import java.io.IOException;
import java.util.List;

public class StartUpCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField newEvent1;
    @FXML
    private Button newEventButton1;
    @FXML
    private Button joinEventButton1;

    @FXML
    private ListView<String> yourEvents;

    /**
     * Constructor
     * @param server The server.
     * @param mainCtrl The main controller.
     */
    @Inject
    public StartUpCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * To add an event to the user's events using an invitation code.
     */
    public void joinEvent() {

    }

    /**
     * Method for creating an event using the Create Event text field.
     * @throws WebApplicationException May throw errors for reasons
     * such as internal server errors, the event name text field being empty,
     * or other such issues.
     */
    public void createEvent() throws WebApplicationException {
        try {
            Event e = getEvent();
            if (e.getEventName().equals("")) {
                throw new WebApplicationException(
                        "Empty text field! Please write an event name" +
                        " before creating an event.", 422);
            }
            List<String> eventCodes = UserConfig.USER_SETTINGS.getEventCodes();
            eventCodes.add(e.getInviteCode());
            UserConfig.USER_SETTINGS.setEventCodes(eventCodes);

            server.createEvent(getEvent());
            System.out.println("event created!");
        } catch (WebApplicationException e) {
            e.printStackTrace();
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        clearFields();
        mainCtrl.showStartUp();
    }

    private void clearFields() {
        newEvent1.clear();
    }

    /**
     * Gets and constructs the event from the text field
     * @return The event
     */
    public Event getEvent() {
        return new Event(newEvent1.getText());
    }

    /**
     * Refreshes the page and updates the list view.
     */
    public void refresh() {

    }
}
