package client.scenes;

import client.language.Language;
import client.language.TextPage;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import commons.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.scene.image.Image;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class StartUpCtrl implements Initializable, TextPage {

    private List<Event> currentEvents;

    private final MenuItem removeFromYourEvents =
            new MenuItem("Remove from your events");
    private final ContextMenu contextMenu = new ContextMenu();

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Label yourEventsLabel;

    @FXML
    private TextField newEvent1;
    @FXML
    private TextField joinEvent1;
    @FXML
    private Button newEventButton1;
    @FXML
    private Button joinEventButton1;

    @FXML
    private ListView<Event> yourEvents;

    @FXML
    private Menu languages;
    @FXML
    private Menu adminLogin;
    @FXML
    private MenuItem loginButton;

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
     * Fetches user events
     */
    private void fetchYourEvents() {
        this.currentEvents = new ArrayList<>();
        List<String> codes = server.getUserSettings().getEventCodes();
        for (Event event : server.getMyEvents()) {
            if (codes.contains(event.getInviteCode())) {
                currentEvents.add(event);
            }
        }
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
        fetchYourEvents();
        fetchLanguages();
        newEvent1.setOnAction(event -> createEvent());
        joinEvent1.setOnAction(event -> joinEvent());
        yourEvents.setCellFactory(param -> new EventListCell());
        yourEvents.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DELETE -> {
                    Event selected = yourEvents.
                            getSelectionModel().getSelectedItem();
                    if (selected != null) undoEventJoin(selected);
                    yourEvents.getSelectionModel().clearSelection();
                }
                case ENTER -> {
                    Event selected = yourEvents.
                            getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        mainCtrl.showEventOverview(selected);
                    }
                }
            }
        });
        loginButton.setOnAction(event -> mainCtrl.showAdminPage());

    }

    private void fetchLanguages() {
        HashMap<String, Language> languages = Language.languages;

        for (String langKey : languages.keySet()) {
            MenuItem item = new MenuItem(langKey);

            item.setOnAction(event -> {
                setLanguage(langKey);
            });

            Image image = new Image(languages
                    .get(langKey).getIconFile().toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            item.setGraphic(imageView);
            this.languages.getItems().add(item);
        }
    }

    /**
     * To add an event to the user's events using an invitation code.
     */
    public void joinEvent() {
        String code = getJoinInvCode();
        try {
            List<String> eventCodes = server.getUserSettings().getEventCodes();
            if (eventCodes.contains(code)) {
                throw new WebApplicationException(
                        Translator
                                .getTranslation(
                                        client.language.Text.StartUp
                                        .Alert.alreadyInEvent), 422);
            }
            Event result = server.joinEvent(code);
            currentEvents.add(result);
            eventCodes.add(code);
            server.getUserSettings().setEventCodes(eventCodes);
            System.out.println("Event: "+ result.getEventName() + " joined!");
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
        refresh();
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
                        Translator.getTranslation(
                                client.language.Text
                                        .StartUp.Alert.noEventWritten), 422);
            }
            Event result = server.saveEvent(e);
            List<String> eventCodes = server.getUserSettings().getEventCodes();
            eventCodes.add(result.getInviteCode());
            server.getUserSettings().setEventCodes(eventCodes);
            currentEvents.add(result);
            System.out.println("Event: "+ result.getEventName() + " created!" +
                    " Invite code: " + result.getInviteCode() + " added!" +
                    " Time of last edit: " + result.getLastActivity());
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
        joinEvent1.clear();
    }

    /**
     * Gets and constructs the event from the text field
     * @return The event
     */
    public Event getEvent() {
        return new Event(newEvent1.getText());
    }

    /**
     * Gets the invitation code from the text field
     * @return The invitation code
     */
    public String getJoinInvCode() {
        return joinEvent1.getText();
    }

    /**
     * Refreshes the page and updates the list view.
     */
    public void refresh() {
        ObservableList<Event> observableEvents =
                FXCollections.observableArrayList(currentEvents);
        SortedList<Event> sortedEvents = new SortedList<>(observableEvents);
        sortedEvents.
                setComparator(Comparator
                        .comparing(Event::getLastActivity).reversed());

        yourEvents.setItems(sortedEvents);

        refreshText();

        System.out.println("Page has been refreshed!");
    }

    /**
     * Refreshes the text on the page.
     */
    public void refreshText() {
        newEventButton1.setText(Translator
                .getTranslation(client.language
                        .Text.StartUp.Buttons.NewEventButton));
        joinEventButton1.setText(Translator
                .getTranslation(client.language
                        .Text.StartUp.Buttons.JoinEventButton));
        yourEventsLabel.setText(Translator
                .getTranslation(client.language
                        .Text.StartUp.yourEventsLabel));
        languages.setText(Translator
                .getTranslation(client.language
                        .Text.StartUp.languagesMenu));
        removeFromYourEvents.setText(Translator.
                getTranslation(client.language
                        .Text.StartUp.Menu.removeYourEvents));
    }

    /**
     * Sets language.
     * @param langKey The language which to set to.
     */
    public void setLanguage(String langKey) {
        try {
            UserConfig.get().setUserLanguage(langKey);
            refreshText();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Undo event joining
     * @param selected The event.
     */
    public void undoEventJoin(Event selected) {
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(Translator
                    .getTranslation(client.language
                            .Text.StartUp.Alert.removeEventHeader));
            alert.setHeaderText(null);
            alert.setContentText(Translator
                    .getTranslation(client.language
                            .Text.StartUp.Alert.removeEventContent));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    List<String> eventCodes =
                            server.getUserSettings().getEventCodes();
                    eventCodes.remove(selected.getInviteCode());
                    server.getUserSettings().setEventCodes(eventCodes);
                    currentEvents.remove(selected);
                    System.out.println("Event: "
                            + selected.getEventName()
                            + " removed!");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        refresh();
    }

    /**
     * Getter for yourEvents (for testing)
     * @return yourEvents listview
     */
    public ListView<Event> getYourEvents() {
        return yourEvents;
    }

    private class EventListCell extends ListCell<Event> {
        private final StackPane stackPane = new StackPane();
        private final Text text = new Text();
        {
            setContextMenu(contextMenu);

            stackPane.getStyleClass().add("event-cell");
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            stackPane.getChildren().addAll(text);

            setOnMouseClicked(event -> {
                if (event.getButton()  == MouseButton.PRIMARY) {
                    Event selected = getItem();
                    if (selected != null) {
                        mainCtrl.showEventOverview(selected);
                    }
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
                contextMenu.getItems().clear();
                removeFromYourEvents.setOnAction(event -> {
                    undoEventJoin(item);
                });
                contextMenu.getItems().add(removeFromYourEvents);
            }
        }
    }


}
