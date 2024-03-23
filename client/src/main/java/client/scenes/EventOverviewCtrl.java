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
import commons.Transaction;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;


public class EventOverviewCtrl implements TextPage, Initializable {

    private Event event;

    @FXML
    private Label eventNameLabel;
    @FXML
    private Label participantsLabel;
    @FXML
    private Label expensesLabel;
    @FXML
    private Menu languages;
    @FXML
    private Button addParticipantButton;
    @FXML
    private Button addExpenseButton;
    @FXML
    private ChoiceBox<String> expensesDropDown;
    @FXML
    private Button settleDebtsButton;
    @FXML
    private Button sendInviteButton;
    @FXML
    private ListView<Participant> participantsListView;
    @FXML
    private ListView<Transaction> expensesListView;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;


    /**
     * Initializes the controller
     * @param server .
     * @param mainCtrl .
     */
    @Inject
    public EventOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
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
    }

    /**
     * Refreshes the page.
     */
    public void refresh() {
        refreshText();
    }
    /**
     * Refreshes the text of EventOverview
     */
    public void refreshText() {
        participantsLabel.setText(Translator
                .getTranslation(Text.EventOverview.participantsLabel));
        expensesLabel.setText(Translator
                .getTranslation(Text.EventOverview.expensesLabel));
        settleDebtsButton.setText(Translator
                .getTranslation(Text.EventOverview.Buttons.settleDebtsButton));
        sendInviteButton.setText(Translator
                .getTranslation(Text.EventOverview.Buttons.sendInviteButton));

        eventNameLabel.setText(event.getEventName());
    }
    /**
     * Add participant to event
     */
    public void addParticipant(){
        mainCtrl.showAddParticipant(event);
        refreshText();
    }

    /**
     * Add expense to the event
     */
    public void addExpense() {

    }

    /**
     * Fetch the languages and add to languages drop down menu.
     */
    private void fetchLanguages() {
        HashMap<String, Language> languages = Language.languages;

        for (String langKey : languages.keySet()) {
            MenuItem item = new MenuItem(langKey);

            item.setOnAction(event -> {
                setLanguage(langKey);
            });

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
     * Set user language.
     * @param langKey The language to set.
     */
    private void setLanguage(String langKey) {
        try {
            UserConfig.get().setUserLanguage(langKey);
            refreshText();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    /**
//     * Still in construction (planning to add name to list of participants)
//     * @param username name to be added to list
//     */
//    public void displayName(String username){
//        this.participantsList.setText(username);
//    }
//
//    /**
//     * Sets language to Dutch
//     */
//    public void setDutch(){
//        try {
//            UserConfig.get().setUserLanguage("nld");
//            refreshText();
//        }catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * Sets language to English
//     */
//    public void setEnglish(){
//        try {
//            UserConfig.get().setUserLanguage("eng");
//            refreshText();
//        }catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * Sets language to German
//     */
//    public void setGerman(){
//        try {
//            UserConfig.get().setUserLanguage("deu");
//            refreshText();
//        }catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }


    /**
     * Setter.
     * @param event Event to be set.
     */
    public void setEvent(Event event) {
        this.event = event;
    }


}
