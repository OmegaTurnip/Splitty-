package client.scenes;

import client.language.Language;
import client.language.Text;
import client.language.TextPage;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Inject;
import commons.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.controlsfx.control.CheckComboBox;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private final EventOverviewCtrl eventOverviewCtrl;

    /**
     * Initializes the controller
     *
     * @param server            .
     * @param mainCtrl          .
     * @param eventOverviewCtrl .
     */
    @Inject
    public AddExpenseCtrl(ServerUtils server, MainCtrl mainCtrl,
                          EventOverviewCtrl eventOverviewCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.eventOverviewCtrl = eventOverviewCtrl;
        this.participantList = new ArrayList<>();
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
        payerSelection();
        tagSelection();
        participantSelection();
        addExpense.setOnAction(event -> registerExpense());
        price.setOnAction(event -> verifyPrice());
        refresh();
    }

    /**
     * Updates expensePayer whenever a payer is selected in ChoiceBox payer
     */
    void payerSelection() {
        payer.setOnAction(event -> {
            Object selectedValue = payer.getValue();
            if (Translator.getTranslation(Text.AddExpense.expensePayerPrompt)
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
            if (Translator.getTranslation(Text.AddExpense.expenseTypePrompt)
                    .equals(selectedValue)) {
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
                                        Text.AddExpense.participantsEveryone)) || allSelected)) {
                            isCheckingAll.set(true);
                            participants.getCheckModel().checkAll();
                        }
                    }
                });
    }


    /**
     * Register the expense added.
     */
    private void registerExpense() {
        getCheckedParticipants();
        Transaction expense = getExpense();
        //TODO: Connect to back-end
    }

    /**
     * Refreshes the page and updates the list view.
     */
    public void refresh() {
        refreshText();
        loadPayers();
        payer.getSelectionModel().select(0);
        loadParticipants();
        loadTags();
        expenseType.getSelectionModel().select(0);
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
        payerChoiceBoxList.add(
                Translator.getTranslation(Text.AddExpense.expensePayerPrompt));
        if (event != null) {
            payerChoiceBoxList.addAll(server.getParticipantsOfEvent(event));
        }
        ObservableList<Object> participantObservableList =
                FXCollections.observableArrayList(payerChoiceBoxList);
        payer.setItems(participantObservableList);
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

    public static class TagListCell extends ListCell<Object> {

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            setFont(Font.font("Arial", 14));

            if (empty || item == null) {
                setBackground(Background.EMPTY);
                setText("");
            } else if (item.equals(
                    Translator.getTranslation(
                            Text.AddExpense.expenseTypePrompt))) {
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
        participantChoiceBoxList.add(Translator.getTranslation(
                Text.AddExpense.participantsEveryone));
        participantChoiceBoxList.add("Test1"); //placeholder
        participantChoiceBoxList.add("Test2"); //placeholder
        participantChoiceBoxList.add("Test3"); //placeholder
        participantChoiceBoxList.add("Test4"); //placeholder
        participantChoiceBoxList.add("Test5"); //placeholder
        participantChoiceBoxList.add("Test6"); //placeholder
        if (event != null) {
            participantChoiceBoxList
                    .addAll(server.getParticipantsOfEvent(event));
        }
        ObservableList<Object> participantObservableList =
                FXCollections.observableArrayList(participantChoiceBoxList);
        participants.getItems().clear();
        participants.getItems().addAll(participantObservableList);
    }


    /**
     * Refreshes the text
     */
    public void refreshText() {
        languages.setText(
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
        participants.setTitle(
                Translator.getTranslation(
                        Text.AddExpense.expenseParticipantsPrompt));
        System.out.println(participants.getTitle());

        int index = payer.getSelectionModel().getSelectedIndex();
        loadPayers();
        payer.getSelectionModel().select(index);
        index = expenseType.getSelectionModel().getSelectedIndex();
        loadTags();
        expenseType.getSelectionModel().select(index);

//        ArrayList<Integer> indices = new ArrayList<>(
//                participants.getCheckModel().getCheckedIndices());
//        List<Object> list = participants.getCheckModel().getCheckedItems();
        loadParticipants();
//        for (Integer i : indices) {
//            participants.getCheckModel().check(i);
//        }

    }

    /**
     * Loads the languages from the config file and adds them
     * with corresponding actions to the menu
     */
    private void fetchLanguages() {
        HashMap<String, Language> languages = Language.languages;

        for (String langKey : languages.keySet()) {
            MenuItem item = new MenuItem(languages.get(langKey)
                    .getNativeName());

            item.setOnAction(event -> setLanguage(langKey));

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
     * Cancels the action in the addParticipant window
     */
    public void cancel() {
        refreshText();
        mainCtrl.showEventOverview(event);
    }


    /**
     * Sets language to German
     *
     * @param language the language in three character String
     */
    public void setLanguage(String language) {
        try {
            UserConfig.get().setUserLanguage(language);
            refreshText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            if (!Objects.equals(o, Translator.getTranslation(
                    Text.AddExpense.participantsEveryone))) {
                participantList.add((Participant) o);
            }
        }
    }

    void verifyPrice() {
        //TODO method to check if the price is valid
    }

    Transaction getExpense() {
        return event.registerDebt(expensePayer,
                expenseName.getText(),
                new Money(new BigDecimal(price.getText()),
                        Currency.getInstance(currency.getValue())),
                participantList, expenseTag);
    }
}
