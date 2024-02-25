package client.scenes;

import client.language.Text;
import client.language.Translator;
import client.utils.UserConfig;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LanguageTestCtrl implements Initializable {

    private final String[] langs =
            UserConfig.USER_SETTINGS.getAvailableLanguages()
                    .keySet().toArray(new String[0]);

    private int langIdx = 0;

    @FXML
    private Label currentLanguage;
    @FXML
    private Label retry;

    /**
     * temp
     */
    @Inject
    public LanguageTestCtrl() {

    }

    /**
     * Temp test thingy
     * @param location Temp test thingy
     * @param resources Temp test thingy
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Cycle through the languages
     */
    public void nextLang() {
        langIdx %= langs.length;
        try {
            UserConfig.USER_SETTINGS.setUserLanguage(langs[langIdx]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        langIdx++;
        reloadLang();
    }

    /**
     * Updates the texts to the current language.
     */
    public void reloadLang() {
        currentLanguage.setText(
                Translator.getTranslation(Text.NativeLanguageName)
        );
        retry.setText(
                Translator.getTranslation(Text.MessageBox.Options.Retry)
        );

    }


}
