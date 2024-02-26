package client.language;

import client.utils.PropertiesFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

public class Language {

    public static final HashMap<String, Language> languages = new HashMap<>();
    private static Language english;
    private final String languageCode;
    private final Properties language;

    private static final Pattern LANGUAGE_CODE_PATTERN =
            Pattern.compile("^[a-z]{3}$");

    /**
     * Creates a new language object, storing its translations. Also adds it to
     * {@link Language#languages languages}.
     *
     * @param   languageCode
     *          The <a href="https://iso639-3.sil.org/code_tables/639/data">ISO
     *          639-3</a> code representing this language.
     * @param   language
     *          The properties file in which the language is stored. It should
     *          already be initialised.
     */
    public Language(String languageCode, Properties language) {
        if (!isValidLanguageCode(languageCode))
            throw new IllegalArgumentException("Invalid languageCode");

        this.languageCode = languageCode;
        this.language = language;
        languages.put(languageCode, this);

        if ("eng".equals(languageCode)) english = this;
    }

    /**
     * Gets the <a href="https://iso639-3.sil.org/code_tables/639/data">ISO
     * 639-3</a> code representing this language.
     *
     * @return  The ISO 639-3 code.
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * Checks whether the inputted {@code String} is a valid
     * <a href="https://iso639-3.sil.org/code_tables/639/data">ISO 639-3</a>
     * code.
     *
     * @param   code
     *          The string to be checked.
     *
     * @return  Whether the string is a valid ISO 639-3 code.
     */
    public static boolean isValidLanguageCode(String code) {
        return code != null && LANGUAGE_CODE_PATTERN.matcher(code).matches();
    }

    /**
     * Returns the translation of a piece of text in this language.
     * If this language doesn't have a translation, it will try to fall back on
     * the english translation, surrounded by square brackets.
     * It will return an error message if no english translation is available.
     *
     * @param   textId
     *          The id of the piece of text of which a translation is needed.
     *          Should be constructed using the {@link Text Text}
     *          class.
     *
     * @return the translation of a piece of text.
     */
    String getTranslation(String textId) {
        if (textId == null)
            throw new IllegalArgumentException("Text id is null!");

        String result = language.getProperty(textId);

        if (result != null)
            return result;
        // Exception for the native language name, as this does not have a
        // valid translation in other languages.
        else if (textId.equals(Text.NativeLanguageName))
            return languageCode;
        else if (english == null)
            return "<TEXT DOESN'T EXIST AND NO ENGLISH FALLBACK>";
        else if (this == english)
            return "<TEXT DOESN'T EXIST>";
        else
            return '[' + english.getTranslation(textId) + ']';
    }

    /**
     * Reads a language file and returns the Language object (also adding it to
     * {@link Language#languages languages}).
     *
     * @param   languageCode
     *          The <a href="https://iso639-3.sil.org/code_tables/639/data">ISO
     *          639-3</a> code representing this language.
     * @param   file
     *          The path to the file containing the translations.
     *
     * @return  The resulting {@code Language} object.
     *
     * @throws  IOException
     *          If an I/O error occurs reading from the language file.
     */
    public static Language fromLanguageFile(String languageCode, File file)
            throws IOException {
        PropertiesFile languageReader = new PropertiesFile(file);
        return new Language(languageCode, languageReader.getContent());
    }

    /**
     * Checks if {@code this} is equal to {@code other}. Only considers the
     * language code.
     *
     * @param   other
     *          The object to check.
     *
     * @return  Whether {@code this} and {@code other} are equal.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Language that = (Language) other;
        return Objects.equals(languageCode, that.languageCode);
    }

    /**
     * Generates a hash code corresponding to {@code this}.
     *
     * @return a hash value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(languageCode);
    }
}
