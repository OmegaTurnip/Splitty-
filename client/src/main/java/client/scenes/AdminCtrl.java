package client.scenes;

import client.language.TextPage;
import client.utils.ServerUtils;
import commons.Event;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private Button saveToJSON;
    @FXML
    private Button loadFromJSON;
    @FXML
    private Button deleteEvent;
    private ArrayList<Event> events;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private String password;

    /**
     * Constructor
     * @param server the server.
     * @param mainCtrl the main controller.
     */
    @Inject
    public AdminCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
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
        rto.setOnAction(event -> mainCtrl.showStartUp());
        refreshText();
    }

    /**
     * Refreshes the contents of the admin page
     */
    public void refresh() {
        ObservableList<Event> eventObservableList =
                FXCollections.observableList(server.getAllEvents(password));
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

    }

    /**
     * Getter for password
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
