package client.scenes;



import client.language.Language;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import client.language.TextPage;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import commons.Transaction;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;


public class EventOverviewCtrl implements TextPage, Initializable {

    private Event event;

    @FXML
    private Label eventNameLabel;
    @FXML
    private Label participantsLabel;
    @FXML
    private Label expensesLabel;
    @FXML
    private Menu languages;
    @FXML
    private Button addParticipantButton;
    @FXML
    private Button addExpenseButton;
    @FXML
    private ChoiceBox<String> expensesDropDown;
    @FXML
    private Button settleDebtsButton;
    @FXML
    private Button sendInviteButton;
    @FXML
    private ListView<Participant> participantsListView;
    @FXML
    private ListView<Transaction> expensesListView;
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
     * Initialise the page.
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
        participantsListView.setCellFactory(param -> new ParticipantCellFactory());
        refresh();
    }

    /**
     * Refreshes the page.
     */
    public void refresh() {
        if (event != null && event.getParticipants().isEmpty()) {
            event.addParticipant("test");
            ObservableList<Participant> observableParticipants =
                    FXCollections.observableArrayList(event.getParticipants());
            participantsListView.setItems(observableParticipants);
        }

        refreshText();
    }
    /**
     * Refreshes the text of EventOverview
     */
    public void refreshText() {
        participantsLabel.setText(Translator
                .getTranslation(client.language
                        .Text.EventOverview.participantsLabel));
        expensesLabel.setText(Translator
                .getTranslation(client.language
                        .Text.EventOverview.expensesLabel));
        settleDebtsButton.setText(Translator
                .getTranslation(client.language
                        .Text.EventOverview.Buttons.settleDebtsButton));
        sendInviteButton.setText(Translator
                .getTranslation(client.language
                        .Text.EventOverview.Buttons.sendInviteButton));

        if (event != null ) eventNameLabel.setText(event.getEventName());
    }
    /**
     * Add participant to event
     */
    public void addParticipant(){
        mainCtrl.showAddParticipant(event);
    }

    /**
     * Add expense to the event
     */
    public void addExpense() {

    }

    /**
     * Fetch the languages and add to languages drop down menu.
     */
    private void fetchLanguages() {
        HashMap<String, Language> languages = Language.languages;

        for (String langKey : languages.keySet()) {
            MenuItem item = new MenuItem(langKey);

            item.setOnAction(event -> {
                setLanguage(langKey);
            });

            Image image = new Image(languages
                    .get(langKey).getIconFile().toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            item.setGraphic(imageView);
            this.languages.getItems().add(item);
        }
    }

    /**
     * Set user language.
     * @param langKey The language to set.
     */
    private void setLanguage(String langKey) {
        try {
            UserConfig.get().setUserLanguage(langKey);
            refreshText();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class ParticipantCellFactory extends ListCell<Participant> {

        private FXMLLoader loader;
        @Override
        protected void updateItem(Participant item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setGraphic(null);
                setText(null);
            } else {
                if (loader == null) {
                    loader = new FXMLLoader(getClass().getResource("/client/scenes/ParticipantCell.fxml"));
                    try {
                        loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ParticipantCellController controller = loader.getController();
                controller.setParticipantCellLabelText(item.getName());
                setText(null);
                setGraphic(loader.getRoot());
            }

        }
    }



    /**
     * Setter.
     * @param event Event to be set.
     */
    public void setEvent(Event event) {
        this.event = event;
    }


}
