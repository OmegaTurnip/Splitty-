package client.scenes;



import client.language.Language;
import client.language.Text;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import client.language.TextPage;
import client.language.Translator;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import commons.Transaction;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class EventOverviewCtrl extends TextPage implements Initializable {

    private Event event;

    private ObservableList<Transaction> transactions;
    private ObservableList<Transaction> transactionsParticipant;
    private ObservableList<Transaction> transactionsPayer;

    @FXML
    private Label eventNameLabel;
    @FXML
    private Label participantsLabel;
    @FXML
    private Label expensesLabel;
    @FXML
    private Button addParticipantButton;
    @FXML
    private Button addExpenseButton;
    @FXML
    private ComboBox<Object> expensesDropDown;
    @FXML
    private Button settleDebtsButton;
    @FXML
    private Button sendInviteButton;
    @FXML
    private ToggleGroup selectExpenses;
    @FXML
    private ToggleButton allExpensesButton;
    @FXML
    private ToggleButton includingExpensesButton;
    @FXML
    private ToggleButton fromExpensesButton;
    @FXML
    private ListView<Participant> participantsListView;
    @FXML
    private ListView<Transaction> expensesListView;
    @FXML
    private MenuItem returnToOverview;
    @FXML
    private Menu rtoButton;
    private final ServerUtils server;
    private MainCtrl mainCtrl;

    private AlertWrapper alertWrapper;

    private TransactionCellController transactionCellController;



    /**
     * Initializes the controller
     * @param server .
     * @param mainCtrl .
     */
    @Inject
    public EventOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.alertWrapper = new AlertWrapper();
    }

    /**
     * Initialise the page.
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
        participantsListView.setCellFactory(param ->
                new ParticipantCellFactory());
        expensesListView.setCellFactory(param ->
                new TransactionCellFactory());
        server.registerForUpdates(t -> {
            updateTransactions(t);
            Platform.runLater(this::refresh);
            System.out.println("Received transaction: " + t.getName());
        }, event);
        server.registerForMessages("/topic/admin", Event.class, e -> {
            if (event.equals(e)) event = e; //Overwrite current event
            System.out.println("Received event: " + event.getEventName());
            refresh();
        });
        server.registerForMessages("/topic/admin/delete", Event.class, e -> {
            if (event.equals(e)) {
                Platform.runLater(() -> {
                    mainCtrl.showStartUp();
                    alertWrapper.showAlert(Alert.AlertType.ERROR,
                            Translator.getTranslation(
                                    Text.EventOverview.Alert.deletedEventTitle),
                            Translator.getTranslation(
                                    Text.EventOverview.Alert.
                                            deletedEventContent)
                    );
                });
            }
        });
        refresh();
    }

    /**
     * Sets the alertWrapper
     * @param alertWrapper alertWrapper
     */
    public void setAlertWrapper(AlertWrapper alertWrapper) {
        this.alertWrapper = alertWrapper;
    }

    /**
     * Refreshes the page.
     */
    public void refresh() {
        Platform.runLater(() -> {
            refreshText();
            if (event != null) {
                ObservableList<Participant> observableParticipants =
                        FXCollections.observableArrayList(
                                event.getParticipants());
                participantsListView.setItems(observableParticipants);
                ObservableList<Object> participantsEvent =
                        FXCollections.observableArrayList(
                                event.getParticipants());
                expensesDropDown.setItems(participantsEvent);
                expensesDropDown.setCellFactory(lv ->
                        new ParticipantListCell());
                expensesDropDown.setConverter(new ParticipantStringConverter());
                getExpenses();
            }
        });


    }

    /**
     * Setter for mainCtrl
     * @param mainCtrl the MainCtrl to set
     */
    public void setMainCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Getter for mainCtrl
     * @return return mainCtrl
     */
    public MainCtrl getMainCtrl() {
        return mainCtrl;
    }

    public static class ParticipantStringConverter
            extends StringConverter<Object> {

        private final StringConverter<Object> participantStringConverter =
                new StringConverter<>() {

        /**
         * Converts the given object to its string representation.
         * @param o The object to convert.
         * @return The string representation of the object's name,
         * or an empty string if the object is null.
         */
                @Override
                public String toString(Object o) {
                    if (o == null) {
                        return "";
                    } else {
                        return ((Participant) o).getName();
                    }
                }

            /**
             * Converts the given string to an object.
             * @param s The string to convert.
             * @return Always returns null,
             * as the conversion from string to object is not implemented.
             */
                @Override
                public Object fromString(String s) {
                    return null;
                }
            };

        /**
         * Converts the given object to its string
         * representation using the internal converter.
         * @param o The object to convert.
         * @return The string representation of the object's name,
         * or an empty string if the object is null.
         */
        @Override
        public String toString(Object o) {
            return participantStringConverter.toString(o);
        }

        /**
         * Converts the given string to an object using the internal converter.
         * @param s The string to convert.
         * @return Always returns null,
         * as the conversion from string to object is not implemented.
         */
        @Override
        public Object fromString(String s) {
            return participantStringConverter.fromString(s);
        }
    }

    public static class ParticipantListCell extends ListCell<Object> {
        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            setFont(Font.font("Arial", 14));
            setTextFill(Color.WHITESMOKE);

            if (empty || item == null) {
                setText("");
            } else {
                Participant participant = (Participant) item;
                setText(participant.getName());
            }
        }
    }

    /**
     * This method adds the transaction to the correct list.
     * @param transaction The transaction that was added.
     * @return Transaction that is added
     */
    public Transaction updateTransactions(Transaction transaction) {
        transactions.add(transaction);
        Participant participant = (Participant) expensesDropDown.getValue();
        if (participant != null &&
                transaction.getParticipants().contains(participant)) {
            transactionsParticipant.add(transaction);
        }
        if (transaction.getPayer().equals(participant)) {
            transactionsPayer.add(transaction);
        }

        return transaction;
    }


    /**
     * Makes sure that the all threads stop
     */

    public void stop(){
        server.stop();
    }

    @FXML
    private void groupOfExpenseSelected(ActionEvent event) {
        getExpenses();
    }

    /**
     * Shows the list of expenses.
     */
    public void getExpenses() {
        Participant participant = (Participant) expensesDropDown.getValue();
        ToggleButton selected =
                (ToggleButton) selectExpenses.getSelectedToggle();

        if (selected != null) {
            String choice = selected.getId();
            if(!choice.equals("AllExpenses") && participant == null){
                alertWrapper.showAlert(Alert.AlertType.ERROR,
                        Translator.getTranslation(
                                Text.EventOverview.Alert.notSelectedTitle),
                        Translator.getTranslation(
                                Text.EventOverview.Alert.notSelectedContent));
            }
            transactions =
                    FXCollections.observableArrayList(event.getTransactions());
            showSelectedExpenses(selected, participant, transactions);
        }
    }

    /**
     * Shows the selected expenses.
     * @param selected The selected toggle.
     * @param participant The participant.
     * @param transactions The transactions.
     */
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public void showSelectedExpenses(ToggleButton selected,
                                     Participant participant,
                                     ObservableList<Transaction> transactions){
        String choice = selected.getId();
        setEvents(transactions);
        switch (choice) {
            case "AllExpenses":
                System.out.println("all clicked");
                expensesListView.setItems(transactions);
                break;
            case "ExpenseIncludingParticipant":
                System.out.println("Including participant clicked");
                transactionsParticipant =
                        FXCollections.observableArrayList();
                for (Transaction transaction : transactions) {
                    for(Participant p : transaction.getParticipants()) {
                        if (p.equals(participant)) {
                            transactionsParticipant.add(transaction);
                        }
                        if (transaction.getPayer().equals(participant)) {
                            transactionsPayer.add(transaction);
                        }
                    }
                }
                expensesListView.setItems(transactionsParticipant);
                break;
            case "ExpensePaidParticipant":
                System.out.println("Paid by participant clicked");
                transactionsPayer =
                        FXCollections.observableArrayList();
                for (Transaction transaction : transactions) {
                    if (transaction.getPayer().equals(participant)) {
                        transactionsPayer.add(transaction);
                    }
                }
                expensesListView.setItems(transactionsPayer);
                break;
        }
    }

    private void setEvents(ObservableList<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            for(Participant p : transaction.getParticipants()) {
                p.setEvent(event);
            }
            transaction.getPayer().setEvent(event);
        }
    }


    /**
     * Refreshes the text of EventOverview
     */
    @Override
    public void refreshText() {
        languageMenu.setText(
                Translator.getTranslation(Text.Menu.Languages));
        participantsLabel.setText(Translator
                .getTranslation(client.language
                        .Text.EventOverview.participantsLabel));
        expensesLabel.setText(Translator
                .getTranslation(client.language
                        .Text.EventOverview.expensesLabel));
        settleDebtsButton.setText(Translator
                .getTranslation(client.language
                        .Text.EventOverview.Buttons.settleDebtsButton));
        sendInviteButton.setText(Translator
                .getTranslation(client.language
                        .Text.EventOverview.Buttons.sendInviteButton));
        allExpensesButton.setText(Translator
                .getTranslation(client.language
                        .Text.EventOverview.Buttons.allExpensesButton));
        includingExpensesButton.setText(Translator
                .getTranslation(client.language
                        .Text.EventOverview.Buttons.includingExpensesButton));
        fromExpensesButton.setText(Translator
                .getTranslation(client.language
                        .Text.EventOverview.Buttons.fromExpensesButton));
        expensesDropDown.setPromptText(Translator
                    .getTranslation(client.language
                            .Text.EventOverview.expensesDropDown));
        if(transactionCellController != null){
            transactionCellController.refreshText();
            expensesListView.refresh();
        }
        if (event != null ) eventNameLabel.setText(event.getEventName());
        refreshIcon(Translator.getCurrentLanguage().getLanguageCode(),
                languageMenu, Language.languages);
    }
    /**
     * Add participant to event
     */
    public void addParticipant(){
        mainCtrl.showAddParticipant(event);
    }

    /**
     * Add expense to the event
     */
    public void addExpense() {
        mainCtrl.showAddExpense(event);
    }


    public void editName() {mainCtrl.showEditName(event);}

    private class ParticipantCellFactory extends ListCell<Participant> {

        private FXMLLoader loader;
        @Override
        protected void updateItem(Participant item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setGraphic(null);
                setText(null);
            } else {
                if (loader == null) {
                    loader = new FXMLLoader(getClass()
                            .getResource(
                                    "/client/scenes/ParticipantCell.fxml"));
                    try {
                        Parent root = loader.load();
                        root.getStylesheets()
                                .add(getClass()
                                        .getResource("style.css")
                                        .toExternalForm());
                        loader.setRoot(root);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ParticipantCellController controller = loader.getController();
                controller.setParticipantCellLabelText(item.getName());
                controller.setEvent(event);
                controller.setParticipant(item);
                controller.setServer(server);
                controller.setMainController(mainCtrl);
                controller.setEventOverviewCtrl(EventOverviewCtrl.this);
                setText(null);
                setGraphic(loader.getRoot());
            }

        }
    }

    private class TransactionCellFactory extends ListCell<Transaction> {

        private FXMLLoader loader;

        @Override
        protected void updateItem(Transaction transaction, boolean empty) {
            super.updateItem(transaction, empty);

            if (transaction == null || empty) {
                setGraphic(null);
                setText(null);
            } else {
                if (loader == null) {
                    loader = new FXMLLoader(getClass()
                            .getResource(
                                    "/client/scenes/TransactionCell.fxml"
                            ));
                    try {
                        Parent root = loader.load();
                        loader.setRoot(root);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                transactionCellController = loader.getController();
                transactionCellController.setTransactionData(transaction);
                transactionCellController.setEvent(event);
                transactionCellController.setServer(server);
                transactionCellController.setMainCtrl(mainCtrl);
                transactionCellController.setTransaction(transaction);
                transactionCellController.setEventOverviewCtrl(
                        EventOverviewCtrl.this);
                setGraphic(loader.getRoot());
            }
        }
    }



    /**
     * Setter.
     * @param event Event to be set.
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Shows the startUpWindow
     */
    public void returnToOverview() {
        mainCtrl.showStartUp();
    }

    /**
     * Getter for expenseListView
     * @return the ListView of expenses
     */
    public ListView<Transaction> getExpensesListView() {
        return expensesListView;
    }
}
