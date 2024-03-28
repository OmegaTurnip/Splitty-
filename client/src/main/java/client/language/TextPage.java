package client.language;

import client.utils.UserConfig;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.HashMap;

public interface TextPage {

    /**
     * Refreshes the text in the current language on the page, should also be
     * used on initial start-up.
     */
    void refreshText();

    /**
     * Set the language of the user.
     * @param langKey the language key
     * @param languagesMenu the menu to set the language icon
     * @param languages the languages
     */
    default void setLanguage(String langKey,
                             Menu languagesMenu,
                             HashMap<String, Language> languages) {
        try {
            UserConfig.get().setUserLanguage(langKey);
            refreshText();
            Image image = new Image(languages
                    .get(langKey).getIconFile().toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            languagesMenu.setGraphic(imageView);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetches the languages from the app languages and sets the menu items.
     * @param languagesMenu the menu to set the languages
     */
    default void fetchLanguages(Menu languagesMenu) {
        HashMap<String, Language> languages = Language.languages;

        for (String langKey : languages.keySet()) {
            MenuItem item = new MenuItem(languages
                    .get(langKey).getNativeName());

            item.setOnAction(event -> {
                setLanguage(langKey, languagesMenu, languages);
            });

            Image image = new Image(languages
                    .get(langKey).getIconFile().toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            item.setGraphic(imageView);
            languagesMenu.getItems().add(item);
        }
    }
}
