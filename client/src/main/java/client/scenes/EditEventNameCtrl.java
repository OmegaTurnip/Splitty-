package client.scenes;

import client.language.Language;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import commons.Event;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.HashMap;

public class EditEventNameCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
//    @FXML
//    private Menu languages;

    @FXML
    private TextField eventName;

    private Event event;

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
//
//        for (String langKey : languages.keySet()) {
//            MenuItem item = new MenuItem(langKey);
//
//            item.setOnAction(event -> {
//                setLanguage(langKey);
//            });
//
//            Image image = new Image(languages
//                    .get(langKey).getIconFile().toURI().toString());
//            ImageView imageView = new ImageView(image);
//            imageView.setFitHeight(20);
//            imageView.setFitWidth(20);
//            item.setGraphic(imageView);
//            this.languages.getItems().add(item);
//        }
//    }

    private void setLanguage(String langKey) {
        try {
            UserConfig.get().setUserLanguage(langKey);
            refreshText();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshText(){
        if(event!= null){
            eventName.setText(event.getEventName());
        }
    }

    public void cancel(){
        refreshText();
        mainCtrl.showEventOverview(event);
    }

    public void changeName(){
        event.setEventName(eventName.getText());
        server.saveEvent(event);
        mainCtrl.showEventOverview(event);
    }

}
