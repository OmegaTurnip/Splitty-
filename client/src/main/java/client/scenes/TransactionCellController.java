package client.scenes;

import client.language.Translator;
import client.utils.ServerUtils;
import commons.Event;
import commons.Transaction;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.stream.Collectors;

public class TransactionCellController {

    private Event event;

    private Transaction transaction;

    private String paid;
    private String forString;

    private ServerUtils server;
    private EventOverviewCtrl eventOverviewCtrl;
    @FXML
    private Label transactionInfoLabel;
    @FXML
    private Button editTransactionButton;
    @FXML
    private Button deleteTransactionButton;


    /**
     * Initialize the controller.
     */
    @FXML
    public void initialize() {
        refreshText();
        editTransactionButton.setOnAction(event -> {
            System.out.println("Edit transaction button clicked");
        });
        deleteTransactionButton.setOnAction(event -> {
            System.out.println("Delete transaction button clicked");
        });
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
        paid = Translator
                .getTranslation(client.language
                        .Text.EventOverview.ExpenseListing.paid);
        forString = Translator.getTranslation(client.language
                .Text.EventOverview.ExpenseListing.for_);
    }

    /**
     * Provide label for the transaction.
     *
     * @param transaction transaction
     */
    public void setTransactionData(Transaction transaction) {
        String transactionInfo = String.format("%s %s %s %s %s %s (%s)",
                transaction.getDate(),
                transaction.getPayer().getName(),
                paid,
                transaction.getAmount().format(Translator.getLocale()),
                forString,
                transaction.getName(),
                transaction.getParticipants().stream()
                        .map(Participant::getName)
                        .collect(Collectors.joining(", ")));

        transactionInfoLabel.setText(transactionInfo);
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

}
