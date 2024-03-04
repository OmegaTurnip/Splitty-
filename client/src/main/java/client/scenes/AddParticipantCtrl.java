package client.scenes;

import client.language.Text;
import client.language.Translator;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AddParticipantCtrl{
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
    private Label username;
    @FXML
    private Button cancel;
    @FXML
    private Button addParticipant;
    @FXML
    private TextField usernameTextField;

    /**
     * Refreshes the text
     */
    public void refreshText() {
        languages.setText(
                Translator.getTranslation(Text.Menu.Languages)
        );
        english.setText(
                Translator.getTranslation(Text.Menu.English)
        );
        dutch.setText(
                Translator.getTranslation(Text.Menu.Dutch)
        );
        german.setText(
                Translator.getTranslation(Text.Menu.German)
        );
        rto.setText(
                Translator.getTranslation(Text.Menu.RTO)
        );
        close.setText(
                Translator.getTranslation(Text.Menu.Close)
        );
        username.setText(
                Translator.getTranslation(Text.AddParticipant.Username)
        );
    }

    /**
     * Method still in construction
     */
    public void addParticipant(){
        String username = usernameTextField.getText();
    }


}
