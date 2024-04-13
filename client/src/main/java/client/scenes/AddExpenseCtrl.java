package client.scenes;

import client.history.Action;
import client.history.ActionHistory;
import client.language.Text;
import client.language.TextPage;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Inject;
import commons.*;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AddExpenseCtrl extends TextPage
        implements Initializable, PriceHandler {

    @FXML
    private Button cancel;
    @FXML
    private Button addExpense;

    //    price of the expense
    @FXML
    private ChoiceBox<String> currency;
    @FXML
    private TextField price;

    //    Payer of the expense
    @FXML
    private ChoiceBox<Object> payer;
    private Participant expensePayer;

    //    Participants in the expense
    @FXML
    private CheckComboBox<Object> participants;
    private List<Participant> participantList;

    //    date of the expense
    @FXML
    private DatePicker date;

    //    expense name
    @FXML
    private TextField expenseName;
    //    tags
    @FXML
    private ComboBox<Object> expenseType;
    private Tag expenseTag;

    private Event event;
    private ServerUtils server;
    private MainCtrl mainCtrl;
    private Transaction expenseToOverwrite;
    private AlertWrapper alertWrapper;
    private ActionHistory actionHistory;
    private EventOverviewCtrl eventOverviewCtrl;
    private LocalDate startUpDate;

    /**
     * Setter
     * @param actionHistory the actionHistory to set
     */
    public void setActionHistory(ActionHistory actionHistory) {
        this.actionHistory = actionHistory;
    }


    /**
     * Initializes the controller
     *
     * @param server   .
     * @param mainCtrl .
     */
    @Inject
    public AddExpenseCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.participantList = new ArrayList<>();
        this.alertWrapper = new AlertWrapper();
    }

    /**
     * Setter
     * @param server the server to set
     */
    public void setServer(ServerUtils server) {
        this.server = server;
    }

    /**
     * Sets alertWrapper
     *
     * @param alertWrapper alertWrapper
     */
    public void setAlertWrapper(AlertWrapper alertWrapper) {
        this.alertWrapper = alertWrapper;
    }

    /**
     * Initialise the expense adding window.
     *
     * @param location  The location used to resolve relative paths for the
     *                  root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object,
     *                  or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetchLanguages();
        loadCurrencies();
        payerSelection();
        tagSelection();
        participantSelection();
        addExpense.setOnAction(event -> {
            getCheckedParticipants();
            try {
                if (verifyInput()) {
                    Transaction expense = getExpense();
                    registerExpense(expense);
                    this.mainCtrl.showEventOverview(this.event);
                }
            } catch (WebApplicationException e) {
                e.printStackTrace();
            }
        });
        date.setValue(LocalDate.now());
        date.setConverter(new MyLocalDateStringConverter("dd/MM/yyyy"));
        refresh();
    }
    private void loadCurrencies() {
        List<String> currencies = new ArrayList<>();
        for (Currency currency : server.getAvailableCurrencies()) {
            currencies.add(currency.getCurrencyCode());
        }
        currencies.sort(Comparator.naturalOrder());
        currency.setItems(FXCollections.observableArrayList(currencies));
        currency.setValue(UserConfig.get()
                .getPreferredCurrency().getCurrencyCode());
    }

    /**
     * Setter
     *
     * @param expenseToOverwrite the expense to set
     */
    public void setExpenseToOverwrite(Transaction expenseToOverwrite) {
        this.expenseToOverwrite = expenseToOverwrite;
    }

    /**
     * Setter
     *
     * @param choiceBox the currency choicebox
     */
    public void setCurrency(ChoiceBox<String> choiceBox) {
        currency = choiceBox;
    }


    /**
     * Setter
     *
     * @param participants the combobox to set
     */
    public void setParticipants(CheckComboBox<Object> participants) {
        this.participants = participants;
    }

    /**
     * Setter
     * @param overviewCtrl The event overview controller to set.
     */
    public void setEventOverviewCtrl(EventOverviewCtrl overviewCtrl) {
        this.eventOverviewCtrl = overviewCtrl;
    }

    /**
     * Getter
     * @return the actionHistory
     */
    public ActionHistory getActionHistory() {
        return actionHistory;
    }

    /**
     * Setter
     * @param startUpDate the startUpDate
     */
    public void setStartUpDate(LocalDate startUpDate) {
        this.startUpDate = startUpDate;
    }

    static class MyLocalDateStringConverter extends StringConverter<LocalDate> {

        private final DateTimeFormatter dateFormatter;
        private AlertWrapper alertWrapper;

        public MyLocalDateStringConverter(String pattern) {
            this.dateFormatter = DateTimeFormatter.ofPattern(pattern);
            alertWrapper = new AlertWrapper();
        }

        @Override
        public String toString(LocalDate date) {
            if (date != null) {
                return dateFormatter.format(date);
            } else {
                return "";
            }
        }

        public void setAlertWrapper(AlertWrapper alertWrapper) {
            this.alertWrapper = alertWrapper;
        }

        @Override
        public LocalDate fromString(String string) {
            if (string != null && !string.isEmpty()) {
                try {
                    return LocalDate.parse(string, dateFormatter);
                } catch (DateTimeParseException e) {
                    alertWrapper.showAlert(Alert.AlertType.ERROR,
                            Translator.getTranslation(
                                    Text.AddExpense.Alert.dateFormatTitle),
                            Translator.getTranslation(
                                    Text.AddExpense.Alert.dateFormatContent));
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    /**
     * Updates expensePayer whenever a payer is selected in ChoiceBox payer
     */
    void payerSelection() {
        payer.setOnAction(event -> {
            if (payer.getSelectionModel().isSelected(0)) {
                expensePayer = null;
            } else {
                expensePayer = (Participant) payer.getValue();
            }
        });
    }

    /**
     * Sets the items of the payer choice box for testing
     *
     * @param participants the items to set
     */
    public void setPayerItems(List<Object> participants) {
        payer.setItems(FXCollections.observableArrayList(participants));
    }

    /**
     * Getter for expensePayer
     *
     * @return the expensePayer
     */
    public Participant getExpensePayer() {
        return expensePayer;
    }

    /**
     * Updates expenseTag whenever an item is selected in expenseType
     */
    public void tagSelection() {
        expenseType.setOnAction(event -> {
            if (expenseType.getSelectionModel().isSelected(0)) {
                expenseTag = null;
            } else {
                expenseTag = (Tag) expenseType.getValue();
            }
        });
    }

    /**
     * Sets the items of the expenseType choicebox for testing
     *
     * @param list the items to set
     */
    public void setExpenseTypeItems(List<Object> list) {
        expenseType.setItems(FXCollections.observableArrayList(list));
    }

    /**
     * Gets the selected expenseTag
     *
     * @return the selected expense tag
     */
    public Tag getExpenseTag() {
        return expenseTag;
    }

    /**
     * Handles which participants are selected at
     * the same time in CheckComboBox participants
     */
    void participantSelection() {
        AtomicBoolean isCheckingAll = new AtomicBoolean(false);
        checkListener(isCheckingAll);
        uncheckListener(isCheckingAll);

    }

    /**
     * Listens for boxes being unchecked and unchecks "Everyone" if anything is
     * deselected and unchecks everything if "Everyone" is deselected
     *
     * @param isCheckingAll whether all fields are check as a boolean
     */
    private void uncheckListener(AtomicBoolean isCheckingAll) {
        participants.getCheckModel().getCheckedItems()
                .addListener((ListChangeListener<Object>) change -> {
                    if (!isCheckingAll.get()) return;
                    while (change.next()) {
                        if (change.wasRemoved()
                                && !change.getRemoved().contains(Translator
                                .getTranslation(
                                        Text.AddExpense
                                                .participantsEveryone))) {
                            isCheckingAll.set(false);
                            participants.getCheckModel().clearCheck(
                                    Translator.getTranslation(
                                            Text.AddExpense
                                                    .participantsEveryone));
                        } else if (change.wasRemoved()
                                && change.getRemoved()
                                .contains(Translator.getTranslation(
                                        Text.AddExpense
                                                .participantsEveryone))) {
                            isCheckingAll.set(false);
                            participants.getCheckModel().clearChecks();

                        }
                    }
                });
    }


    /**
     * Listens for boxes being checked and checks everything if "Everyone"
     * is selected and checks "Everyone" if everything is selected
     *
     * @param isCheckingAll whether all fields are check as a boolean
     */
    private void checkListener(AtomicBoolean isCheckingAll) {
        participants.getCheckModel().getCheckedItems()
                .addListener((ListChangeListener<Object>) change -> {
                    if (isCheckingAll.get()) return;
                    while (change.next()) {
                        boolean allSelected = participants.getCheckModel()
                                .getCheckedItems().size()
                                == participants.getItems().size() - 1;
                        if (change.wasAdded()
                                && (change.getAddedSubList()
                                .contains(Translator.getTranslation(
                                        Text.AddExpense.participantsEveryone))
                                || allSelected)) {
                            isCheckingAll.set(true);
                            participants.getCheckModel().checkAll();
                        }
                    }
                });
    }


    /**
     * Register the expense added.
     * @param expense the expense to register
     */
    public void registerExpense(Transaction expense) {
        if (expenseToOverwrite == null) {
            Transaction returnedE = server.saveTransaction(expense);
//            event.removeTransaction(expense);
            expense.setTransactionId(returnedE.getTransactionId());
//            event.addTransaction(expense);
            System.out.println("Added expense " + expense);
        } else {
            event.removeTransaction(expenseToOverwrite);
            // I reversed the order of this
            // because it looked dangerous
            expense.setTransactionId(
                    expenseToOverwrite.getTransactionId());
            server.saveEvent(event);
            ExpenseEditAction editAction = new ExpenseEditAction(
                    expenseToOverwrite, expense,
                    server, event, eventOverviewCtrl,
                    mainCtrl);
            actionHistory.addAction(editAction);
            System.out.println("Edited expense " + expense);
        }
    }

    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    private boolean verifyInput() {
        if (!verifyPrice(price.getText(), alertWrapper)) {
            return false;
        }
        if (expenseName.getText().isEmpty()
                || expenseName.getText().isBlank()) {
            noName();
            return false;

        }
        if (expensePayer == null
                || !expensePayer.getClass().equals(Participant.class)) {
            noPayer();
            return false;
        }
        try {
            if (date.getValue() == null || date.getPromptText().isEmpty() ||
                    date.getPromptText().isBlank()) {
                throw new DateTimeParseException("",
                        date.getPromptText(), 0);
            }
        } catch (DateTimeParseException e) {
            wrongDate();
            return false;
        }
        if (date.getValue().isAfter(startUpDate)) {
            dateTooFarAhead();
            return false;
        } else if (date.getValue().isBefore(
                LocalDate.of(2000, 1, 1))) {
            dateTooFarBehind();
            return false;
        }

        if (participantList.isEmpty()) {
            noParticipants();
            return false;
        }
        return true;
    }

    private void noName() {
        alertWrapper.showAlert(Alert.AlertType.ERROR,
                Translator.getTranslation(
                        Text.AddExpense.Alert.noNameTitle),
                Translator.getTranslation(
                        Text.AddExpense.Alert.noNameContent));
    }

    private void noParticipants() {
        alertWrapper.showAlert(Alert.AlertType.ERROR,
                Translator.getTranslation(
                        Text.AddExpense.Alert.noParticipantsTitle),
                Translator.getTranslation(
                        Text.AddExpense.Alert.noParticipantsContent));

    }

    private void dateTooFarBehind() {
        alertWrapper.showAlert(Alert.AlertType.ERROR,
                Translator.getTranslation(
                        Text.AddExpense.Alert.oldDateTitle),
                Translator.getTranslation(
                        Text.AddExpense.Alert.oldDateContent));
    }

    private void dateTooFarAhead() {
        alertWrapper.showAlert(Alert.AlertType.ERROR,
                Translator.getTranslation(
                        Text.AddExpense.Alert.futureDateTitle),
                Translator.getTranslation(
                        Text.AddExpense.Alert.futureDateContent));
    }

    private void noPayer() {
        alertWrapper.showAlert(Alert.AlertType.ERROR,
                Translator.getTranslation(
                        Text.AddExpense.Alert.noPayerTitle),
                Translator.getTranslation(
                        Text.AddExpense.Alert.noPayerContent));
    }

    private void wrongDate() {
        alertWrapper.showAlert(Alert.AlertType.ERROR,
                Translator.getTranslation(
                        Text.AddExpense.Alert.dateFormatTitle),
                Translator.getTranslation(
                        Text.AddExpense.Alert.dateFormatContent));
    }

    /**
     * Refreshes the page and updates the list view.
     */
    public void refresh() {
        refreshText();
        currency.setValue(UserConfig.get()
                .getPreferredCurrency().getCurrencyCode());
        loadPayers();
        loadParticipants();
        loadTags();
        if (expenseToOverwrite != null) {
            payer.getSelectionModel().select(expenseToOverwrite.getPayer());
            expenseType.getSelectionModel().select(expenseToOverwrite.getTag());
            addExpense.setText("Edit expense");
            expenseName.setText(expenseToOverwrite.getName());
            price.setText(expenseToOverwrite.getAmount()
                    .getAmount().toString());
            date.setValue(expenseToOverwrite.getDate());
        } else {
            expenseType.getSelectionModel().select(0);
            addExpense.setText("Add expense");
            payer.getSelectionModel().select(0);
            expenseName.clear();
            price.clear();
            date.setValue(startUpDate);
        }
        System.out.println("Page has been refreshed!");
    }

    /**
     * Gets the participants in the event from the server and
     * constructs the items for the Choice payer
     * through an observable list
     */
    private void loadPayers() {
        List<Object> payerChoiceBoxList = new ArrayList<>();
        payerChoiceBoxList.add(
                Translator.getTranslation(Text.AddExpense.expensePayerPrompt));
        if (event != null) {
            payerChoiceBoxList.addAll(event.getParticipants());
        }
        ObservableList<Object> participantObservableList =
                FXCollections.observableArrayList(payerChoiceBoxList);
        payer.setConverter(new ParticipantStringConverter());
        payer.setItems(participantObservableList);
    }

    public class ParticipantStringConverter extends StringConverter<Object> {
        /**
         * ToString for participant in converter
         *
         * @param o the object of type {@code T} to convert
         * @return the normal toString for non-participant
         * objects, the name of the Participant for Participant objects
         */
        @Override
        public String toString(Object o) {
            if (o == null) return null;
            if (!o.getClass().equals(Participant.class))
                return o.toString();
            Participant participant = (Participant) o;
            return participant.getName();
        }

        /**
         * FromString for participants
         *
         * @param string the {@code String} to convert
         * @return a participant if the name exists for the event, else null
         */
        @Override
        public Participant fromString(String string) {
            for (Participant participant : event.getParticipants()) {
                if (participant.getName().equals(string)) return participant;
            }
            return null;
        }
    }

    /**
     * Gets the tags in the event from the server and
     * constructs the items for the ChoiceBox expenseType
     * through an observable list
     */
    private void loadTags() {
        List<Object> tagChoiceboxList = new ArrayList<>();
        tagChoiceboxList.add(
                Translator.getTranslation(Text.AddExpense.expenseTypePrompt));
        if (event != null) {
            tagChoiceboxList.addAll(event.getTags());
        }
        ObservableList<Object> tagsObservableList =
                FXCollections.observableArrayList(tagChoiceboxList);
        expenseType.setItems(tagsObservableList);
        expenseType.setCellFactory(lv -> new TagListCell());
    }

    public class TagListCell extends ListCell<Object> {

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            setFont(Font.font("Arial", 14));

            if (empty || item == null) {
                setBackground(Background.EMPTY);
                setText("");
            } else if (item.equals(
                    expenseType.getItems().get(0))) {
                setBackground(new Background(
                        new BackgroundFill(Color.WHITE, null, null)));
            } else {
                getTagStyle((Tag) item);
            }
        }

        private void getTagStyle(Tag tag) {
            setBackground(new Background(new BackgroundFill(
                    Color.valueOf(tag.getColour()), null, null)));

            setText(tag.getName());
            double red = Color.valueOf(tag.getColour()).getRed();
            double green = Color.valueOf(tag.getColour()).getGreen();
            double blue = Color.valueOf(tag.getColour()).getBlue();

            // Formula for relative luminance specified in
            // the Web Content Accessibility Guidelines (WCAG)
            boolean useWhiteText = 0.2126 * red +
                    0.7152 * green + 0.0722 * blue < 0.5;

            if (useWhiteText) setTextFill(Color.WHITE);
            setOnMouseEntered(event -> {
                setStyle("-fx-background-color: " + tag.getColour() +
                        ", rgba(255, 255, 255, 0.4);");
                if (!useWhiteText) setTextFill(Color.BLACK);
            });

            setOnMouseExited(event -> setStyle("-fx-background-color: " +
                    tag.getColour() + ";"));
        }
    }

    /**
     * Gets the participants in the event from the server and
     * constructs the items for the CheckComboBox participants
     * through an observable list
     */
    private void loadParticipants() {
        List<Object> participantChoiceBoxList = new ArrayList<>();
        participants.setConverter(new ParticipantStringConverter());
        participantChoiceBoxList.add(Translator.getTranslation(
                Text.AddExpense.participantsEveryone));

        if (event != null) {
            participantChoiceBoxList
                    .addAll(event.getParticipants());
        }
        ObservableList<Object> participantObservableList =
                FXCollections.observableArrayList(participantChoiceBoxList);
        participants.getCheckModel().clearChecks();
        participants.getItems().clear();
        participants.setTitle(Translator.getTranslation(
                Text.AddExpense.expenseParticipantsPrompt));
        participants.getItems().addAll(participantObservableList);
    }


    /**
     * Refreshes the text
     */
    @Override
    public void refreshText() {
        languageMenu.setText(
                Translator.getTranslation(Text.Menu.Languages));
        cancel.setText(
                Translator.getTranslation(Text.AddParticipant.Cancel));
        addExpense.setText(
                Translator.getTranslation((
                        Text.AddExpense.Button.addExpenseButton)));
        expenseName.setPromptText(
                Translator.getTranslation(Text.AddExpense.expenseNamePrompt));
        price.setPromptText(
                Translator.getTranslation(Text.AddExpense.expensePricePrompt));
        date.setPromptText(
                Translator.getTranslation(Text.AddExpense.expenseDatePrompt));
        int index = payer.getSelectionModel().getSelectedIndex();
        loadPayers();
        payer.getSelectionModel().select(index);
        index = expenseType.getSelectionModel().getSelectedIndex();
        loadTags();
        expenseType.getSelectionModel().select(index);
        loadParticipants();
    }

    /**
     * Cancels the action in the addParticipant window
     */
    public void cancel() {
        refreshText();
        mainCtrl.showEventOverview(event);
    }

    /**
     * Setter.
     *
     * @param event The event to be set.
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Updates {@code participantList} to the checked participants
     */
    public void getCheckedParticipants() {
        // added below
        participantList.clear();
        var list = participants.getCheckModel().getCheckedItems();
        for (Object o : list) {
            if (o != participants.getItems().get(0)) {
                participantList.add((Participant) o);
            }
        }
    }

    /**
     * sets mainCtrl
     * @param mainCtrl mainCtrl
     */
    public void setMainCtrl(MainCtrl mainCtrl){
        this.mainCtrl = mainCtrl;
    }


    Transaction getExpense() {
        BigDecimal b = new BigDecimal(price.getText().replace(",", "."));
        return event.registerDebt(expensePayer,
                expenseName.getText(),
                new Money(b, Currency.getInstance(currency.getValue())),
                participantList, date.getValue(), expenseTag);
    }

    /**
     * Price to set
     *
     * @param price price
     */

    public void setPrice(TextField price) {
        this.price = price;
    }

    /**
     * Payer of the expense
     *
     * @param expensePayer the person that has paid for the expense
     */
    public void setExpensePayer(Participant expensePayer) {
        this.expensePayer = expensePayer;
    }

    /**
     * Sets the participantlist
     *
     * @param participantList the participantlist to be set
     */
    public void setParticipantList(List<Participant> participantList) {
        this.participantList = participantList;
    }

    /**
     * Getter
     * @return the participantList
     */
    public List<Participant> getParticipantList() {
        return participantList;
    }

    /**
     * Sets the date
     *
     * @param date date to be set
     */
    public void setDate(DatePicker date) {
        this.date = date;
    }

    /**
     * Sets the name of the expense
     *
     * @param expenseName the expenseName
     */
    public void setExpenseName(TextField expenseName) {
        this.expenseName = expenseName;
    }

    /**
     * Sets the expenseTag
     *
     * @param expenseTag the tag to be set
     */

    public void setExpenseTag(Tag expenseTag) {
        this.expenseTag = expenseTag;
    }

    private static class ExpenseEditAction implements Action {
        private Transaction oldTransaction;
        private Transaction newTransaction;
        private ServerUtils server;
        private Event event;
        private EventOverviewCtrl eventOverviewCtrl;
        private MainCtrl mainCtrl;

        public ExpenseEditAction(Transaction oldTransaction,
                                 Transaction newTransaction,
                                 ServerUtils server,
                                 Event event,
                                 EventOverviewCtrl eventOverviewCtrl,
                                 MainCtrl mainCtrl) {
            this.oldTransaction = oldTransaction;
            this.newTransaction = newTransaction;
            this.server = server;
            this.event = event;
            this.eventOverviewCtrl = eventOverviewCtrl;
            this.mainCtrl = mainCtrl;
        }

        @Override
        public void undo() {
            oldTransaction.setTransactionId(newTransaction.getTransactionId());
            event.removeTransaction(newTransaction);
            event.addTransaction(oldTransaction);
            server.saveEvent(event);
            mainCtrl.showEventOverview(event);
        }

        @Override
        public void redo() {
            newTransaction.setTransactionId(oldTransaction.getTransactionId());
            event.removeTransaction(oldTransaction);
            event.addTransaction(newTransaction);
            server.saveEvent(event);
            mainCtrl.showEventOverview(event);
        }
    }
}
