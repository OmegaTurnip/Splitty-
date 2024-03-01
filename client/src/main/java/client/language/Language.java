package client.language;

import client.utils.ConfigFile;
import client.utils.PropertiesFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.List;
import java.util.regex.Pattern;

public class Language {

    /**
     * A set containing all loaded languages.
     */
    public static final HashMap<String, Language> languages = new HashMap<>();

    /**
     * A default language to fall back on (the default language is English).
     */
    private static Language defaultLanguage;
    private final String languageCode;
    private final Properties language;

    /**
     * A regex for validating a language code. The {@code ^} character denotes
     * the start of the string and the {@code $} the end. So, for a {@code
     * String} to match, the expression between the {@code ^} and {@code $}
     * character should apply to the whole of the {@code String}. The {@code
     * [a-z]{3}} part means that for a {@code String} to match, a character from
     * the ASCII/Unicode range a-z (i.e. a lowercase latin character) should be
     * found exactly 3 times. I.e. the {@code String} should contain, from start
     * to end, exactly 3 lowercase latin characters to match.
     */
    private static final Pattern LANGUAGE_CODE_PATTERN =
            Pattern.compile("^[a-z]{3}$");

    /**
     * Creates a new language object, storing its translations. Also adds it to
     * {@link Language#languages}.
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

        if ("eng".equals(languageCode)) defaultLanguage = this;
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
     *          Should be constructed using the {@link Text}
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
        else if (defaultLanguage == null)
            return "<TEXT DOESN'T EXIST AND NO ENGLISH FALLBACK>";
        else if (this == defaultLanguage)
            return "<TEXT DOESN'T EXIST>";
        else
            return '[' + defaultLanguage.getTranslation(textId) + ']';
    }

    /**
     * Reads a language file and returns the Language object (also adding it to
     * {@link Language#languages}).
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
     * Creates an empty language file containing all known text ids. This only
     * exists because of the following requirement: "To generate an empty
     * language template from the language selection, so I can contribute a new
     * language to the project." This function *SHOULD NOT* be used by us to add
     * a new language, use the {@link TranslationCreator} application for that
     * purpose.
     *
     * @param   file
     *          The file in which the new language will be stored.
     * @param   languageCode
     *          The <a href="https://iso639-3.sil.org/code_tables/639/data">ISO
     *          639-3</a> code representing the new language.
     * @param   languageName
     *          The English name of the language.
     *
     * @throws  IOException
     *          If an I/O error occurs writing to or creating the file.
     */
    public static void createEmptyLanguageFile(File file, String languageCode,
            String languageName) throws IOException {
        if (!isValidLanguageCode(languageCode))
            throw new IllegalArgumentException("Invalid languageCode");

        if (file == null)
            throw new IllegalArgumentException("file is null!");

        if (languageName == null)
            throw new IllegalArgumentException("languageName is null");

        Set<String> textIds = new HashSet<>();
        for (Language language : languages.values()) {
            textIds.addAll(language.language.stringPropertyNames());
        }

        List<String> sortedTextIds = textIds.stream().sorted().toList();

        ConfigFile resultingFile = new ConfigFile(file, "The " +
                languageName + " translation of the program", false);

        for (String textId : sortedTextIds) {
            resultingFile.setAttribute(textId, "");
        }
        resultingFile.flush();
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
