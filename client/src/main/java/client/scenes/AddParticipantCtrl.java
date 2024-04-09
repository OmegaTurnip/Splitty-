package client.scenes;

import client.language.Formatter;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AddParticipantCtrl extends TextPage implements Initializable {

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

    private ServerUtils server;
    private MainCtrl mainCtrl;
    private Event event;

    private Participant participantToOverwrite;
    private AlertWrapper alertWrapper;

    /**
     * Initializes the controller
     * @param server .
     * @param mainCtrl .
     */
    @Inject
    public AddParticipantCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.alertWrapper = new AlertWrapper();
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
        fetchLanguages();
    }

    /**
     * Getter.
     * @return The email text field.
     */
    public TextField getEmailTextField() {
        return emailTextField;
    }

    /**
     * Getter.
     * @return The iban text field.
     */
    public TextField getIbanTextField() {
        return ibanTextField;
    }

    /**
     * Getter.
     * @return The bic text field.
     */
    public TextField getBicTextField() {
        return bicTextField;
    }

    /**
     * Getter.
     * @return The languages-menu.
     */
    public Menu getLanguages() {
        return languageMenu;
    }

    /**
     * Refresh the page
     */
    public void refresh() {
        if (participantToOverwrite != null) {
            usernameTextField.setText(participantToOverwrite.getName());
            emailTextField.setText(participantToOverwrite.getEmail());
            ibanTextField.setText(participantToOverwrite.getIban());
            bicTextField.setText(participantToOverwrite.getBic());
        }
        refreshText();
    }

    /**
     * Sets alertWrapper
     * @param alertWrapper alertWrapper to be set
     */
    public void setAlertWrapper(AlertWrapper alertWrapper) {
        this.alertWrapper = alertWrapper;
    }

    /**
     * Refreshes the text
     */
    @Override
    public void refreshText() {
        languageMenu.setText(
                Translator.getTranslation(Text.Menu.Languages));
        username.setText(
                Translator.getTranslation(Text.AddParticipant.Username)
        );
        addParticipant.setText(
                Translator.getTranslation(Text.AddParticipant.Add)
        );
        if (participantToOverwrite != null) {
            title.setText(
                    Translator.getTranslation(Text.AddParticipant.EditTitle)
            );
        } else {
            title.setText(
                    Translator.getTranslation(Text.AddParticipant.Title)
            );
        }
        cancel.setText(
                Translator.getTranslation(Text.AddParticipant.Cancel)
        );

    }

    /**
     * Getter.
     * @return The cancel button.
     */
    public Button getCancel() {
        return cancel;
    }

    /**
     * Cancels the action in the addParticipant window
     */
    public void cancel(){
        clearFields();
        mainCtrl.showEventOverview(event);
    }

    /**
     * Method still in construction
     */
    public void addParticipant(){
        if(saveParticipant()){
            this.mainCtrl.showEventOverview(event);
        }
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

    public boolean saveParticipant() throws WebApplicationException {
        try{
            if (!(emptyCheck() && formatCheck() && uniqueCheck())){
                return false;
            }
            if (participantToOverwrite != null) {
                overwriteParticipant();
                clearFields();
            } else {
                createParticipant();
                clearFields();
            }
        } catch(WebApplicationException e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void createParticipant() {
        Participant participant =
                event.addParticipant(usernameTextField.getText(),
                        emailTextField.getText(),
                        ibanTextField.getText(),
                        bicTextField.getText());
        Participant returnedP = server.saveParticipant(participant);
        participant.setParticipantId(returnedP.getParticipantId());
        System.out.println("Created " + participant);
        event.removeParticipant(participant);
        event.addParticipant(returnedP);
    }

    private void overwriteParticipant() {
        participantToOverwrite.setName(usernameTextField.getText());
        participantToOverwrite.setEmail(emailTextField.getText());
        participantToOverwrite.setIban(ibanTextField.getText());
        participantToOverwrite.setBic(bicTextField.getText());
//        Participant returnedP = server
//                .saveParticipant(participantToOverwrite);
//        event.removeParticipant(participantToOverwrite);
//        event.addParticipant(returnedP);
        event.removeParticipant(participantToOverwrite);
        event.addParticipant(participantToOverwrite);
        server.saveEvent(event);
    }

    /**
     * Checks whether all fields are non-empty
     * @return boolean whether check failed or succeeded
     */
    public boolean emptyCheck(){
        if (usernameTextField.getText().isEmpty()) {
            if (sendEmptyCheckError() == ButtonType.OK){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the entered participants name is unique compared
     * to all other participants
     * @return boolean whether check fails or passes
     */
    public boolean uniqueCheck(){
        List<Participant> participants = event.getParticipants();
        ArrayList<String> participantNames = new ArrayList<>();
        for (Participant participant: participants){
            participantNames.add(participant.getName());
        }
        if (participantNames.contains(usernameTextField.getText())){
            if (sendDuplicateNameError() == ButtonType.OK){
                return false;
            }
        }
        return true;
    }

    /**
     * Formats the alert of a duplication error
     * @return returns resulting buttonPress of alert
     */
    public ButtonType sendDuplicateNameError(){
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("name", usernameTextField.getText());
        return alertWrapper.showAlertButton(Alert.AlertType.ERROR,
                Translator.getTranslation(
                        Text.AddParticipant.Alert.DuplicateError),
                Formatter.format(Translator.getTranslation(
                        Text.AddParticipant.Alert.DuplicateErrorContent
                ), parameters));
    }

    /**
     * Formats the alert of an empty error
     * @return returns resulting buttonPress of alert
     */
    public ButtonType sendEmptyCheckError(){
        return alertWrapper.showAlertButton(Alert.AlertType.ERROR,
                Translator.getTranslation(
                        Text.AddParticipant.Alert.EmptyError),
                Translator.getTranslation(
                        Text.AddParticipant.Alert.NoName
                ));
    }

    /**
     * Clears all input fields
     */
    public void clearFields(){
        usernameTextField.setText("");
        emailTextField.setText("");
        ibanTextField.setText("");
        bicTextField.setText("");
    }

    /**
     * Checks whether the format of the fields is correct
     * @return boolean whether check fails or not
     */
    public boolean formatCheck(){
        if (!isValidEmail(emailTextField.getText())) {
            alertWrapper.showAlertButton(Alert.AlertType.ERROR,
                    Translator.getTranslation(
                            Text.AddParticipant.Alert.FormatError),
                    Translator.getTranslation(
                    Text.AddParticipant.Alert.InvalidMail
                    ));
            return false;
        }
        if (!isValidIban(ibanTextField.getText())) {
            alertWrapper.showAlertButton(Alert.AlertType.ERROR,
                    Translator.getTranslation(
                            Text.AddParticipant.Alert.FormatError),
                    Translator.getTranslation(
                            Text.AddParticipant.Alert.InvalidIBAN
                    ));
            return false;

        }
        if (!isValidBic(bicTextField.getText())) {
            alertWrapper.showAlertButton(Alert.AlertType.ERROR,
                    Translator.getTranslation(
                            Text.AddParticipant.Alert.FormatError),
                    Translator.getTranslation(
                            Text.AddParticipant.Alert.InvalidBIC
                    ));
            return false;
        }
        return true;
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@.+$"
    );
    private static final Pattern IBAN_PATTERN = Pattern.compile(
            "[A-Z0-9\\s]+"
    );
    private static final Pattern BIC_PATTERN = Pattern.compile(
            "^[A-Z0-9]{4}[A-Z]{2}[A-Z0-9]{2}(?>[A-Z0-9]{3})?$"
    );
    /**
     * Checks whether the supplied email is valid.
     * @param   email
     *          The email to be checked.
     * @return  Whether the supplied email is valid.
     */
    static boolean isValidEmail(String email) {
        return email.isEmpty() || EMAIL_PATTERN.matcher(email).matches();
    }
    /**
     * Checks whether the supplied IBAN is valid.
     * @param   iban
     *          The IBAN to be checked.
     * @return  Whether the supplied IBAN is valid.
     */
    static boolean isValidIban(String iban) {
        return iban.isEmpty() || IBAN_PATTERN.matcher(iban).matches();
    }
    /**
     * Checks whether the supplied BIC is valid.
     * @param   bic
     *          The BIC to be checked.
     * @return  Whether the supplied BIC is valid.
     */
    static boolean isValidBic(String bic) {
        return bic.isEmpty() || BIC_PATTERN.matcher(bic).matches();
    }
    /**
     * Setter.
     * @param participant The participant to be set.
     */
    public void setParticipant(Participant participant) {
        this.participantToOverwrite = participant;
    }

    /**
     * Getter.
     * @return The username text field.
     */
    public TextField getUsernameTextField() {
        return usernameTextField;
    }

    /**
     * Getter.
     * @return The title label.
     */
    public Label getTitle() {
        return title;
    }

    /**
     * Getter.
     * @return The username label.
     */
    public Label getUsername() {
        return username;
    }

    /**
     * Getter.
     * @return The add participant label.
     */
    public Button getAdd() {
        return addParticipant;
    }

    /**
     * Getter.
     * @return The server utils.
     */
    public ServerUtils getServer() {
        return server;
    }
    /**
     * Getter.
     * @return The main controller.
     */
    public MainCtrl getMainCtrl() {
        return mainCtrl;
    }

    /**
     * Setter.
     * @param server The server utils to be set.
     */
    public void setServer(ServerUtils server) {
        this.server = server;
    }

    /**
     * Setter.
     * @param mainCtrl The main controller to be set.
     */
    public void setMainCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }
}
