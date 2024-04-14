package client.scenes;

import client.history.ActionHistory;
import client.language.Text;
import client.language.Translator;
import client.utils.ServerUtils;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

public class ParticipantCellController {

    private Event event;
    private MainCtrl mainCtrl;

    private Participant participant;

    private ServerUtils server;
    private EventOverviewCtrl eventOverviewCtrl;
    @FXML
    private StackPane root;

    @FXML
    private Label participantCellLabel;

    @FXML
    private Button editParticipantButton;

    @FXML
    private Button deleteParticipantButton;

    private AlertWrapper alertWrapper;

    private ActionHistory actionHistory;

    /**
     * Setter.
     * @param actionHistory The action history to set.
     */
    public void setActionHistory(ActionHistory actionHistory) {
        this.actionHistory = actionHistory;
    }

    /**
     * Setter.
     * @param text Participant label text to be set.
     */
    public void setParticipantCellLabelText(String text) {
        participantCellLabel.setText(text);
    }

    /**
     * Initialize the controller.
     */
    @FXML
    public void initialize() {
        editParticipantButton.setOnAction(event -> {
            editParticipant(participant);
        });
        deleteParticipantButton.setOnAction(event -> {
            deleteParticipant(participant);
        });
        root.setFocusTraversable(false);
        this.alertWrapper = new AlertWrapper();
    }

    /**
     * Sets the alertWrapper
     * @param alertWrapper alertWrapper
     */
    public void setAlertWrapper(AlertWrapper alertWrapper) {
        this.alertWrapper = alertWrapper;
    }

    /**
     * Edit the participant.
     * @param participant The participant to edit.
     */
    private void editParticipant(Participant participant) {
        mainCtrl.showEditParticipant(event, participant, actionHistory);
    }

    /**
     * Delete the participant.
     * @param participant The participant to delete.
     */
    public void deleteParticipant(Participant participant) {
        if (participant != null) {
            ButtonType result = showDeletionAlert();
            if (result == ButtonType.OK) {
                server.send("/topic/actionHistory", event);
                //For telling users that actionHistory was updated
                //by participant editing/deleting, so it has to be cleared
                //for undo/redo on expenses to work.
                server.removeParticipant(participant, event);
                event.removeParticipant(participant);
            }
        }
    }

    /**
     * For some reason the test won't work without this
     * @return Outcome of buttonpress
     */
    public ButtonType showDeletionAlert(){
        return alertWrapper.showAlertButton(
                Alert.AlertType.CONFIRMATION,
                Translator.getTranslation(Text
                        .EventOverview
                        .ParticipantCellController
                        .Alert.deleteParticipantTitle),
                Translator.getTranslation(Text
                        .EventOverview
                        .ParticipantCellController
                        .Alert.deleteParticipantContent)
        );
    }

    /**
     * Setter.
     * @param event The event to set.
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Setter.
     * @param participant The participant to set.
     */
    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    /**
     * Setter.
     * @param server The server to set.
     */
    public void setServer(ServerUtils server) {
        this.server = server;
    }

    /**
     * Setter.
     * @param eventOverviewCtrl The eventOverviewCtrl to set.
     */
    public void setEventOverviewCtrl(EventOverviewCtrl eventOverviewCtrl) {
        this.eventOverviewCtrl = eventOverviewCtrl;
    }

    /**
     * Setter.
     * @param mainCtrl The mainCtrl to set.
     */
    public void setMainController(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }
}
