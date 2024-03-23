package client.scenes;



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
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.List;


public class EventOverviewCtrl implements TextPage {

    private Event event;

    @FXML
    private Label eventNameLabel;
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
     * Refreshes all the text
     */
    public void refreshText(){
        refreshMenu();
        refreshTextEventOverview();
    }
    /**
     * Refreshes the text of EventOverview
     */
    public void refreshTextEventOverview() {
//        inviteCodeButton.setText(
//                Translator.getTranslation(Text.EventOverview.Buttons.SendInvite)
//        );
//        participants.setText(
//                Translator.getTranslation
//                        (Text.EventOverview.Labels.Participants));
//        editParticipant.setText(
//                Translator.getTranslation(Text.EventOverview.Buttons.Edit));
//        addParticipant.setText(
//                Translator.getTranslation(Text.EventOverview.Buttons.Add));
//        settleDebts.setText(
//                Translator.getTranslation
//                        (Text.EventOverview.Buttons.SettleDebts));
//        addExpense.setText(
//                Translator.getTranslation(Text.EventOverview.Buttons.AddExpense)
//        );
//
//        expenses.setText(
//                Translator.getTranslation(Text.EventOverview.Labels.Expenses));

        eventNameLabel.setText(event.getEventName());
    }

    /**
     * Refreshes text of everything in the menu
     */
    public void refreshMenu(){
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
