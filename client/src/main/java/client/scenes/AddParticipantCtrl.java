package client.scenes;

import client.language.Text;
import client.language.TextPage;
import client.language.Translator;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Modality;

import java.net.URL;
import java.util.ResourceBundle;

public class AddParticipantCtrl implements Initializable, TextPage {


    @FXML
    private Menu languages;
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
     * Initializes the controller
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
        fetchLanguages(languages);
    }

    /**
     * Refresh the page
     */
    public void refresh() {
        refreshText();
    }

    /**
     * Refreshes the text
     */
    public void refreshText() {
        languages.setText(
                Translator.getTranslation(Text.Menu.Languages));
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
     * Setter.
     * @param event The event to be set.
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Method that creates the participant in the database
     * @return Boolean that states if the creation was successful such
     * that the window can be closed
     * @throws WebApplicationException Alert when something goes wrong
     */

    public boolean createParticipant() throws WebApplicationException{
        try{
            emptyCheck();
            formatCheck();
            event.addParticipant(usernameTextField.getText());
            server.saveEvent(event);
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

    /**
     * Checks whether all fields are non-empty
     */
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

    /**
     * Checks whether the format of the fields is correct
     */
    public void formatCheck(){
        if (!isValidEmail(emailTextField.getText())) {
            throw new WebApplicationException(
                    Translator.getTranslation(
                            Text.AddParticipant.Alert.InvalidMail
                    ), 422);
        }
        if (!isValidIban(ibanTextField.getText())) {
            throw new WebApplicationException(
                    Translator.getTranslation(
                            Text.AddParticipant.Alert.InvalidIBAN
                    ), 422);
        }
        if (!isValidBic(bicTextField.getText())) {
            throw new WebApplicationException(
                    Translator.getTranslation(
                            Text.AddParticipant.Alert.InvalidBIC
                    ), 422);
        }
    }

    /**
     * Checks whether the email is valid
     * @param email The email to be checked
     * @return Boolean that states whether the email is valid
     */
    public boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }

    /**
     * Checks whether the IBAN is valid
     * @param iban The IBAN to be checked
     * @return Boolean that states whether the IBAN is valid
     */
    public boolean isValidIban(String iban) {
        String regex = "^([A-Z]{2})([0-9]{2})([A-Z0-9]{4})" +
                "([0-9]{7})([A-Z0-9]{1,16})$";
        return iban.matches(regex);
    }

    /**
     * Checks whether the BIC is valid
     * @param bic The BIC to be checked
     * @return Boolean that states whether the BIC is valid
     */
    public boolean isValidBic(String bic) {
        String regex = "^[A-Za-z]{4}[A-Za-z]{2}\\w{2}(\\w{3})?$";
        return bic.matches(regex);
    }


}
