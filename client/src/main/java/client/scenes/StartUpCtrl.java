package client.scenes;

import client.language.Language;
import client.language.TextPage;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import commons.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;


import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class StartUpCtrl extends TextPage implements Initializable {

    private List<Event> currentEvents;

    private final MenuItem removeFromYourEvents =
            new MenuItem("Remove from your events");
    private ContextMenu contextMenu;

    @FXML
    private Menu currencyMenu1;

    private ServerUtils server;
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

    /**
     * Setter for newEvent1 (for testing w dependency injection)
     * @param newEvent1 The new event text field
     */
    public void setNewEvent1(TextField newEvent1) {
        this.newEvent1 = newEvent1;
    }

    /**
     * Setter for joinEvent1 (for testing w dependency injection)
     * @param joinEvent1 The join event text field
     */
    public void setJoinEvent1(TextField joinEvent1) {
        this.joinEvent1 = joinEvent1;
    }

    /**
     * Getter for newEvent1 (for testing)
     * @return The new event text field
     */
    public TextField getNewEvent1() {
        return newEvent1;
    }

    /**
     * Getter for currentEvents
     * @return The current events
     */
    public List<Event> getCurrentEvents() {
        return currentEvents;
    }

    /**
     * Getter for joinEvent1 (for testing)
     * @return The join event text field
     */
    public TextField getJoinEvent1() {
        return joinEvent1;
    }

    @FXML
    private Menu adminLogin;
    @FXML
    private MenuItem loginButton;
    private String password;

    private AlertWrapper alertWrapper;

    /**
     * Constructor
     * @param server The server.
     * @param mainCtrl The main controller.
     */
    @Inject
    public StartUpCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.currentEvents = new ArrayList<>();
        this.alertWrapper = new AlertWrapper();
    }

    /**
     * Fetches user events
     */
    private void fetchYourEvents() {
        this.currentEvents = new ArrayList<>();
        try {
            currentEvents.addAll(server.getMyEvents());
        } catch (Exception e) {
            System.out.println("Event codes are " +
                    "empty so it throws 404 exception");
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
        contextMenu = new ContextMenu();
        fetchYourEvents();
        fetchLanguages();
        loadCurrencyMenu();
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
        createLogin();
        registerForDeleteMessages();
        registerForSaveEvents();
    }

    /**
     * Makes a dialog for the login to the admin page
     */
    private void createLogin() {
        loginButton.setOnAction(event -> {
            Dialog<String> loginDialog = new Dialog<>();
            loginDialog.setTitle("Login");
            ButtonType loginButton = new ButtonType("Login",
                    ButtonBar.ButtonData.APPLY);
            loginDialog.getDialogPane().getButtonTypes()
                    .addAll(ButtonType.CANCEL, loginButton);
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Enter admin password");
            loginDialog.getDialogPane().setContent(passwordField);
            loginDialog.setResultConverter(button -> {
                if (button == loginButton) {
                    mainCtrl.showAdminPage(passwordField.getText());
                }
                return null;
            });
            loginDialog.showAndWait();
        });
    }
    private void registerForSaveEvents() {
        List<String> userEvents = server.getUserSettings().getEventCodes();
        server.registerForMessages("/topic/admin", Event.class,
                event -> refresh());
    }

    private void registerForDeleteMessages() {
        server.registerForMessages("/topic/admin/delete", Event.class,
                event -> {
                    currentEvents.remove(event);
                    List<String> codes = server
                            .getUserSettings().getEventCodes();
                    codes.remove(event.getInviteCode());
                    try {
                        server.getUserSettings().setEventCodes(codes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    refresh();
                });
    }

    /**
     * Getter for server
     * @return The server
     */
    public ServerUtils getServer() {
        return server;
    }
    /**
     * To add an event to the user's events using an invitation code.
     */
    public void joinEvent() {
        String code = getJoinInvCode();
        try {
            List<String> eventCodes = server.getUserSettings().getEventCodes();
            if (eventCodes.contains(code)) {
                alertWrapper.showAlert(Alert.AlertType.ERROR,
                        Translator.getTranslation(
                                client.language.Text.StartUp
                                        .Alert.alreadyInEventTitle),
                        Translator.getTranslation(
                                client.language.Text.StartUp
                                        .Alert.alreadyInEvent)
                );
            } else if (code.isEmpty()) {
                alertWrapper.showAlert(Alert.AlertType.ERROR,
                        Translator.getTranslation(
                                client.language.Text.StartUp
                                        .Alert.noEventWrittenTitle),
                        Translator.getTranslation(
                                client.language.Text.StartUp
                                        .Alert.noEventWritten)
                );
            }
            Event result = server.joinEvent(code);
            currentEvents.add(result);
            eventCodes.add(code);
            server.getUserSettings().setEventCodes(eventCodes);
            System.out.println("Event: "+ result.getEventName() + " joined!");
        } catch (WebApplicationException e) {
            e.printStackTrace();
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
            if (e.getEventName() == null || e.getEventName().isEmpty() ||
                    e.getEventName().isBlank()) {
                alertWrapper.showAlert(Alert.AlertType.ERROR,
                        Translator.getTranslation(
                                client.language.Text.StartUp
                                        .Alert.noEventWrittenTitle),
                        Translator.getTranslation(
                                client.language.Text.StartUp
                                        .Alert.noEventWritten));
                return; //Do not create event if no name is given
            }
            Event result = server.createEvent(e);
            List<String> eventCodes = server.getUserSettings().getEventCodes();
            eventCodes.add(result.getInviteCode());
            server.getUserSettings().setEventCodes(eventCodes);
//            currentEvents.add(result); //Might lead to bugs in the UI
            System.out.println("Event: "+ result.getEventName() + " created!" +
                    " Invite code: " + result.getInviteCode() + " added!" +
                    " Time of last edit: " + result.getLastActivity());
        } catch (WebApplicationException e) {
            e.printStackTrace();
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
        if (joinEvent1 != null) return joinEvent1.getText();
        return null;
    }



    /**
     * Refreshes the page and updates the list view.
     */
    public void refresh() {
        Platform.runLater(() -> {
            fetchYourEvents();
            ObservableList<Event> observableEvents =
                    FXCollections.observableArrayList(currentEvents);
            SortedList<Event> sortedEvents = new SortedList<>(observableEvents);
            sortedEvents.
                    setComparator(Comparator
                            .comparing(Event::getLastActivity).reversed());

            yourEvents.setItems(sortedEvents);

            refreshText();
            System.out.println("Page has been refreshed!");
        });

    }

    /**
     * Refreshes the text on the page.
     */
    @Override
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
        languageMenu.setText(Translator
                .getTranslation(client.language
                        .Text.StartUp.languagesMenu));
        currencyMenu1.setText(UserConfig.get().getPreferredCurrency()
                .getCurrencyCode());
        removeFromYourEvents.setText(Translator.
                getTranslation(client.language
                        .Text.StartUp.Menu.removeYourEvents));
        newEvent1.setPromptText(Translator
                .getTranslation(client.language
                        .Text.StartUp.createNewEventLabel));
        joinEvent1.setPromptText(Translator
                .getTranslation(client.language
                        .Text.StartUp.joinEventLabel));
        refreshIcon(Translator.getCurrentLanguage().getLanguageCode(),
                languageMenu, Language.languages);
    }

    /**
     * Undo event joining
     * @param selected The event.
     */
    public void undoEventJoin(Event selected) {
        if (selected != null) {
            ButtonType result = alertWrapper.showAlertButton(
                    Alert.AlertType.CONFIRMATION,
                    Translator
                            .getTranslation(client.language
                                    .Text.StartUp.Alert.removeEventHeader),
                    Translator
                            .getTranslation(client.language
                                    .Text.StartUp.Alert.removeEventContent));
            if (result == ButtonType.OK) {
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

    /**
     *
     * @param events
     */
    public void setEvents(List<Event> events) {
        this.currentEvents = events;
    }
    /**
     * used for testing purposes
     * @param alertWrapper
     */
    public void setAlertWrapper(AlertWrapper alertWrapper) {
        this.alertWrapper = alertWrapper;
    }

    /**
     * used for testing purposes
     * @param server
     */
    public void setServer(ServerUtils server) {
        this.server = server;
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

    private void loadCurrencyMenu() {
        currencyMenu1.getItems().clear();
        List<String> currencies = server.getAvailableCurrencies().stream()
                .map(Currency::getCurrencyCode)
                .sorted()
                .toList();
        for (String currency : currencies) {
            MenuItem item = new MenuItem(currency);
            item.setOnAction(event ->
                setCurrency(Currency.getInstance(currency))
            );
            currencyMenu1.getItems().add(item);
        }
        currencyMenu1.setText(UserConfig.get().getPreferredCurrency()
                .getCurrencyCode());
    }

    private void setCurrency(Currency currency) {
        try {
            server.getUserSettings().setPreferredCurrency(currency);
            currencyMenu1.setText(UserConfig.get().getPreferredCurrency()
                    .getCurrencyCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
