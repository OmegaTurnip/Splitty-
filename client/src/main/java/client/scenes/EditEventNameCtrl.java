package client.scenes;


import client.language.Text;
import client.language.TextPage;
import client.language.Translator;
import client.utils.ServerUtils;

import commons.Event;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;


public class EditEventNameCtrl extends TextPage implements Initializable {

    private final ServerUtils server;
    private MainCtrl mainCtrl;
    private final EventOverviewCtrl eventOverviewCtrl;

    private AlertWrapper alertWrapper;



    @FXML
    private TextField eventName;
    @FXML
    private Label eventInput;
    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;

    private Event event;

    /**
     * Constructer
     * @param server serverUtils file
     * @param mainCtrl mainCtrl file
     * @param eventOverviewCtrl eventOverviewCtrl file
     */
    @Inject
    public EditEventNameCtrl(ServerUtils server, MainCtrl mainCtrl,
                             EventOverviewCtrl eventOverviewCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.eventOverviewCtrl = eventOverviewCtrl;
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
     * Setter.
     * @param event Event to be set.
     */
    public void setEvent(Event event) {
        this.event = event;
    }


    /**
     * sets an alertWrapper
     * @param alertWrapper alertWrapper
     */
    public void setAlertWrapper(AlertWrapper alertWrapper) {
        this.alertWrapper = alertWrapper;
    }


    /**
     * Refreshes the text
     */
    public void refreshText(){
        if(event!= null){
            eventName.setText(event.getEventName());
        }
        eventInput.setText(
                Translator.getTranslation(Text.EditName.inputName)
        );
        cancelButton.setText(
                Translator.getTranslation(Text.MessageBox.Options.Cancel)
        );
        confirmButton.setText(
                Translator.getTranslation(Text.EditName.confirm)
        );
    }

    /**
     * Return to the eventoverview
     */
    public void cancel(){
        eventOverviewCtrl.refreshText();
        mainCtrl.showEventOverview(event);
    }



    /**
     * Changes the name and saves it to the database
     */
    public void changeName(){
        if (!event.getEventName().equals(eventName.getText())){
            ButtonType result = alertWrapper.showAlertButton(
                            Alert.AlertType.CONFIRMATION,
                    "Name change confirmation",
                    "Are you sure you want to change the name of "+
                    "the event '" + event.getEventName()+ "' to the following: "
                    + eventName.getText() + ".");
            if (result == ButtonType.OK){
                event.setEventName(eventName.getText());
                server.saveEvent(event);
                mainCtrl.showEventOverview(event);
            }
        }
        mainCtrl.showEventOverview(event);
    }

    /**
     * Setter for mainCtrl
     * @param mainCtrl the MainCtrl to set
     */
    public void setMainCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }
}
