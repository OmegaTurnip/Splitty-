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
        editTransactionButton.setOnAction(event -> {
            System.out.println("Edit transaction button clicked");
        });
        deleteTransactionButton.setOnAction(event -> {
            System.out.println("Delete transaction button clicked");
        });
    }

    /**
     * Provide label for the transaction.
     * @param transaction transaction
     */
    public void setTransactionData(Transaction transaction) {
        String transactionInfo = String.format("%s %s paid %s for %s (%s)",
                transaction.getDate(),
                transaction.getPayer().getName(),
                transaction.getAmount().format(Translator.getLocale()),
                transaction.getName(),
                transaction.getParticipants().stream()
                        .map(Participant::getName)
                        .collect(Collectors.joining(", ")));

        transactionInfoLabel.setText(transactionInfo);
    }
}
