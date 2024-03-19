package client.scenes;

import client.language.Text;
import client.language.TextPage;
import client.language.Translator;
import client.utils.UserConfig;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LanguageTestCtrl implements Initializable, TextPage {

    private final String[] langs =
            UserConfig.get().getAvailableLanguages()
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
            UserConfig.get().setUserLanguage(langs[langIdx]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        langIdx++;
        refreshText();
    }

    /**
     * Refreshes the text in the current language on the page, should also be
     * used on initial start-up.
     */
    @Override
    public void refreshText() {
        currentLanguage.setText(
                Translator.getTranslation(Text.NativeLanguageName)
        );
        retry.setText(
                Translator.getTranslation(Text.MessageBox.Options.Retry)
        );

    }


}
