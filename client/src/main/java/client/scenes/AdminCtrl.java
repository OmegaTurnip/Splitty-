package client.scenes;

import client.language.Text;
import client.language.TextPage;
import client.language.Translator;
import client.utils.ServerUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class AdminCtrl implements TextPage, Initializable {
    @FXML
    private Label eventsLabel;
    @FXML
    private Menu languages;
    @FXML
    private TableView<Event> eventsTable;
    @FXML
    private TableColumn<Event, String> eventName;
    @FXML
    private TableColumn<Event, LocalDate> creationDate;
    @FXML
    private TableColumn<Event, LocalDateTime> lastActivity;
    @FXML
    private MenuItem rto;
    @FXML
    private Menu rtoHeader;
    @FXML
    private Button saveToJSON;
    @FXML
    private Button loadFromJSON;
    @FXML
    private Button deleteEvent;

    @FXML
    private Button restoreEventBtn;
    @FXML
    private TextField restoreEventTextField;
    @FXML
    private ChoiceBox<String> sortByChoiceBox;
    private List<Event> events;
    private List<Event> restoredEvents;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private ObjectMapper objectMapper;

    /**
     * Constructor
     * @param server the server.
     * @param mainCtrl the main controller.
     */
    @Inject
    public AdminCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        restoredEvents = new ArrayList<>();
        events = new ArrayList<>();
    }

    /**
     * Saves the events to a JSON file.
     */
    public void saveToJson() {
        try (PrintWriter writer = new PrintWriter("client/events.json")) {
            String json = objectMapper.writeValueAsString(events);
            writer.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Saved to JSON");
    }

    /**
     * Loads the events from a JSON file.
     */
    public void loadFromJson() {
        File file = new File("client/events.json");
        try {
            restoredEvents = objectMapper.readValue(file, new TypeReference<List<Event>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Loaded from JSON");
        restoreEventBtn.setVisible(true);
        restoreEventBtn.setManaged(true);
        refresh();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Translator.getTranslation(Text.Admin.Alert.eventLoadedTitle));
        alert.setHeaderText(null);
        TextArea textArea = new TextArea(Translator.getTranslation(Text.Admin.Alert.eventLoadedContent));
        textArea.setEditable(false);
        textArea.setWrapText(true);
        ScrollPane scrollPane = new ScrollPane(textArea);
        alert.getDialogPane().setContent(scrollPane);
        alert.showAndWait();
    }

    /**
     * Restores the selected event retrieved from the JSON dump
     * and restores it to the database.
     */
    public void restoreEvent() {
        String invCode = restoreEventTextField.getText();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Translator.getTranslation(Text.Admin.Alert.restoreEventAlertTitle));
        alert.setHeaderText(null);
        alert.setContentText(Translator.getTranslation(Text.Admin.Alert.restoreEventAlertContent));
        if (!invCode.isEmpty()) {
            restoreEventTextField.clear();
            Event restoredEvent = null;
            for (Event event : restoredEvents) {
                if (event.getInviteCode().equals(invCode)) {
                    restoredEvent = event;
                    break;
                }
            }
            if (restoredEvent != null) {
                events.add(restoredEvent);
                server.saveEvent(restoredEvent);
                refresh();
            } else {
                alert.showAndWait();
            }
        } else {
            alert.showAndWait();
        }
    }

    /**
     * Deletes the selected event from the database.
     */
    public void deleteEvent() {
        Event selectedEvent = eventsTable.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            try {
                events.remove(selectedEvent);
                Response response = server.deleteEvent(selectedEvent);
                if (response.getStatus() == 404) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(Translator.getTranslation(Text.Admin.Alert.deleteEventAlertTitle));
                    alert.setHeaderText(null);
                    alert.setContentText(Translator.getTranslation(Text.Admin.Alert.deleteEventAlertContent));
                    alert.showAndWait();
                }
                refresh();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void sortByEventName() {
        ObservableList<Event> observableEvents =
                FXCollections.observableArrayList(events);
        SortedList<Event> sortedEvents = new SortedList<>(observableEvents);
        sortedEvents.
                setComparator(Comparator
                        .comparing(Event::getEventName));
        eventsTable.setItems(sortedEvents);
    }

    private void sortByCreationDate() {
        ObservableList<Event> observableEvents =
                FXCollections.observableArrayList(events);
        SortedList<Event> sortedEvents = new SortedList<>(observableEvents);
        sortedEvents.
                setComparator(Comparator
                        .comparing(Event::getEventCreationDate));
        eventsTable.setItems(sortedEvents);
    }

    private void sortByLastActivity() {
        ObservableList<Event> observableEvents =
                FXCollections.observableArrayList(events);
        SortedList<Event> sortedEvents = new SortedList<>(observableEvents);
        sortedEvents.
                setComparator(Comparator
                        .comparing(Event::getLastActivity).reversed());
        eventsTable.setItems(sortedEvents);
    }


    /**
     * Initializes the {@code AdminCtrl}
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
        fetchLanguages(languages);
        rto.setOnAction(event -> mainCtrl.showStartUp());
        ObservableList<String> sortOptions = FXCollections.observableArrayList(
                "Title", "Creation Date", "Last Activity");
        sortByChoiceBox.setItems(sortOptions);
        sortByChoiceBox.setValue("Sort By");
        sortByChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    switch (newValue) {
                        case "Title":
                            sortByEventName();
                            break;
                        case "Creation Date":
                            sortByCreationDate();
                            break;
                        case "Last Activity":
                            sortByLastActivity();
                            break;
                    }
                });
        restoreEventBtn.setVisible(false);
        restoreEventBtn.setManaged(false);
    }
    /**
     * Refreshes the contents of the admin page
     */
    public void refresh() {
        events = server.getMyEvents();
        ObservableList<Event> eventObservableList =
                FXCollections.observableList(events);
        eventName.setCellValueFactory(
                new PropertyValueFactory<Event, String>("eventName"));
        creationDate.setCellValueFactory(
                new PropertyValueFactory<Event, LocalDate>(
                        "eventCreationDate"));
        lastActivity.setCellValueFactory(
                new PropertyValueFactory<Event, LocalDateTime>("lastActivity"));
        eventsTable.setItems(eventObservableList);
        refreshText();
    }

    /**
     * Refreshes the text of the admin page
     */
    @Override
    public void refreshText() {

        rto.setText(Translator.getTranslation(Text.Menu.ReturnToOverview));
        rtoHeader.setText(Translator.getTranslation(Text.Menu.ReturnToOverview));
        languages.setText(Translator.getTranslation(Text.Menu.Languages));

        String titleTranslation = Translator.getTranslation(Text.Admin.title);
        eventsLabel.setText(Translator.getTranslation(Text.Admin.eventsLabel));
        String creationDateTranslation = Translator.getTranslation(Text.Admin.creationDate);
        String lastActivityTranslation = Translator.getTranslation(Text.Admin.lastActivity);

        eventName.setText(titleTranslation);
        creationDate.setText(creationDateTranslation);
        lastActivity.setText(lastActivityTranslation);
        saveToJSON.setText(Translator.getTranslation(Text.Admin.Buttons.saveToJSON));
        loadFromJSON.setText(Translator.getTranslation(Text.Admin.Buttons.loadFromJSON));
        deleteEvent.setText(Translator.getTranslation(Text.Admin.Buttons.deleteEvent));
        restoreEventBtn.setText(Translator.getTranslation(Text.Admin.Buttons.restoreEvent));

        sortByChoiceBox.setValue(Translator.getTranslation(Text.Admin.sortByChoiceBox));
        ObservableList<String> sortOptions = FXCollections.observableArrayList(
                titleTranslation, creationDateTranslation, lastActivityTranslation);
        sortByChoiceBox.setItems(sortOptions);

    }
}
