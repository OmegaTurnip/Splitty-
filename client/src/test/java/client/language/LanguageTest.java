package client.language;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class LanguageTest {

    @BeforeAll
    static void setup() throws IOException {
        Language.fromLanguageFile(
                "eng", new File("../includedLanguages/eng.properties")
        );
        Language.fromLanguageFile(
                "nld", new File("../includedLanguages/nld.properties")
        );
        Language.fromLanguageFile(
                "deu", new File("../includedLanguages/deu.properties")
        );
    }


    @Test
    void testEnglish() {
        Translator.setCurrentLanguage(Language.languages.get("eng"));
        assertEquals("English",
                Translator.getTranslation(Text.NativeLanguageName)
        );
        assertEquals("Cancel",
                Translator.getTranslation(Text.MessageBox.Options.Cancel)
        );
        assertEquals("<TEXT DOESN'T EXIST>",
                Translator.getTranslation("random-non-existing-text-id")
        );
    }

    @Test
    void testDutch() {
        Translator.setCurrentLanguage(Language.languages.get("nld"));
        assertEquals("Nederlands",
                Translator.getTranslation(Text.NativeLanguageName)
        );
        assertEquals("Annuleren",
                Translator.getTranslation(Text.MessageBox.Options.Cancel)
        );
        // The square brackets are a relic of checking if maybe an english
        // translation exists, but I'm not changing that as it's something only
        // a dev should see and the only easy way for me to check if it actually
        // adds square brackets when falling back on English.
        assertEquals("[<TEXT DOESN'T EXIST>]",
                Translator.getTranslation("random-non-existing-text-id")
        );
    }
}
