package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StartUpCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField newEvent1;
    @FXML
    private Button newEventButton1;
    @FXML
    private Button joinEventButton1;

    @FXML
    private ListView<Event> yourEvents;

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
     * Injector setter
     * @param yourEvents yourEvents listview.
     */
    public void setYourEvents(ListView<Event> yourEvents) {
        this.yourEvents = yourEvents;
    }

    /**
     * Initialise the start-up window.
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        yourEvents.setCellFactory(param -> new ListCell<>() {
            private final StackPane stackPane = new StackPane();
            private final Text text = new Text();

            {
                stackPane.getStyleClass().add("event-cell");
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                stackPane.getChildren().addAll(text);
                setOnMouseClicked(event -> {
                    Event selected = getItem();
                    if (selected != null) {
                        System.out.println("Event clicked: " + selected.getEventName());
                        mainCtrl.showEventOverview(selected);
                    }
                });
            }
            @Override
            protected void updateItem(Event item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getEventName() == null) {
                    setGraphic(null);
                    setText(null);
                    stackPane.setVisible(false);
                } else {
                    text.setText(item.getEventName());
                    setGraphic(stackPane);
                    stackPane.setVisible(true);
                }
            }
        });
        refresh();
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
            if (e.getEventName().isEmpty()) {
                throw new WebApplicationException(
                        "Empty text field! Please write an event name" +
                        " before creating an event.", 422);
            }
            List<String> eventCodes = server.getUserSettings().getEventCodes();
            eventCodes.add(e.getInviteCode());
            server.getUserSettings().setEventCodes(eventCodes);
            Event result = server.createEvent(e);
            System.out.println("Event: "+ result.getEventName() + " created!");
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
//        mainCtrl.showStartUp();
        refresh();
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
        yourEvents.getItems().clear();
        List<Event> myEvents = server.getMyEvents();
//        for (Event event : myEvents) {
//            yourEvents.getItems().add(event.getEventName());
//        }
        yourEvents.getItems().addAll(myEvents);
        System.out.println("Page has been refreshed!");
    }


}
