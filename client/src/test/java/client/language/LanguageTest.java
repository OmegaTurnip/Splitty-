package client.language;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

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
    void testLanguageCode() {
        assertTrue(Language.isValidLanguageCode("eng"));

        assertFalse(Language.isValidLanguageCode("en"));
        assertFalse(Language.isValidLanguageCode("ENG"));
        assertFalse(Language.isValidLanguageCode("..."));
    }

    @Test
    void testLanguageLocale() {
        Translator.setCurrentLanguage(Language.languages.get("deu"));
        assertEquals("de_DE", Translator.getCurrentLanguage().getLocale().toString());
    }

    @Test
    void testNativeName() {
        assertEquals("Nederlands", Language.languages.get("nld").getNativeName());
    }

    @Test
    void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Language(null, new Properties(), null));
    }

    @Test
    void testGetTranslation() {
        assertThrows(IllegalArgumentException.class, () -> Language.languages.get("eng").getTranslation(null));
    }

    @Test
    void testEnglish() {
        Translator.setCurrentLanguage(Language.languages.get("eng"));
        assertEquals("English",
                Translator.getTranslation(Text.NativeLanguageName)
        );
        assertEquals("Languages",
                Translator.getTranslation(Text.Menu.Languages)
        );
        assertEquals("<TEXT DOESN'T EXIST>",
                Translator.getTranslation("random-non-existing-text-id")
        );
        assertEquals("eng", Language.languages.get("eng").getLanguageCode());
    }

    @Test
    void testDutch() {
        Translator.setCurrentLanguage(Language.languages.get("nld"));
        assertEquals(Translator.getCurrentLanguage(), Language.languages.get("nld"));
        assertEquals("Nederlands",
                Translator.getTranslation(Text.NativeLanguageName)
        );
        assertEquals("Talen",
                Translator.getTranslation(Text.Menu.Languages)
        );
        // The square brackets are a relic of checking if maybe an english
        // translation exists, but I'm not changing that as it's something only
        // a dev should see and the only easy way for me to check if it actually
        // adds square brackets when falling back on English.
        assertEquals("[<TEXT DOESN'T EXIST>]",
                Translator.getTranslation("random-non-existing-text-id")
        );
    }

    @Test
    void testEquals() {
        assertEquals(Language.languages.get("eng"), Language.languages.get("eng"));
        assertNotEquals(Language.languages.get("eng"), null);
        assertNotEquals(Language.languages.get("eng"), Language.languages.get("nld"));
    }

    @Test
    void testHashCode() {
        assertEquals(Language.languages.get("eng").hashCode(), Language.languages.get("eng").hashCode());
    }
}
