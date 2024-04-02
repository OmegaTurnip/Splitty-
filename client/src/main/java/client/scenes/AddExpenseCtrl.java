package client.scenes;

import client.language.Text;
import client.language.TextPage;
import client.language.Translator;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddExpenseCtrl implements Initializable, TextPage {

    @FXML
    private Menu languages;
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
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Pattern pricePattern;

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
        pricePattern = Pattern.compile("^[0-9]+(?:[.,][0-9]+)?$");
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
        fetchLanguages(languages);
        payerSelection();
        tagSelection();
        participantSelection();
        addExpense.setOnAction(event -> {
            if (registerExpense()) {
                this.mainCtrl.showEventOverview(this.event);
            }
        });
        date.setValue(LocalDate.now());
        date.setConverter(new MyLocalDateStringConverter("dd/MM/yyyy"));
        refresh();
    }

    static class MyLocalDateStringConverter extends StringConverter<LocalDate> {

        private final DateTimeFormatter dateFormatter;

        public MyLocalDateStringConverter(String pattern) {
            this.dateFormatter = DateTimeFormatter.ofPattern(pattern);
        }

        @Override
        public String toString(LocalDate date) {
            if (date != null) {
                return dateFormatter.format(date);
            } else {
                return "";
            }
        }

        @Override
        public LocalDate fromString(String string) {
            if (string != null && !string.isEmpty()) {
                try {
                    return LocalDate.parse(string, dateFormatter);
                } catch (DateTimeParseException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Invalid date format");
                    alert.setHeaderText(null);
                    alert.setContentText("Try entering a date of the " +
                            "format dd/mm/yyyy! " +
                            "You can also pick the date from the calendar.");
                    alert.showAndWait();
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
            Object selectedValue = payer.getValue();
            if ("Select the person that paid for the expense"
                    .equals(selectedValue)) {
                expensePayer = null;
            } else {
                expensePayer = (Participant) payer.getValue();
            }
        });
    }

    void tagSelection() {
        expenseType.setOnAction(event -> {
            Object selectedValue = expenseType.getValue();
            if ("Select the expense type".equals(selectedValue)) {
                expenseTag = null;
            } else {
                expenseTag = (Tag) expenseType.getValue();
            }
        });
    }

    /**
     * Handles which participants are selected at
     * the same time in CheckComboBox participants
     */
    void participantSelection() {
        AtomicBoolean isCheckingAll = new AtomicBoolean(false);
        checkListener(isCheckingAll);
        allUncheckedListener();
        uncheckListener(isCheckingAll);
/*
        participants.getCheckModel().getCheckedItems()
                .addListener((ListChangeListener<Object>) change -> {
                    while (change.next()) {
                          if (change.wasAdded() && participants
                          .getCheckModel().getCheckedItems().size() == 1) {
                            participants.setTitle(null);
                        }
                    }
                });
*/
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
                                && !change.getRemoved().contains("Everyone")) {
                            isCheckingAll.set(false);
                            participants.getCheckModel().clearCheck("Everyone");
                        } else if (change.wasRemoved()
                                && change.getRemoved().contains("Everyone")) {
                            isCheckingAll.set(false);
                            participants.getCheckModel().clearChecks();

                        }
                    }
                });
    }

    /**
     * Sets the title of CheckComboBox participants to default
     * if everything is unchecked
     */
    private void allUncheckedListener() {
        participants.getCheckModel().getCheckedItems()
                .addListener((ListChangeListener<Object>) change -> {
                    while (change.next()) {
                        if (participants.getCheckModel()
                                .getCheckedItems().isEmpty())
                            participants.setTitle("Select the " +
                                    "people involved in the expense");
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
                                .contains("Everyone") || allSelected)) {
                            isCheckingAll.set(true);
                            participants.getCheckModel().checkAll();
                        }
                    }
                });
    }


    /**
     * Register the expense added.
     */
    private boolean registerExpense() {
        getCheckedParticipants();
        try {
            if (verifyInput()) {
                Transaction expense = getExpense();
                server.saveEvent(event);
                System.out.println("Added expense " + expense);
            }
        } catch (WebApplicationException e) {
            e.printStackTrace();
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return false;
        }

        return true;

    }

    private boolean verifyInput() {
        if (!verifyPrice(price.getText())) return false;
        if (expensePayer == null
                || !expensePayer.getClass().equals(Participant.class))
            return false;
        try {
            if (date.getValue() == null) return false;
        } catch (DateTimeParseException e) {
            showAlert("Invalid date format",
                    "Try entering a date of the format dd/mm/yyyy! " +
                            "You can also pick the date from the calendar.");
        }
        return !participantList.isEmpty();
    }

    /**
     * Refreshes the page and updates the list view.
     */
    public void refresh() {
        refreshText();
        loadPayers();
        loadParticipants();
        loadTags();
        //TODO: Connect to back-end
        System.out.println("Page has been refreshed!");
    }

    /**
     * Gets the participants in the event from the server and
     * constructs the items for the Choice payer
     * through an observable list
     */
    private void loadPayers() {
        List<Object> payerChoiceBoxList = new ArrayList<>();
        payerChoiceBoxList
                .add("Select the person that paid for the expense");
        if (event != null) {
            payerChoiceBoxList.addAll(event.getParticipants());
        }
        ObservableList<Object> participantObservableList =
                FXCollections.observableArrayList(payerChoiceBoxList);
        payer.setConverter(new ParticipantStringConverter());
        payer.setItems(participantObservableList);
        if (payer.getValue() == null) payer
                .setValue("Select the person that paid for the expense");
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
        tagChoiceboxList
                .add("Select the expense type");
        if (event != null) {
            tagChoiceboxList.addAll(event.getTags());
        }
        ObservableList<Object> tagsObservableList =
                FXCollections.observableArrayList(tagChoiceboxList);
        expenseType.setItems(tagsObservableList);
        if (expenseType.getValue() == null) expenseType
                .setValue("Select the expense type");
        expenseType.setCellFactory(lv -> new TagListCell());
    }

    public static class TagListCell extends ListCell<Object> {

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            setFont(Font.font("Arial", 14));

            if (empty || item == null) {
                setBackground(Background.EMPTY);
                setText("");
            } else if (item.equals("Select the expense type")) {
                setBackground(new Background(new BackgroundFill(Color.WHITE,
                        CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
                getTagStyle((Tag) item);
            }
        }

        private void getTagStyle(Tag tag) {
            setBackground(new Background(
                    new BackgroundFill(Color.valueOf(tag.getColour()),
                            CornerRadii.EMPTY, Insets.EMPTY)));

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

            setOnMouseExited(event -> {
                setStyle("-fx-background-color: " +
                        tag.getColour() + ";");
            });
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
        participantChoiceBoxList.add("Everyone");
        if (event != null) {
            participantChoiceBoxList
                    .addAll(event.getParticipants());
        }
        ObservableList<Object> participantObservableList =
                FXCollections.observableArrayList(participantChoiceBoxList);
        participants.getItems().clear();
        participants.getItems().addAll(participantObservableList);

        if (participants.getCheckModel().getCheckedIndices().isEmpty()) {
            participants.setTitle("Select the people involved in the expense");
        }
    }


    /**
     * Refreshes the text
     */
    public void refreshText() {
        languages.setText(
                Translator.getTranslation(Text.Menu.Languages));
        cancel.setText(
                Translator.getTranslation(Text.AddParticipant.Cancel)
        );
        //TODO: Make labels for the other text
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
        for (Object o : participants.getCheckModel().getCheckedItems()) {
            if (!Objects.equals(o, "Everyone")) {
                participantList.add((Participant) o);
            }
        }
    }

    void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    boolean verifyPrice(String input) {
        Matcher matcher = pricePattern.matcher(input);

        if (!matcher.matches()) {
            choosePriceAlert(input);
            return false;
        } else return true;

    }

    private void choosePriceAlert(String input) {
        if (input.isEmpty()) {
            showAlert("Invalid price format",
                    "Please enter a price.");
        } else if (!Character.isDigit(input.charAt(0))) {
            showAlert("Invalid price format",
                    "Your price must start with a digit!");
        } else if (input.matches("[a-zA-Z]")) {
            showAlert("Invalid price format",
                    "Your price may not contain letters!");
        } else if (input.chars().filter(ch -> ch == ',').count() > 1
                || input.chars().filter(ch -> ch == '.').count() > 1
                || (input.chars().filter(ch -> ch == ',').count() > 0
                && input.chars().filter(ch -> ch == '.').count() > 0)) {
            showAlert("Invalid price format",
                    "Your price may not contain more" +
                            " than one period or comma!");
            // If none of the above, consider it as general invalid format
        } else if (!Character.isDigit(input.charAt(0))
                || !Character.isDigit(input.charAt(input.length() - 1))) {
            showAlert("Invalid price format",
                    "Your price must start and end with a digit!");
        } else {
            showAlert("Invalid price format",
                    "Your price is not of the correct format!");
        }
    }


    Transaction getExpense() {
        BigDecimal b = new BigDecimal(price.getText());
        return event.registerDebt(expensePayer,
                expenseName.getText(),
                new Money(b,
                        Currency.getInstance("EUR")), //placeholder
//                        Currency.getInstance(currency.getValue())),
                participantList, expenseTag);
    }
}
