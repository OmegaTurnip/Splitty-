package client.scenes;

import client.language.Text;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;

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
    private Event event;

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
                Translator.getTranslation(Text.Menu.ReturnToOverview)
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
        mainCtrl.showEventOverview(event);
    }

    /**
     * Method still in construction
     */
    public void addParticipant(){
        if(createParticipant()){
            this.mainCtrl.showEventOverview(event);
        }
    }

    private Participant getParticipant(){
        return null;
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


    /**
     * Setter.
     * @param event The event to be set.
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    public boolean createParticipant() throws WebApplicationException{
        try{
            emptyCheck();
            event.addParticipant(usernameTextField.getText());
        } catch(WebApplicationException e){
            e.printStackTrace();
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return false;
        }
        return true;
    }

    public void emptyCheck(){
        if (usernameTextField.getText().isEmpty()) {
            throw new WebApplicationException(
                    Translator.getTranslation(
                            Text.AddParticipant.Alert.NoName
                                    ), 422);
        }
        if (emailTextField.getText().isEmpty()) {
            throw new WebApplicationException(
                    Translator.getTranslation(
                            Text.AddParticipant.Alert.NoMail
                    ), 422);
        }
        if (ibanTextField.getText().isEmpty()) {
            throw new WebApplicationException(
                    Translator.getTranslation(
                            Text.AddParticipant.Alert.NoIBAN
                    ), 422);
        }
        if (bicTextField.getText().isEmpty()) {
            throw new WebApplicationException(
                    Translator.getTranslation(
                            Text.AddParticipant.Alert.NoBIC
                    ), 422);
        }
    }
}
