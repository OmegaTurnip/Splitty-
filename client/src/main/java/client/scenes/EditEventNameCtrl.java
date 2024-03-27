package client.scenes;


import client.utils.ServerUtils;
import client.utils.UserConfig;
import commons.Event;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.io.IOException;


public class EditEventNameCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;


    @FXML
    private TextField eventName;

    private Event event;

    /**
     * Constructer
     * @param server serverUtils file
     * @param mainCtrl mainCtrl file
     */
    @Inject
    public EditEventNameCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Setter.
     * @param event Event to be set.
     */
    public void setEvent(Event event) {
        this.event = event;
    }

//    private void fetchLanguages() {
//        HashMap<String, Language> languages = Language.languages;
//        for (String langKey : languages.keySet()) {
//            MenuItem item = new MenuItem(langKey);
//            item.setOnAction(event -> {
//                setLanguage(langKey);
//            });
//            Image image = new Image(languages
//                    .get(langKey).getIconFile().toURI().toString());
//            ImageView imageView = new ImageView(image);
//            imageView.setFitHeight(20);
//            imageView.setFitWidth(20);
//            item.setGraphic(imageView);
//            this.languages.getItems().add(item);
//        }
//    }

    /**
     * SSetst the language (not in use yet)
     * @param langKey key of the language that needs to be activated
     */
    private void setLanguage(String langKey) {
        try {
            UserConfig.get().setUserLanguage(langKey);
            refreshText();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Refreshes the text
     */
    public void refreshText(){
        if(event!= null){
            eventName.setText(event.getEventName());
        }
    }

    /**
     * Return to the eventoverview
     */
    public void cancel(){
        refreshText();
        mainCtrl.showEventOverview(event);
    }

    /**
     * Changes the name and saves it to the database
     */
    public void changeName(){
        event.setEventName(eventName.getText());
        server.saveEvent(event);
        mainCtrl.showEventOverview(event);
    }

}
