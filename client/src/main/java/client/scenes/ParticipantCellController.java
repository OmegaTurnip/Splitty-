package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

public class ParticipantCellController {

    private Event event;

    private Participant participant;

    private ServerUtils server;
    private EventOverviewCtrl eventOverviewCtrl;

    @FXML
    private Label participantCellLabel;

    @FXML
    private Button editParticipantButton;

    @FXML
    private Button deleteParticipantButton;


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
            System.out.println("Edit participant button clicked");
        });
        deleteParticipantButton.setOnAction(event -> {
            deleteParticipant(participant);
            System.out.println("Delete participant button clicked");
        });
    }

    /**
     * Delete the participant.
     * @param participant The participant to delete.
     */
    private void deleteParticipant(Participant participant) {
        if (participant != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete participant");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Are you sure you want to delete this participant?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                event.removeParticipant(participant);
                try {
                    server.saveEvent(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                eventOverviewCtrl.refresh();
            }
        }
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



}
