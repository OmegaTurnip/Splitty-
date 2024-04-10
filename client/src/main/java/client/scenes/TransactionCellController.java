package client.scenes;

import client.language.Formatter;
import client.language.Text;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import commons.Event;
import commons.Money;
import commons.Transaction;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.util.HashMap;
import java.util.stream.Collectors;

public class TransactionCellController {

    private Event event;

    private Transaction transaction;

    private String paid;

    private ServerUtils server;
    private EventOverviewCtrl eventOverviewCtrl;
    @FXML
    private Label transactionInfoLabel;
    @FXML
    private Button editTransactionButton;
    @FXML
    private Button deleteTransactionButton;
    private AlertWrapper alertWrapper;


    /**
     * Initialize the controller.
     */
    @FXML
    public void initialize() {
        refreshText();
        alertWrapper = new AlertWrapper();
        editTransactionButton.setOnAction(event ->
                System.out.println("Edit transaction button clicked"));
        deleteTransactionButton.setOnAction(event -> removeTransaction());
    }

    /**
     * Removes the transaction of the cell from the event
     * in the application and from the database
     */
    public void removeTransaction() {
        if (transaction != null) {
            ButtonType result = alertWrapper.showAlertButton(
                    Alert.AlertType.CONFIRMATION,
                    Translator.getTranslation(Text
                            .EventOverview
                            .TransactionCellController
                            .Alert.deleteExpenseTitle),
                    Translator.getTranslation(Text
                            .EventOverview
                            .TransactionCellController
                            .Alert.deleteExpenseContent)
            );
            if (result == ButtonType.OK) {
                server.removeTransaction(transaction);
                event.deleteTransaction(transaction);
                eventOverviewCtrl.refresh();
                System.out.println("Delete transaction button clicked");
            }
        }
    }

    /**
     *
     */
    public void refresh() {
        refreshText();
    }

    /**
     *
     */
    void refreshText() {
        paid = Translator.getTranslation(
                Text.EventOverview.ExpenseListing.paid);
    }

    /**
     * Provide label for the transaction.
     * @param transaction transaction
     */
    public void setTransactionData(Transaction transaction) {
        refreshText();
        HashMap<String, String> transactionInfo = new HashMap<>();
        transactionInfo.put("date", transaction.getDate().toString());
        transactionInfo.put("payer", transaction.getPayer().getName());
        Money shownAmount = server.convertMoney(transaction.getAmount(),
                UserConfig.get().getPreferredCurrency(), transaction.getDate());
        transactionInfo.put("amount",
                shownAmount.format(Translator.getLocale()));
        transactionInfo.put("name",transaction.getName());
        transactionInfo.put("participants",
                transaction.getParticipants().stream()
                        .map(Participant::getName)
                        .collect(Collectors.joining(", ")));
        transactionInfoLabel.setText(Formatter.format(paid, transactionInfo));
    }

    /**
     * Sets the event
     *
     * @param event The event to be set
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Set the transaction
     *
     * @param transaction the transaction of the cell
     */
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    /**
     * Sets the server
     *
     * @param server server
     */
    public void setServer(ServerUtils server) {
        this.server = server;
    }

    /**
     * Set the eventOverviewController
     *
     * @param eventOverviewCtrl the event overview controller
     */
    public void setEventOverviewCtrl(EventOverviewCtrl eventOverviewCtrl) {
        this.eventOverviewCtrl = eventOverviewCtrl;
    }


    /**
     * Setter
     * @param alertWrapper the alertWrapper to set
     */
    public void setAlertWrapper(AlertWrapper alertWrapper) {
        this.alertWrapper = alertWrapper;
    }

}
