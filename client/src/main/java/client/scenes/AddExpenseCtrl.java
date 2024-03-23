package client.scenes;

import client.language.Language;
import client.language.Text;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.HashMap;

public class AddExpenseCtrl {

    @FXML
    private Menu languages;
    @FXML
    private Menu rto;
    @FXML
    private Menu close;
    @FXML
    private Button cancel;
    @FXML
    private ChoiceBox currency;
    @FXML
    private ChoiceBox<Participant> payer;
    @FXML
    private DatePicker date;
    @FXML
    private Button addExpense;
    @FXML
    private TextField expenseName;
    @FXML
    private TextField price;
    private Event event;


    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final EventOverviewCtrl eventOverviewCtrl;

    /**
     * Initalizes the controller
     *
     * @param server            .
     * @param mainCtrl          .
     * @param eventOverviewCtrl .
     */
    @Inject
    public AddExpenseCtrl(ServerUtils server, MainCtrl mainCtrl,
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
        rto.setText(
                Translator.getTranslation(Text.Menu.ReturnToOverview)
        );
        close.setText(
                Translator.getTranslation(Text.Menu.Close)
        );
        cancel.setText(
                Translator.getTranslation(Text.AddParticipant.Cancel)
        );

    }


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
     * Cancels the action in the addParticipant window
     */
    public void cancel() {
        refreshText();
        mainCtrl.showEventOverview(event);
    }

    /**
     * Sets language to German
     * @param language the language in three character String
     */
    public void setLanguage(String language) {
        try {
            UserConfig.get().setUserLanguage(language);
            refreshText();
        } catch (IOException e) {
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

}
