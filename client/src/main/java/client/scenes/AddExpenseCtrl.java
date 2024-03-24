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
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
//import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;


import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AddExpenseCtrl implements Initializable, TextPage {

    @FXML
    private Menu languages;
    @FXML
    private Button cancel;
    @FXML
    private ChoiceBox<Currency> currency;
    @FXML
    private ChoiceBox<Object> payer;
    @FXML
    private CheckComboBox<Object> participants;
    private Collection<Participant> participantList;
    @FXML
    private DatePicker date;
    @FXML
    private Button addExpense;
    @FXML
    private TextField expenseName;
    @FXML
    private TextField price;
    @FXML
    private ComboBox<Tag> expenseType;
    private Event event;
    private ArrayList<Tag> tags = new ArrayList<>();
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final EventOverviewCtrl eventOverviewCtrl;
    private Participant expensePayer;

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
        addExpense.setOnAction(event -> registerExpense());
        refresh();
    }

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
     * Changes the scene to startUp
     */
    private void backToStartup() {
        //TODO: Set on-action to go back to start-up window
    }

    /**
     * Register the expense added
     */
    private void registerExpense() {
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

    private void loadParticipants() {
        List<Object> participantChoiceBoxList = new ArrayList<>();
        participantChoiceBoxList
                .add("Select the people who took part in the expense");
        participantChoiceBoxList.add("Everyone");
        if (event != null) {
            participantChoiceBoxList
                    .addAll(server.getParticipantsOfEvent(event));
        }
        ObservableList<Object> participantObservableList =
                FXCollections.observableArrayList(participantChoiceBoxList);
        participants.getItems().clear();
            participants.getItems().addAll(participantObservableList);

        if (participants.getCheckModel().getCheckedIndices().isEmpty()) {
            participants.setTitle("Select the person " +
                    "that paid for the expense");
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
        participants.setTitle("Select the person that paid for the expense");
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

//    Transaction getExpense() {
//        Transaction expense = new Transaction(expensePayer,
//        expenseName.getText(), );
//
//    }
}
