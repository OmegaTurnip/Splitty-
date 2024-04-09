package client.language;

import client.utils.UserConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.HashMap;

public abstract class TextPage {

    @FXML
    protected Menu languageMenu;

    /**
     * Refreshes the text in the current language on the page, should also be
     * used on initial start-up.
     */
    public abstract void refreshText();

    /**
     * Set the language of the user.
     * @param langKey the language key
     * @param languagesMenu the menu to set the language icon
     * @param languages the languages
     */
    private void setLanguage(String langKey,
                             Menu languagesMenu,
                             HashMap<String, Language> languages) {
        try {
            UserConfig.get().setUserLanguage(langKey);
            refreshText();
            refreshIcon(langKey, languagesMenu, languages);
            languageMenu.setText(
                    Translator.getTranslation(Text.Menu.Languages));
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Refreshes the icon of the language menu.
     * @param langKey the language key
     * @param languagesMenu the menu to set the language icon on
     * @param languages the available languages
     */
    public void refreshIcon(String langKey, Menu languagesMenu, HashMap<String,
            Language> languages) {
        Image image = new Image(languages
                .get(langKey).getIconFile().toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        languagesMenu.setGraphic(imageView);
    }

    /**
     * Fetches the languages from the app languages and sets the menu items.
     */
    public void fetchLanguages() {
        HashMap<String, Language> languages = Language.languages;

        for (String langKey : languages.keySet()) {
            MenuItem item = new MenuItem(languages
                    .get(langKey).getNativeName());

            item.setOnAction(event -> {
                setLanguage(langKey, languageMenu, languages);
            });

            Image image = new Image(languages
                    .get(langKey).getIconFile().toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            item.setGraphic(imageView);
            languageMenu.getItems().add(item);
        }
        String langKey = UserConfig.get().getUserLanguage();
        refreshIcon(langKey, languageMenu, languages);
    }
}
