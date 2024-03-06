package client.scenes;

import client.language.Text;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Inject;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

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
    private Label title;
    @FXML
    private Button cancel;
    @FXML
    private Button addParticipant;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField ibanTextField;
    @FXML
    private TextField bicTextField;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final EventOverviewCtrl eventOverviewCtrl;

    /**
     * Initalizes the controller
     * @param server .
     * @param mainCtrl .
     * @param eventOverviewCtrl .
     */
    @Inject
    public AddParticipantCtrl(ServerUtils server, MainCtrl mainCtrl,
                              EventOverviewCtrl eventOverviewCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.eventOverviewCtrl = eventOverviewCtrl;
    }

    /**
     * Refreshes the text
     */
    public void refreshText() {
        languages.setText(
                Translator.getTranslation(Text.Menu.Languages));
        english.setText(
                Translator.getTranslation(Text.Menu.English));
        dutch.setText(
                Translator.getTranslation(Text.Menu.Dutch));
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
        addParticipant.setText(
                Translator.getTranslation(Text.AddParticipant.Add)
        );
        title.setText(
                Translator.getTranslation(Text.AddParticipant.Title)
        );
        cancel.setText(
                Translator.getTranslation(Text.AddParticipant.Cancel)
        );

    }

    /**
     * Cancels the action in the addParticipant window
     */
    public void cancel(){
        refreshText();
        mainCtrl.showOverview();
    }

    /**
     * Method still in construction
     */
    public void addParticipant(){
        String username = usernameTextField.getText();
        this.eventOverviewCtrl.displayName(username);
        this.mainCtrl.showOverview();
    }

    private Participant getParticipant(){
        return null;
    }

    /**
     * Sets language to Dutch
     */
    public void setDutch(){
        try {
            UserConfig.USER_SETTINGS.setUserLanguage("nld");
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
            UserConfig.USER_SETTINGS.setUserLanguage("eng");
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
            UserConfig.USER_SETTINGS.setUserLanguage("deu");
            refreshText();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
