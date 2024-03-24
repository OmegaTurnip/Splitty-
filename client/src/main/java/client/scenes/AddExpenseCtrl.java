package client.scenes;

import client.language.Language;
import client.language.Text;
import client.language.TextPage;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import commons.Tag;
//import commons.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.CheckComboBox;


import java.io.IOException;
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
    private ChoiceBox<Currency> currency;
    @FXML
    private TextField price;

//    Payer of the expense
    @FXML
    private ChoiceBox<Object> payer;
    private Participant expensePayer;

//    Participants in the expense
    @FXML
    private CheckComboBox<Object> participants;
    private Collection<Participant> participantList;

//    date of the expense
    @FXML
    private DatePicker date;

//    expense name
    @FXML
    private TextField expenseName;
//    tags
    @FXML
    private ComboBox<Tag> expenseType;
    private ArrayList<Tag> tags = new ArrayList<>();

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
        participantSelection();
        addExpense.setOnAction(event -> registerExpense());
        refresh();
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

    /**
     * Handles which participants are selected at
     * the same time in CheckComboBox participants
     */
    void participantSelection() {
        AtomicBoolean isCheckingAll = new AtomicBoolean(false);
        checkListener(isCheckingAll);
        allUncheckedListener();
        uncheckListener(isCheckingAll);
    }

    /**
     * Listens for boxes being unchecked and unchecks "Everyone" if anything is
     * deselected and unchecks everything if "Everyone" is deselected
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
                            clearParticipants();
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
                            checkAllParticipants();
                        }
                        if (change.wasAdded()) {
                            participants.setTitle(null);
                        }
                    }
                });
    }


    /**
     * Register the expense added.
     */
    private void registerExpense() {
        getCheckedParticipants();
        //TODO: Connect to back-end
    }

    /**
     * Refreshes the page and updates the list view.
     */
    public void refresh() {
        refreshText();
        loadPayers();
        loadParticipants();
        //TODO: Connect to back-end
        System.out.println("Page has been refreshed!");
    }

    /**
     *
     */
    private void loadPayers() {
        List<Object> payerChoiceBoxList = new ArrayList<>();
        payerChoiceBoxList
                .add("Select the person that paid for the expense");
        if (event != null) {
            payerChoiceBoxList.addAll(server.getParticipantsOfEvent(event));
        }
        ObservableList<Object> participantObservableList =
                FXCollections.observableArrayList(payerChoiceBoxList);
        payer.setItems(participantObservableList);
        if (payer.getValue() == null) payer
                .setValue("Select the person that paid for the expense");
    }

    /**
     * Gets the participants in the event from the server and
     * constructs the items for the CheckComboBox participants
     * through an observable list
     */
    private void loadParticipants() {
        List<Object> participantChoiceBoxList = new ArrayList<>();
        participantChoiceBoxList.add("Everyone");
        participantChoiceBoxList.add("Test1");
        participantChoiceBoxList.add("Test2");
        participantChoiceBoxList.add("Test3");
        participantChoiceBoxList.add("Test4");
        participantChoiceBoxList.add("Test5");
        if (event != null) {
            participantChoiceBoxList
                    .addAll(server.getParticipantsOfEvent(event));
        }
        ObservableList<Object> participantObservableList =
                FXCollections.observableArrayList(participantChoiceBoxList);
        participants.getItems().clear();
        participants.getItems().addAll(participantObservableList);

        if (participants.getCheckModel().getCheckedIndices().isEmpty()) {
            participants.setTitle("Select the people involved in the expense");
        } else {
            participants.setTitle(null);
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
     * Unselects all participants
     */
    public void clearParticipants() {
        for (int i = 0; i < participants.getItems().size(); i++) {
            participants.getCheckModel().clearCheck(i);
        }
        participants.setTitle("Select the people involved in the expense");
    }

    /**
     * Checks all participants in CheckComboBox participants
     */
    public void checkAllParticipants() {
        for (int i = 0; i < participants.getItems().size(); i++) {
            participants.getCheckModel().check(i);
        }
        participants.setTitle(null);
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
            if (!Objects.equals(o, "Everyone")) {
                participantList.add((Participant) o);
            }
        }
    }
////    Transaction getExpense() {
////        Transaction expense = event.registerTransaction(expensePayer,
////        expenseName.getText(), participantList, , );
////        return null;
////    }
}
