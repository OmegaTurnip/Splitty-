package client.language;

/**
 * This class is a wrapper to make selecting certain sentences easier.
 * Every page/divider should have its own inner-class to separate different
 * parts of the program.
 */
public class Text {

    public static final String NativeLanguageName = "NativeLanguageName";
    static final String Locale = "Locale";

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

    public static final class Menu {
        private static final String level = "Menu.";

        public static final String Languages = level + "Languages";
        public static final String ReturnToOverview = level +
                "ReturnToOverview";
        public static final String Close = level + "Close";

    }

    public static final class EventOverview {

        private static final String level = "EventOverview.";
        public static final String expensesLabel = level
                + "expensesLabel";
        public static final String participantsLabel = level
                + "participantsLabel";

        public static final String expensesDropDown = level
                + "expensesDropDown";

        public static final class ExpenseListing {
            private static final String level = EventOverview.level +
                    "ExpenseListing.";

            public static final String paid = level + "paid";
        }

        public static final class Alert {
            private static final String level = EventOverview.level +
                    "Alert.";
            public static final String deletedEventTitle = level +
                    "deletedEventTitle";
            public static final String deletedEventContent = level +
                    "deletedEventContent";
        }


        public static final class Buttons {

            private static final String level = EventOverview.level +
                    "Buttons.";
            public static final String sendInviteButton = level
                    + "sendInviteButton";
            public static final String settleDebtsButton = level
                    + "settleDebtsButton";
            public static final String allExpensesButton =
                    level + "allExpensesButton";
            public static final String includingExpensesButton =
                    level + "includingExpensesButton";

            public static final String fromExpensesButton =
                    level + "fromExpensesButton";

        }

        public static class ParticipantCellController {
            public static final String level = EventOverview.level +
                    "ParticipantCellController.";

            public static final class Alert {
                public static final String level = ParticipantCellController
                        .level + "Alert.";
                public static final String deleteParticipantTitle = level +
                        "deleteParticipantTitle";
                public static final String deleteParticipantContent = level +
                        "deleteParticipantContent";

            }
        }

        public static class TransactionCellController {
            public static final String level = EventOverview.level +
                    "TransactionCellController.";

            public static final class Alert {
                public static final String level = TransactionCellController
                        .level + "Alert.";
                public static final String deleteExpenseTitle = level +
                        "deleteExpenseTitle";
                public static final String deleteExpenseContent = level +
                        "deleteExpenseContent";

            }
        }
    }

    public static final class AddParticipant {
        private static final String level = "AddParticipant.";

        public static final String Username = level + "Username";
        public static final String Email = level + "Email";
        public static final String IBAN = level + "IBAN";
        public static final String BIC = level + "BIC";
        public static final String Cancel = level + "Cancel";
        public static final String Add = level + "Add";
        public static final String Title = level + "Title";
        public static final String EditTitle = level + "EditTitle";

        public static final class Alert {
            public static final String level = AddParticipant.level + "Alert.";
            public static final String NoName = level + "NoName";

            public static final String InvalidMail = level + "InvalidMail";
            public static final String InvalidIBAN = level + "InvalidIBAN";
            public static final String InvalidBIC = level + "InvalidBIC";

            public static final String DuplicateError = level +
                    "DuplicateError";
            public static final String EmptyError = level + "EmptyError";
            public static final String FormatError = level + "FormatError";

            public static final String DuplicateErrorContent = level +
                    "DuplicateErrorContent";
        }


    }

    public static final class StartUp {
        private static final String level = "StartUp.";

        public static final String createNewEventLabel =
                level + "createNewEventLabel";

        public static final String joinEventLabel = level + "joinEventLabel";

        public static final String yourEventsLabel = level + "yourEventsLabel";

        public static final String languagesMenu = level + "languagesMenu";

        public static final String currencyMenu = level + "currencyMenu";

        public static final class Menu {
            public static final String level = StartUp.level + "Menu.";
            public static final String removeYourEvents =
                    level + "removeYourEvents";
        }

