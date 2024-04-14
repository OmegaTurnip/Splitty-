package client.scenes;

import client.language.Language;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class AdminCtrl extends TextPage implements Initializable {
    @FXML
    private Label eventsLabel;
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
    private HashMap<Long, Event> events;
    private HashMap<Long, Event> restoredEvents;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private ObjectMapper objectMapper;

    private AlertWrapper alertWrapper;

    private File file;

    private String password;

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
        fetchLanguages();
        rto.setOnAction(event -> mainCtrl.showStartUp());
        restoreEventBtn.setVisible(false);
        restoreEventBtn.setManaged(false);
        restoreEventChoiceBox.setVisible(false);
        restoreEventChoiceBox.setManaged(false);
        eventsTable.setOnKeyPressed(event -> {
            switch(event.getCode()) {
                case DELETE:
                    deleteEvent();
                    break;
            }
        });
        registerForNewEvent();
        eventName.setCellValueFactory(
                new PropertyValueFactory<Event, String>("eventName"));
        setOnDoubleClickToShowEvent();
        creationDate.setCellValueFactory(
                new PropertyValueFactory<Event, LocalDate>(
                        "eventCreationDate"));
        lastActivity.setCellValueFactory(
                new PropertyValueFactory<Event, LocalDateTime>("lastActivity"));
//        refresh();

    }

    private void setOnDoubleClickToShowEvent() {
        eventName.setCellFactory(col -> {
            return new TableCell<Event, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        setOnMouseClicked(e -> {
                            if (e.getClickCount() == 2) {
                                if (!isEmpty()) {
                                    Event event = getTableView()
                                            .getItems().get(getIndex());
                                    if (event != null) {
                                        mainCtrl.showEventOverview(event);
                                    }
                                }
                            }
                        });
                    }
                }
            };
        });
    }

    private void registerForNewEvent() {
        server.registerForMessages("/topic/admin", Event.class, e -> {
            events.put(e.getId(), e);
            System.out.println("Received event: " + e.getEventName());
            refresh();
        });
    }

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
        this.alertWrapper = new AlertWrapper();
        restoredEvents = new HashMap<>();
        events = new HashMap<>();
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
        restoredEvents = new HashMap<>();
        events = new HashMap<>();
        this.file = file;
    }

    /**
     * Setter for password
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Saves the events to a JSON file.
     */
    public void saveToJson() {
        try {
            HashMap<Long, Event> tempList;
            Event selectedEvent = eventsTable
                    .getSelectionModel().getSelectedItem();
            if (file.length() == 0) {
                tempList = new HashMap<>();
            } else {
                tempList = objectMapper.readValue(file,
                        new TypeReference<HashMap<Long, Event>>() {});
            }
//            if (tempList.contains(selectedEvent)) {
//                tempList.remove(selectedEvent);
//                //Overwriting the event if it exists
//            }

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
     * Sets the alertWrapper
     * @param alertWrapper alertWrapper
     */
    public void setAlertWrapper(AlertWrapper alertWrapper) {
        this.alertWrapper = alertWrapper;
    }

    /**
     * Getter for events.
     * @return the events.
     */
    public List<Event> getEvents() {
        return new ArrayList<>(events.values());
    }

    /**
     * Saves to JSON with dependency Injection
     * @param selectedEvent the selected event
     * @param writer the writer
     * @param tempList The list of events from the JSON file
     */
    public void saveToJsonProper(Event selectedEvent,
                                 Writer writer, HashMap<Long, Event> tempList) {
        try {
            if (selectedEvent != null) {
                tempList.put(selectedEvent.getId(), selectedEvent);
            } else {
                alertWrapper.showAlert(Alert.AlertType.ERROR,
                        Translator.getTranslation(
                                Text.Admin.Alert.JSONUnselectedTitle),
                        (Translator
                                .getTranslation(Text.Admin.Alert.
                                        JSONUnselectedContent))
                );
                return;
            }
            String json = objectMapper.writeValueAsString(tempList);
            writer.write(json);
            alertWrapper.showAlert(Alert.AlertType.INFORMATION,
                    Translator.getTranslation(Text
                                    .Admin.Alert.saveToJSONSuccessTitle),
                    Translator.getTranslation(Text
                                    .Admin.Alert.saveToJSONSuccessContent)
            );
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
        for (Event event : events) {
            this.events.put(event.getId(), event);
        }
    }

    /**
     * Loads the events from a JSON file.
     */
    public void loadFromJson() {
        try {
            restoredEvents = objectMapper
                    .readValue(file,
                            new TypeReference<HashMap<Long, Event>>() {});
        } catch (Exception e) {
            System.out.println("Could not load from JSON");
            e.printStackTrace();
        }
        restoreEventChoiceBox.getItems().clear();
        for (Event e : restoredEvents.values()) {
            restoreEventChoiceBox.getItems().add(e);
        }
        System.out.println("Loaded from JSON");
        restoreEventBtn.setVisible(true);
        restoreEventBtn.setManaged(true);
        restoreEventChoiceBox.setVisible(true);
        restoreEventChoiceBox.setManaged(true);
        refresh();
        alertWrapper.showAlert(Alert.AlertType.INFORMATION,
                Translator.getTranslation
                        (Text.Admin.Alert.eventLoadedTitle),
                Translator
                        .getTranslation(Text.Admin.Alert.eventLoadedContent));
    }

    /**
     * Restores the selected event retrieved from the JSON dump
     * and restores it to the database.
     */
    public void restoreEvent() {
        Event eventToRestore = restoreEventChoiceBox.getValue();
        alertWrapper.showAlert(Alert.AlertType.ERROR,
                Translator.getTranslation(
                        Text.Admin.Alert.restoreEventAlertTitle),
                Translator
                        .getTranslation(Text.Admin.Alert.
                                restoreEventAlertContent)
        );
        if (eventToRestore != null) {
            server.saveEvent(eventToRestore);
            alertWrapper.showAlert(Alert.AlertType.INFORMATION,
                    Translator.getTranslation(Text
                                    .Admin.Alert.restoreEventAlertSuccessTitle),
                    Translator
                            .getTranslation(Text.Admin.Alert.
                                    restoreEventAlertSuccessContent));
            refresh();
        } else {
            alertWrapper.showAlert(Alert.AlertType.ERROR,
                    Translator.getTranslation(
                            Text.Admin.Alert.restoreEventAlertTitle),
                    Translator
                            .getTranslation(Text.Admin.Alert.
                                    restoreEventAlertContent));
            
        }
    }

    /**
     * Deletes the selected event from the database.
     */
    public void deleteEvent() {
        Event selectedEvent = eventsTable.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            ButtonType result = alertWrapper.showAlertButton(
                    Alert.AlertType.CONFIRMATION,
                    Translator.getTranslation(client.language
                                    .Text.StartUp.Alert.removeEventHeader),
                    Translator
                            .getTranslation(client.language
                                    .Text.StartUp.Alert.removeEventContent)
                    );
            if (result == ButtonType.OK) {
                try {
                    events.remove(selectedEvent.getId());
                    server.deleteEvent(selectedEvent, password);
                    refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            alertWrapper.showAlert(Alert.AlertType.ERROR,
                    Translator.getTranslation(Text.Admin.Alert
                            .JSONUnselectedTitle),
                    Translator.getTranslation(Text.Admin.Alert
                            .JSONUnselectedContent));
        }
    }

//    private void sortByEventName() {
//        SortedList<Event> sortedEvents = sortEvents(Comparator
//                .comparing(Event::getEventName));
//        eventsTable.setItems(sortedEvents);
//    }
//
//    private void sortByCreationDate() {
//        SortedList<Event> sortedEvents = sortEvents(Comparator
//                .comparing(Event::getEventCreationDate).reversed());
//        eventsTable.setItems(sortedEvents);
//    }
//
//    private void sortByLastActivity() {
//        SortedList<Event> sortedEvents = sortEvents(Comparator
//                .comparing(Event::getLastActivity).reversed());
//        eventsTable.setItems(sortedEvents);
//    }
//
//    /**
//     * Sorts the events using a comparator
//     * @param comparator the comparator to use
//     * @return  a sorted list of events
//     */
//    public SortedList<Event> sortEvents(Comparator<Event> comparator) {
//        ObservableList<Event> observableEvents =
//                FXCollections.observableArrayList(events);
//        return new SortedList<>(observableEvents, comparator);
//    }



    /**
     * Refreshes the contents of the admin page
     */
    public void refresh() {
        ObservableList<Event> eventObservableList =
                FXCollections.observableArrayList(events.values());
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
        languageMenu.setText(Translator.getTranslation(Text
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
        refreshIcon(Translator.getCurrentLanguage().getLanguageCode(),
                languageMenu, Language.languages);

    }
}
