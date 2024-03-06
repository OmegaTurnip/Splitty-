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

    public static final class Menu{
        private static final String level = "menu.";

        public static final String Languages = level + "Languages";
        public static final String RTO = level + "RTO";
        public static final String English = level + "English";
        public static final String Dutch = level + "Dutch";
        public static final String German = level + "German";
        public static final String Close = level + "Close";

    }

    public static final class EventOverview {

        private static final String level = "eventoverview.";

        public static final class Buttons {

            private static final String level = EventOverview.level +
                    "buttons.";
            public static final String Edit = level + "Edit";
            public static final String Add = level + "Add";
            public static final String SendInvite = level + "SendInvite";
            public static final String AddExpense = level + "AddExpense";
            public static final String SettleDebts = level + "SettleDebts";

        }

        public static final class Labels {
            private static final String level = EventOverview.level + "labels.";
            public static final String Expenses = level + "Expenses";
            public static final String Participants = level + "Participants";

        }
    }

    public static final class AddParticipant{
        private static final String level = "addparticipant.";

        public static final String Username = level + "Username";
        public static final String Cancel = level + "Cancel";
        public static final String Add = level + "Add";
        public static final String Title = level + "Title";

    }

}