        public static final class Buttons {
            public static final String level = StartUp.level + "Buttons.";
            public static final String NewEventButton =
                    level + "NewEventButton";
            public static final String JoinEventButton =
                    level + "JoinEventButton";

        }

        public static final class Alert {
            public static final String level = StartUp.level + "Alert.";
            public static final String noEventWritten =
                    level + "noEventWritten";
            public static final String alreadyInEvent =
                    level + "alreadyInEvent";
            public static final String removeEventHeader =
                    level + "removeEventHeader";
            public static final String removeEventContent =
                    level + "removeEventContent";
        }
    }

    public static final class EditName {
        private static final String level = "EditName.";

        public static final String inputName = level + "inputName";

        public static final String confirm = level + "confirm";

        public static final class Alert {
            public static final String level = EditName.level + "Alert.";
            public static final String confirmTitle = level + "confirmTitle";
            public static final String confirmContent = level
                    + "confirmContent";
        }
    }

    public static final class Admin {

        private static final String level = "Admin.";

        public static final String eventsLabel = level + "eventsLabel";
        public static final String title = level + "title";
        public static final String creationDate = level + "creationDate";
        public static final String lastActivity = level + "lastActivity";

        public static final class Alert {
            public static final String level = Admin.level + "Alert.";

            public static final String unauthorisedTitle = level
                    + "unauthorisedTitle";
            public static final String unauthorisedContent = level
                    + "unauthorisedContent";
            public static final String restoreEventAlertTitle = level
                    + "restoreEventAlertTitle";
            public static final String restoreEventAlertContent = level
                    + "restoreEventAlertContent";
            public static final String eventLoadedTitle = level
                    + "eventLoadedTitle";
            public static final String eventLoadedContent = level
                    + "eventLoadedContent";
            public static final String JSONUnselectedTitle = level
                    + "JSONUnselectedTitle";
            public static final String JSONUnselectedContent = level
                    + "JSONUnselectedContent";
            public static final String saveToJSONSuccessTitle = level
                    + "saveToJSONSuccessTitle";
            public static final String saveToJSONSuccessContent = level
                    + "saveToJSONSuccessContent";
            public static final String restoreEventAlertSuccessTitle = level
                    + "restoreEventAlertSuccessTitle";
            public static final String restoreEventAlertSuccessContent = level
                    + "restoreEventAlertSuccessContent";
        }

        public static final class Buttons {
            public static final String level = Admin.level + "Buttons.";
            public static final String saveToJSON = level + "saveToJSON";
            public static final String loadFromJSON = level + "loadFromJSON";
            public static final String deleteEvent = level + "deleteEvent";
            public static final String restoreEvent = level + "restoreEvent";
        }

    }

    public static final class Alert {
        private static final String level = "Alert.";
        public static final String serverDownTitle = level + "serverDownTitle";
        public static final String serverDownContent =
                level + "serverDownContent";
    }

    public static final class AddExpense {
        private static final String level = "AddExpense.";
        public static final String expenseNamePrompt = level + "namePrompt";
        public static final String expensePricePrompt = level + "pricePrompt";
        public static final String expensePayerPrompt = level + "payerPrompt";
        public static final String expenseDatePrompt = level + "datePrompt";
        public static final String expenseParticipantsPrompt =
                level + "participantsPrompt";
        public static final String expenseTypePrompt = level + "typePrompt";
        public static final String participantsEveryone = level + "everyone";

        public static final class Button {
            public static final String level = AddExpense.level + "Buttons.";
            public static final String addExpenseButton = level + "addExpense";
        }

        public static final class Alert {
            public static final String level = AddExpense.level + "Alert.";
            public static final String invalidPrice = level + "invalidPrice";
            public static final String emptyString = level + "emptyString";
            public static final String startWithDigit = level +
                    "mustStartWithDigit";
            public static final String noLetters = level + "noLettersAllowed";
            public static final String onlyOnePeriodOrComma = level +
                    "onlyOnePeriodOrCommaAllowed";
            public static final String generallyInvalid = level +
                    "generallyInvalid";
        }

    }

}
