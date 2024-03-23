package client.scenes;

import client.language.Language;
import client.language.Text;
import client.language.TextPage;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import commons.Tag;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AddExpenseCtrl implements Initializable, TextPage {

    @FXML
    private Menu languages;
    @FXML
    private Menu rto;
    @FXML
    private Menu close;
    @FXML
    private Button cancel;
    @FXML
    private ChoiceBox<Currency> currency;
    @FXML
    private ChoiceBox<Participant> payer;
    @FXML
    private ComboBox<Participant> participants;
    private Collection<Participant> participantList;
    @FXML
    private DatePicker date;
    @FXML
    private Button addExpense;
    @FXML
    private TextField expenseName;
    @FXML
    private TextField price;
    @FXML
    private ComboBox<Tag> expenseType;
    private Event event;
    private ArrayList<Tag> tags = new ArrayList<>();
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final EventOverviewCtrl eventOverviewCtrl;

    /**
     * Initializes the controller
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
     * Initialise the expense adding window.
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
        addExpense.setOnAction(event -> registerExpense());
        rto.setOnAction(event -> mainCtrl.showStartUp());
        refresh();
    }

    /**
     * Changes the scene to startUp
     */
    private void backToStartup() {
        //TODO: Set on-action to go back to start-up window
    }

    /**
     * Register the expense added
     */
    private void registerExpense() {
        //TODO: Connect to back-end
    }

    /**
     * Refreshes the page and updates the list view.
     */
    public void refresh() {
        refreshText();
        //TODO: Connect to back-end
        System.out.println("Page has been refreshed!");
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
        //TODO: Make labels for the other text
    }


    private void fetchLanguages() {
        HashMap<String, Language> languages = Language.languages;

        for (String langKey : languages.keySet()) {
            MenuItem item = new MenuItem(langKey);

            item.setOnAction(event -> setLanguage(langKey));

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
