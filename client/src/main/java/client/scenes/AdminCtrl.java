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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

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
    private ChoiceBox<Event> restoreEventChoiceBox;
    @FXML
    private ChoiceBox<String> sortByChoiceBox;
    private List<Event> events;
    private List<Event> restoredEvents;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private ObjectMapper objectMapper;

    private File file;

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
        file = new File("client/events.json");
    }

    /**
     * Constructor
     * @param server the server.
     * @param mainCtrl the main controller.
     * @param file the file.
     */
    public AdminCtrl(ServerUtils server, MainCtrl mainCtrl, File file) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        restoredEvents = new ArrayList<>();
        events = new ArrayList<>();
        this.file = file;
    }

    /**
     * Saves the events to a JSON file.
     */
    public void saveToJson() {
        try {
            List<Event> tempList;
            Event selectedEvent = eventsTable
                    .getSelectionModel().getSelectedItem();
            if (file.length() == 0) {
                tempList = new ArrayList<>();
            } else {
                tempList = objectMapper.readValue(file,
                        new TypeReference<List<Event>>() {});
            }
            if (tempList.contains(selectedEvent)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(Translator
                        .getTranslation(Text
                                .Admin.Alert.saveToJSONDuplicateTitle));
                alert.setHeaderText(null);
                alert.setContentText(Translator
                        .getTranslation(Text
                                .Admin.Alert.saveToJSONDuplicateContent));
                alert.showAndWait();
                return;
            }

            try (PrintWriter writer = new PrintWriter(file)) {
                saveToJsonProper(selectedEvent, writer, tempList);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Getter for events.
     * @return the events.
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * Saves to JSON with dependency Injection
     * @param selectedEvent the selected event
     * @param writer the writer
     * @param tempList The list of events from the JSON file
     */
    public void saveToJsonProper(Event selectedEvent,
                                 Writer writer, List<Event> tempList) {
        try {
            if (selectedEvent != null) {
                tempList.add(selectedEvent);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(Translator
                        .getTranslation(Text
                                .Admin.Alert.saveToJSONUnselectedTitle));
                alert.setHeaderText(null);
                alert.setContentText(Translator
                        .getTranslation(Text
                                .Admin.Alert.saveToJSONUnselectedContent));
                alert.showAndWait();
            }
            String json = objectMapper.writeValueAsString(tempList);
            writer.write(json);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(Translator
                    .getTranslation(Text
                            .Admin.Alert.saveToJSONSuccessTitle));
            alert.setHeaderText(null);
            alert.setContentText(Translator
                    .getTranslation(Text
                            .Admin.Alert.saveToJSONSuccessContent));
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Saved to JSON");
    }

    /**
     * Setter for objectMapper.
     * @param objectMapper the object mapper.
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Setter for events.
     * @param events the events.
     */
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     * Loads the events from a JSON file.
     */
    public void loadFromJson() {
        try {
            restoredEvents = objectMapper
                    .readValue(file, new TypeReference<List<Event>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Event e : restoredEvents) {
            restoreEventChoiceBox.getItems().add(e);
        }
        System.out.println("Loaded from JSON");
        restoreEventBtn.setVisible(true);
        restoreEventBtn.setManaged(true);
        restoreEventChoiceBox.setVisible(true);
        restoreEventChoiceBox.setManaged(true);
        refresh();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Translator
                .getTranslation(Text.Admin.Alert.eventLoadedTitle));
        alert.setHeaderText(null);
        TextArea textArea = new TextArea(Translator
                .getTranslation(Text.Admin.Alert.eventLoadedContent));
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
        Event eventToRestore = restoreEventChoiceBox.getValue();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Translator
                .getTranslation(Text.Admin.Alert.restoreEventAlertTitle));
        alert.setHeaderText(null);
        alert.setContentText(Translator
                .getTranslation(Text.Admin.Alert.restoreEventAlertContent));
        if (eventToRestore != null) {
//            restoreEventChoiceBox.getSelectionModel().clearSelection();
            if (!events.contains(eventToRestore)) {
                events.add(eventToRestore);
            } else {
                events.remove(eventToRestore);
                events.add(eventToRestore);
            }
            server.send("/app/admin/save",
                    eventToRestore);
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle(Translator
                    .getTranslation(Text
                            .Admin.Alert.restoreEventAlertSuccessTitle));
            alert1.setHeaderText(null);
            alert1.setContentText(Translator
                    .getTranslation(Text
                            .Admin.Alert.restoreEventAlertSuccessContent));
            alert1.showAndWait();
            refresh();
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
                    events.remove(selectedEvent);
                    server.send("/app/admin/delete", selectedEvent);
                    refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void sortByEventName() {
        SortedList<Event> sortedEvents = sortEvents(Comparator
                .comparing(Event::getEventName));
        eventsTable.setItems(sortedEvents);
    }

    private void sortByCreationDate() {
        SortedList<Event> sortedEvents = sortEvents(Comparator
                .comparing(Event::getEventCreationDate).reversed());
        eventsTable.setItems(sortedEvents);
    }

    private void sortByLastActivity() {
        SortedList<Event> sortedEvents = sortEvents(Comparator
                .comparing(Event::getLastActivity).reversed());
        eventsTable.setItems(sortedEvents);
    }

    /**
     * Sorts the events using a comparator
     * @param comparator the comparator to use
     * @return  a sorted list of events
     */
    public SortedList<Event> sortEvents(Comparator<Event> comparator) {
        ObservableList<Event> observableEvents =
                FXCollections.observableArrayList(events);
        return new SortedList<>(observableEvents, comparator);
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
        restoreEventChoiceBox.setVisible(false);
        restoreEventChoiceBox.setManaged(false);

        events = server.getMyEvents();

        server.registerForMessages("/topic/admin", Event.class, e -> {
            events.add(e);
            System.out.println("Received event: " + e.getEventName());
            refresh();
        });

    }
    /**
     * Refreshes the contents of the admin page
     */
    public void refresh() {
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
        rtoHeader.setText(Translator.getTranslation(Text
                .Menu.ReturnToOverview));
        languages.setText(Translator.getTranslation(Text
                .Menu.Languages));
        String titleTranslation = Translator
                .getTranslation(Text.Admin.title);
        eventsLabel.setText(Translator
                .getTranslation(Text.Admin.eventsLabel));
        String creationDateTranslation = Translator
                .getTranslation(Text.Admin.creationDate);
        String lastActivityTranslation = Translator
                .getTranslation(Text.Admin.lastActivity);

        eventName.setText(titleTranslation);
        creationDate.setText(creationDateTranslation);
        lastActivity.setText(lastActivityTranslation);
        saveToJSON.setText(Translator
                .getTranslation(Text.Admin.Buttons.saveToJSON));
        loadFromJSON.setText(Translator
                .getTranslation(Text.Admin.Buttons.loadFromJSON));
        deleteEvent.setText(Translator
                .getTranslation(Text.Admin.Buttons.deleteEvent));
        restoreEventBtn.setText(Translator
                .getTranslation(Text.Admin.Buttons.restoreEvent));

        sortByChoiceBox.setValue(Translator
                .getTranslation(Text.Admin.sortByChoiceBox));
        ObservableList<String> sortOptions = FXCollections.observableArrayList(
                titleTranslation, creationDateTranslation,
                lastActivityTranslation);
        sortByChoiceBox.setItems(sortOptions);

    }
}
