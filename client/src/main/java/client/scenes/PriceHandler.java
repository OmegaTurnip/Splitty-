package client.scenes;

import client.language.Text;
import client.language.Translator;
import javafx.scene.control.Alert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface PriceHandler {
    Pattern pricePattern = Pattern.compile("^[0-9]+(?:[.,][0-9]+)?$");
    default boolean verifyPrice(String input, AlertWrapper alertWrapper) {
        Matcher matcher = pricePattern.matcher(input);
        if (!matcher.matches()) {
            choosePriceAlert(input, alertWrapper);
            return false;
        } else return true;

    }

    default void choosePriceAlert(String input, AlertWrapper alertWrapper) {
        if (input.isEmpty()) {
            alertWrapper.showAlert(Alert.AlertType.ERROR, Translator.getTranslation(
                            Text.AddExpense.Alert.invalidPrice),
                    Translator.getTranslation(
                            Text.AddExpense.Alert.emptyString));
        } else if (input.matches("[a-zA-Z]")) {
            alertWrapper.showAlert(Alert.AlertType.ERROR, Translator.getTranslation(
                            Text.AddExpense.Alert.invalidPrice),
                    Translator.getTranslation(Text.AddExpense.Alert.noLetters));
        } else if (input.chars().filter(ch -> ch == ',').count() > 1
                || input.chars().filter(ch -> ch == '.').count() > 1
                || (input.chars().filter(ch -> ch == ',').count() > 0
                && input.chars().filter(ch -> ch == '.').count() > 0)) {
            alertWrapper.showAlert(Alert.AlertType.ERROR, Translator.getTranslation(
                            Text.AddExpense.Alert.invalidPrice),
                    Translator.getTranslation(
                            Text.AddExpense.Alert.onlyOnePeriodOrComma));
        } else if (!Character.isDigit(input.charAt(0))
                || !Character.isDigit(input.charAt(input.length() - 1))) {
            alertWrapper.showAlert(Alert.AlertType.ERROR, Translator.getTranslation(
                            Text.AddExpense.Alert.invalidPrice),
                    Translator.getTranslation(
                            Text.AddExpense.Alert.startWithDigit));
            // If none of the above, consider it as general invalid format
        } else {
            alertWrapper.showAlert(Alert.AlertType.ERROR, Translator.getTranslation(
                            Text.AddExpense.Alert.invalidPrice),
                    Translator.getTranslation(
                            Text.AddExpense.Alert.generallyInvalid));
        }
    }
}
