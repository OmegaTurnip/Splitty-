package client.scenes;


import client.history.ActionHistory;
import client.language.Language;
import client.language.Text;
import client.utils.UserConfig;
import commons.ParticipantValuePair;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


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
    private Label sumOfExpenses;
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
    private AtomicReference<String> allExpensesStyle;
    @FXML
    private ToggleButton includingExpensesButton;
    private AtomicReference<String> includingExpensesStyle;
    @FXML
    private ToggleButton fromExpensesButton;
    private AtomicReference<String> fromExpensesStyle;
    @FXML
    private HBox buttonBar;
    @FXML
    private ListView<Participant> participantsListView;
    @FXML
    private ListView<Transaction> expensesListView;
    @FXML
    private MenuItem returnToOverview;
    @FXML
    private Menu rtoButton;
    private ServerUtils server;
    private MainCtrl mainCtrl;

    private AlertWrapper alertWrapper;

    private TransactionCellController transactionCellController;
    private ActionHistory actionHistory;
    private Map<Participant, BigDecimal> participantBalances;

    /**
     * Initializes the controller
     *
     * @param server   .
     * @param mainCtrl .
     */
    @Inject
    public EventOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.alertWrapper = new AlertWrapper();
        this.actionHistory = new ActionHistory();
    }

    /**
     * Initialise the page.
     *
     * @param location  The location used to resolve relative paths
     *                  for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object,
     *                  or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetchLanguages();
        participantBalances = new HashMap<>();
        participantsListView.setCellFactory(param ->
                new ParticipantCellFactory());
        expensesListView.setCellFactory(param ->
                new TransactionCellFactory());
        toggleButtonHover();
        participantsListView.setFocusTraversable(false);
        expensesListView.setFocusTraversable(false);
        expensesListView.getSelectionModel().clearSelection();
        registerForEventUpdate();
        registerForEventDeletion();
        registerForActionHistoryClearing();
        registerForUndoDeleteTransactions();
        registerForDeleteTransactions();
        refresh();
    }

    private void registerForEventUpdate() {
        server.registerForMessages("/topic/admin", Event.class, e -> {
            if (event.equals(e)) event = e; //Overwrite current event
            System.out.println("Received event: " + event.getEventName());
            refresh();
        });
    }

    private void registerForActionHistoryClearing() {
        server.registerForMessages("/topic/actionHistory", Event.class, e -> {
            if (event.equals(e)) {
                actionHistory.clear();
                System.out.println("Action history cleared");
            }

        });
    }

    private void registerForEventDeletion() {
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

        server.registerForMessages("/topic/actionHistory", String.class, b -> {
            actionHistory.clear();
            System.out.println(b);
        });
        refresh();
    }

    private void registerForUndoDeleteTransactions() {
        server.registerForMessages("/topic/undoDelete",
                Transaction.class, t -> {
                    try {
                        Platform.runLater(() -> updateTransactions(t));
                        Platform.runLater(this::refresh);
                        System.out.println("Received transaction: "
                                + t.getName());
                    } catch (Exception e) {
                        System.err.println("An error occurred: "
                                + e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    private void registerForDeleteTransactions() {
        server.registerForMessages("/topic/transaction/delete",
                Transaction.class, t -> {
                    if (t.getLongPollingEventId().equals(event.getId())) {
                        try {
                            event.removeTransaction(t);
                            Platform.runLater(() -> getExpenses());
                            Platform.runLater(this::refresh);
                            System.out.println("Deleted transaction: "
                                    + t.getName());
                        } catch (Exception e) {
                            System.err.println("An error occurred: "
                                    + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                });
    }

    private void toggleButtonHover() {
        fromExpensesStyle = new AtomicReference<>();
        includingExpensesStyle = new AtomicReference<>();
        allExpensesStyle = new AtomicReference<>();

        toggleButtonListener(allExpensesButton, allExpensesStyle);
        toggleButtonListener(fromExpensesButton, fromExpensesStyle);
        toggleButtonListener(includingExpensesButton, includingExpensesStyle);
    }

    private void toggleButtonListener(ToggleButton toggleButton,
                                      AtomicReference<String> previousStyle) {
        toggleButton.setOnMouseEntered(event -> {
            previousStyle.set(toggleButton.getStyle());
            toggleButton.setStyle("-fx-background-color: #CFBDFF;");
        });
        toggleButton.setOnMouseExited(event -> {
            toggleButton.setStyle(previousStyle.get());
        });
    }

    /**
     * Getter.
     *
     * @return Get action history.
     */
    public ActionHistory getActionHistory() {
        return actionHistory;
    }

    /**
     * Undo the last expense action.
     */
    public void undo() {
        if (actionHistory.hasUndoActions()) {
            actionHistory.undo();
        } else {
//            alertWrapper.showAlert(Alert.AlertType.INFORMATION,
//                    Translator.getTranslation(
//                            Text.EventOverview.Alert.noUndoTitle),
//                    Translator.getTranslation(
//                            Text.EventOverview.Alert.noUndoContent));
        }
    }

    /**
     * Redo the last expense action.
     */
    public void redo() {
        if (actionHistory.hasRedoActions()) {
            actionHistory.redo();
        } else {
//            alertWrapper.showAlert(Alert.AlertType.INFORMATION,
//                    Translator.getTranslation(
//                            Text.EventOverview.Alert.noRedoTitle),
//                    Translator.getTranslation(
//                            Text.EventOverview.Alert.noRedoContent));
        }
    }

    /**
     * Sets the alertWrapper
     *
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
                Currency currency = UserConfig.get().getPreferredCurrency();
                sumOfExpenses.setText("Sum of expenses: " +
                        server.getSumOfAllExpenses(event,
                                currency).getAmount().toString() +
                        " " + currency.getCurrencyCode());
                setParticipantBalances(
                        server.getSharesOfParticipants(event, currency));
                ObservableList<Participant> observableParticipants =
                        FXCollections.observableArrayList(
                                event.getParticipants());
                participantsListView.setItems(observableParticipants);
                ObservableList<Object> participantsEvent =
                        FXCollections.observableArrayList(
                                event.getParticipants());
                participantsListView.getSelectionModel().clearSelection();
                expensesDropDown.setItems(participantsEvent);
                expensesDropDown
                        .setStyle("-fx-selection-bar-text-fill: #fefdfd");
                expensesDropDown.setCellFactory(lv ->
                        new ParticipantListCell());
                expensesDropDown.setConverter(new ParticipantStringConverter());
                getExpenses();
            }

        });


    }

    /**
     * Setter for mainCtrl
     *
     * @param mainCtrl the MainCtrl to set
     */
    public void setMainCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Getter for mainCtrl
     *
     * @return return mainCtrl
     */
    public MainCtrl getMainCtrl() {
        return mainCtrl;
    }

    /**
     * Setter
     *
     * @param server the server to set
     */
    public void setServer(ServerUtils server) {
        this.server = server;
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
                     * as the conversion from string to object is
                     * not implemented.
                     */
                    @Override
                    public Object fromString(String s) {
                        return null;
                    }
                };

        /**
         * Converts the given object to its string
         * representation using the internal converter.
         *
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
         *
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
            setFont(Font.font("System", 14));
            setTextFill(Paint.valueOf("#0d0d0d"));

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
     *
     * @param transaction The transaction that was added.
     * @return Transaction that is added
     */
    public Transaction updateTransactions(Transaction transaction) {
        if (transaction.getLongPollingEventId().equals(event.getId())){
            if(event.getTransactions().isEmpty() ||
                    !event.getTransactions().getLast().getTransactionId()
                            .equals(transaction.getTransactionId())){
                event.addTransaction(transaction);
            }
        }
        getExpenses();
        return transaction;
    }


    /**
     * Makes sure that the all threads stop
     */

    public void stop() {
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
            selected.setStyle("-fx-background-color: #331575;" +
                    "-fx-text-fill: #fefdfd");
            fromExpensesStyle.set(fromExpensesButton.getStyle());
            allExpensesStyle.set(allExpensesButton.getStyle());
            includingExpensesStyle.set(includingExpensesButton.getStyle());
            for (Node node : buttonBar.getChildren()) {
                ToggleButton button = (ToggleButton) node;
                if (button != selected) {
                    button.setStyle("-fx-background-color: #E0DDF2; " +
                            "-fx-text-fill: #0d0d0d");
                }
            }
            if (!choice.equals("AllExpenses") && participant == null) {
                alertWrapper.showAlert(Alert.AlertType.ERROR,
                        Translator.getTranslation(
                                Text.EventOverview.Alert.notSelectedTitle),
                        Translator.getTranslation(
                                Text.EventOverview.Alert.notSelectedContent));
                selectExpenses.getSelectedToggle().setSelected(false);
                selectExpenses.selectToggle(allExpensesButton);
                getExpenses();
                return;
            }
            List<Transaction> transactionList =
                    event.getTransactions().stream()
                            .filter(t -> !t.isPayoff()).toList();
            transactions =
                    FXCollections.observableArrayList(transactionList);
            showSelectedExpenses(selected, participant, transactions);
        }
    }

    /**
     * Shows the selected expenses.
     *
     * @param selected     The selected toggle.
     * @param participant  The participant.
     * @param transactions The transactions.
     */
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public void showSelectedExpenses(ToggleButton selected,
                                     Participant participant,
                                     ObservableList<Transaction> transactions) {
        String choice = selected.getId();
        expensesListView.getItems().clear();
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
                    for (Participant p : transaction.getParticipants()) {
                        if (p.equals(participant)) {
                            transactionsParticipant.add(transaction);
                        }
                    }
                }
                expensesListView.setItems(transactionsParticipant);
                expensesListView.getSelectionModel().clearSelection();
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
        if (transactionCellController != null) {
            transactionCellController.refreshText();
            expensesListView.refresh();
        }
        if (event != null) eventNameLabel.setText(event.getEventName());
        refreshIcon(Translator.getCurrentLanguage().getLanguageCode(),
                languageMenu, Language.languages);
    }

    /**
     * Add participant to event
     */
    public void addParticipant() {
        mainCtrl.showAddParticipant(event);
    }

    /**
     * Add expense to the event
     */
    public void addExpense() {
        mainCtrl.showAddExpense(event);
    }

    /**
     * Switches to edit name scene
     */
    public void editName() {
        mainCtrl.showEditName(event);
    }

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
                String participantInfo = item.getName();
                if (participantBalances.containsKey(item)) {
                    participantInfo += " has paid " +
                            participantBalances.get(item);
                }
                controller.setParticipantCellLabelText(participantInfo);
                controller.setEvent(event);
                controller.setParticipant(item);
                controller.setServer(server);
                controller.setMainController(mainCtrl);
                controller.setEventOverviewCtrl(EventOverviewCtrl.this);
                controller.setActionHistory(actionHistory);
                setText(null);
                setGraphic(loader.getRoot());
            }

        }
    }

    /**
     * Makes a map out of the balances for participants
     * @param participantValuePairSet the set of participantvaluepairs
     */
    public void setParticipantBalances(Set<ParticipantValuePair>
                                               participantValuePairSet) {
        participantBalances.clear();
        for (ParticipantValuePair p : participantValuePairSet) {
            participantBalances.put(p.participant(), p.money().getAmount());
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
                transactionCellController.setServer(server);
                transactionCellController.setTransactionData(transaction);
                transactionCellController.setEvent(event);
                transactionCellController.setMainCtrl(mainCtrl);
                transactionCellController.setTransaction(transaction);
                transactionCellController.setEventOverviewCtrl(
                        EventOverviewCtrl.this);
                transactionCellController.setActionHistory(actionHistory);
                setGraphic(loader.getRoot());
            }
        }
    }

    /**
     * Shows the invite code of the event
     */
    public void showInviteCode() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(Translator.getTranslation(client.language.
                Text.EditName.Alert.showInviteTitle));
        ButtonType cancelButtonType = new ButtonType(
                Translator.getTranslation(Text.EditName.cancel),
                ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType copyButtonType = new ButtonType(
                Translator.getTranslation(Text.EditName.copy),
                ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(copyButtonType,
                cancelButtonType);
        Label label = new Label(Translator.getTranslation(
                Text.EditName.Alert.showInviteContent));
        TextField textField = new TextField();
        textField.setText(event.getInviteCode());
        textField.setEditable(false);
        textField.setPrefWidth(275);
        GridPane grid = new GridPane();
        grid.add(label, 1, 1);
        grid.add(textField, 2, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(450);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == copyButtonType) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(event.getInviteCode());
                clipboard.setContent(content);
                mainCtrl.showEventOverview(event);
            } return null;
        });
        dialog.setResultConverter(buttonType -> {
            if (buttonType == cancelButtonType) {
                mainCtrl.showEventOverview(event);
            }
            return null;
        });
        dialog.showAndWait();
    }


    /**
     * Setter.
     *
     * @param event Event to be set.
     */
    public void setEvent(Event event) {
        this.event = event;
        server.registerForUpdates(t -> {
            try {
                Platform.runLater(() -> updateTransactions(t));
                Platform.runLater(this::refresh);
                System.out.println("Received transaction: " + t.getName());
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }, event);
    }

    /**
     * Shows the startUpWindow
     */
    public void returnToOverview() {
        server.stopLongPolling();
        mainCtrl.showStartUp();
    }

    /**
     * Getter for expenseListView
     *
     * @return the ListView of expenses
     */
    public ListView<Transaction> getExpensesListView() {
        return expensesListView;
    }

    /**
     * Changes the page to open debts
     */
    public void showSettleDebtsPage() {
        server.stopLongPolling();
        mainCtrl.showOpenDebts(event);
    }
}
