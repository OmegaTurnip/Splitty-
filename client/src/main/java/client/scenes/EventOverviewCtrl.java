package client.scenes;



import client.language.Text;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;

import java.io.IOException;


public class EventOverviewCtrl {

    private Event event;

    @FXML
    private Label eventTitle;

    @FXML
    private Button editParticipant;
    @FXML
    private Button addParticipant;
    @FXML
    private Button settleDebts;
    @FXML
    private Button addExpense;
    @FXML
    private Button inviteCodeButton;
    @FXML
    private Label participants;
    @FXML
    private Menu languages;
    @FXML
    private CheckMenuItem english;
    @FXML
    private CheckMenuItem dutch;
    @FXML
    private CheckMenuItem german;
    @FXML
    private Menu rto;
    @FXML
    private Menu close;
    @FXML
    private Label participantsList;
    @FXML
    private Label expenses;
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
        inviteCodeButton.setText(
                Translator.getTranslation(Text.EventOverview.Buttons.SendInvite)
        );
        participants.setText(
                Translator.getTranslation
                        (Text.EventOverview.Labels.Participants));
        editParticipant.setText(
                Translator.getTranslation(Text.EventOverview.Buttons.Edit));
        addParticipant.setText(
                Translator.getTranslation(Text.EventOverview.Buttons.Add));
        settleDebts.setText(
                Translator.getTranslation
                        (Text.EventOverview.Buttons.SettleDebts));
        addExpense.setText(
                Translator.getTranslation(Text.EventOverview.Buttons.AddExpense)
        );

        expenses.setText(
                Translator.getTranslation(Text.EventOverview.Labels.Expenses));

        eventTitle.setText(event.getEventName());

        StringBuilder participantsString = new StringBuilder();
        for (Participant participant : event.getParticipants()) {
            participantsString.append(participant.getName()).append("\n");
        }

        participantsList.setText(participantsString.toString());
    }

    /**
     * Refreshes text of everything in the menu
     */
    public void refreshMenu(){
        languages.setText(
                Translator.getTranslation(Text.Menu.Languages));
        english.setText(
                Translator.getTranslation(Text.Menu.English));
        dutch.setText(
                Translator.getTranslation(Text.Menu.Dutch));
        german.setText(
                Translator.getTranslation(Text.Menu.German));
        rto.setText(
                Translator.getTranslation(Text.Menu.ReturnToOverview));
        close.setText(
                Translator.getTranslation(Text.Menu.Close));
    }
    /**
     * Still in construction
     */
    public void addParticipant(){
        mainCtrl.showAddParticipant(event);
        refreshText();
    }

    /**
     * Still in construction (planning to add name to list of participants)
     * @param username name to be added to list
     */
    public void displayName(String username){
        this.participantsList.setText(username);
    }

    /**
     * Sets language to Dutch
     */
    public void setDutch(){
        try {
            UserConfig.get().setUserLanguage("nld");
            refreshText();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets language to English
     */
    public void setEnglish(){
        try {
            UserConfig.get().setUserLanguage("eng");
            refreshText();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets language to German
     */
    public void setGerman(){
        try {
            UserConfig.get().setUserLanguage("deu");
            refreshText();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void setEvent(Event event) {
        this.event = event;
    }
}
