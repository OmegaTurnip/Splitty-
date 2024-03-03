package client.language;

/**
 * This class is a wrapper to make selecting certain sentences easier.
 * Every page/divider should have its own inner-class to separate different
 * parts of the program.
 */
public class Text {

    public static final String NativeLanguageName = "nativelanguagename";

    /**
     * General language class for the common messagebox texts.
     * This class is to be used throughout the entire program (unlike most that
     * will be added later on down the line).
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
