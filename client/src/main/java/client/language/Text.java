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

        private static final String level = "MessageBox.";

        /**
         * General language class for the common messagebox options.
         */
        public static final class Options {

            private static final String level = MessageBox.level + "Options.";

            // Ok is an example of a text id that will be used to select the
            // sentence we want.
            public static final String Ok = level + "Ok";
            public static final String Cancel = level + "Cancel";
            public static final String Abort = level + "Abort";
            public static final String Continue = level + "Continue";
            public static final String Retry = level + "Retry";
            public static final String Ignore = level + "Ignore";
            public static final String Yes = level + "Yes";
            public static final String No = level + "No";
            public static final String Help = level + "Help";





        }
    }

    public static final class Menu{
        private static final String level = "Menu.";

        public static final String Languages = level + "Languages";
        public static final String ReturnToOverview = level +
                "ReturnToOverview";
        public static final String English = level + "English";
        public static final String Dutch = level + "Dutch";
        public static final String German = level + "German";
        public static final String Close = level + "Close";

    }

    public static final class EventOverview {

        private static final String level = "EventOverview.";

        public static final class Buttons {

            private static final String level = EventOverview.level +
                    "Buttons.";
            public static final String Edit = level + "Edit";
            public static final String Add = level + "Add";
            public static final String SendInvite = level + "SendInvite";
            public static final String AddExpense = level + "AddExpense";
            public static final String SettleDebts = level + "SettleDebts";

        }

        public static final class Labels {
            private static final String level = EventOverview.level + "Labels.";
            public static final String Expenses = level + "Expenses";
            public static final String Participants = level + "Participants";

        }
    }

    public static final class AddParticipant{
        private static final String level = "AddParticipant.";

        public static final String Username = level + "Username";
        public static final String Cancel = level + "Cancel";
        public static final String Add = level + "Add";
        public static final String Title = level + "Title";

    }

}
