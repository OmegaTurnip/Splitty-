package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ParticipantCellController {

    @FXML
    private Label participantCellLabel;

    @FXML
    private Button editParticipantButton;

    @FXML
    private Button deleteParticipantButton;

    public void setParticipantCellLabelText(String text) {
        participantCellLabel.setText(text);
    }



}
