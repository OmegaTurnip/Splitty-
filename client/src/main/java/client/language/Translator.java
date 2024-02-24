package client.language;


/**
 * This class is a wrapper to make selecting certain sentences easier.
 * Every page/divider should have its own inner-class to separate different
 * parts of the program.
 */
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
     *          Should be constructed using the {@link Translator Translator}
     *          class.
     *
     * @return the translation of a piece of text.
     */
    public static String getTranslation(String textId) {
        return currentLanguage.getTranslation(textId);
    }

    public static final String NativeLanguageName = "nativelanguagename";

    /**
     * General language class for the common messagebox texts.
     * This class is to be used throughout the entire program (unlike most).
     */
    public static final class MessageBox {

        private static final String level = "messagebox.";

        /**
         * General language class for the common messagebox options.
         */
        public static final class Options {

            private static final String level = MessageBox.level + "options.";

            // Ok is an example of a text id that will be used to select the
            // sentence we want.
            public static final String Ok = level + "ok";
            public static final String Cancel = level + "cancel";
            public static final String Abort = level + "abort";
            public static final String Continue = level + "continue";
            public static final String Retry = level + "retry";
            public static final String Ignore = level + "ignore";
            public static final String Yes = level + "yes";
            public static final String No = level + "no";
            public static final String Help = level + "help";
        }
    }



}
