package client.language;


public class Translator {

    private static Language currentLanguage;

    /**
     * Gets the language in which the {@code String}s will be provided as a
     * {@code Language} object.
     *
     * @return  the {@code Language} in which the {@code String}s will be
     *          provided.
     */
    public static Language getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Sets the language in which the {@code String}s will be provided.
     * Updates the language globally. (But a refresh is needed to update
     * existing pieces of text).
     *
     * @param   currentLanguage
     *          The new language.
     */
    public static void setCurrentLanguage(Language currentLanguage) {
        Translator.currentLanguage = currentLanguage;
    }

    /**
     * Returns the translation of a piece of text in the current language.
     * If this language doesn't have a translation, it will try to fall back on
     * the english translation, surrounded by square brackets.
     * It will return an error message if no english language or translation is
     * available.
     *
     * @param   textId
     *          The id of the piece of text of which a translation is needed.
     *          Should be constructed using the {@link Text} class.
     *
     * @return  The translation of a piece of text.
     */
    public static String getTranslation(String textId) {
        return currentLanguage.getTranslation(textId);
    }


}
