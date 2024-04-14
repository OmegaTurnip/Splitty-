package client.scenes;

import client.language.*;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Inject;
import commons.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class DebtPageCtrl extends TextPage
        implements Initializable, PriceHandler {


    @FXML
    private Accordion openDebtsList;
    @FXML
    private Label openDebtsLabel;
    @FXML
    private Label noOpenDebtsLabel;
    private Event event;
    private MainCtrl mainCtrl;
    private ServerUtils server;
    private AlertWrapper alertWrapper;
    private LocalDate startUpDate;

    /**
     * Initializes the controller
     *
     * @param server   the serverUtils
     * @param mainCtrl the mainctrl
     */
    @Inject
    public DebtPageCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.alertWrapper = new AlertWrapper();
    }

    /**
     * Initialise the page.
     *
     * @param location  The location used to resolve relative paths
     *                  for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root
     *                  object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetchLanguages();
        server.registerForMessages("/topic/admin", Event.class, e -> {
            Platform.runLater(() -> {
                if (event == null) return;
                if (event.equals(e)) event = e;
                refresh();
            });
        });
        server.registerForMessages("/topic/admin/delete", Event.class, e -> {
            if (event == null) return;
            if (event.equals(e)) {
                Platform.runLater(() -> {
                    mainCtrl.showStartUp();
                    alertWrapper.showAlert(Alert.AlertType.ERROR,
                            Translator.getTranslation(
                                    Text.EventOverview.Alert.deletedEventTitle),
                            Translator.getTranslation(
                                    Text.EventOverview.Alert.
                                            deletedEventContent)
                    );
                });
            }
        });
    }

    /**
     * Refreshes the contents of the page
     */
    public void refresh() {

        Set<Debt> debts = server
                .simplifyDebts(event, UserConfig.get().getPreferredCurrency());
        openDebtsList.getPanes().clear();
        for (Debt debt : debts) {
            populateAccordion(event, debt);
        }
        noOpenDebtsLabel.setVisible(debts.isEmpty());
    }

    /**
     * Refreshes the text of the page
     */
    @Override
    public void refreshText() {
        refresh();
        refreshIcon(Translator.getCurrentLanguage().getLanguageCode(),
                languageMenu, Language.languages);
        noOpenDebtsLabel.setText(Translator.getTranslation(
                Text.DebtPage.noOpenDebts));
        openDebtsLabel.setText(Translator.getTranslation(
                Text.DebtPage.openDebts));

    }

    /**
     * Populates the accordion with a debt
     *
     * @param event the event
     * @param debt  the debt
     */
    @SuppressWarnings("checkstyle:MethodLength")
    private void populateAccordion(Event event, Debt debt) {
        if (!debt.from().getName().equals(debt.to().getName())) {

            HashMap<String, String> params = new HashMap<>();
            params.put("from", debt.from().getName());
            params.put("amount", debt.amount().format(Translator.getLocale()));
            params.put("to", debt.to().getName());

            String title = Formatter.format(Translator.getTranslation(
                    Text.DebtPage.debtTitle),
                    params
            );

            TitledPane tp = new TitledPane(title, null);
            openDebtsList.getPanes().add(tp);
            AnchorPane anchorPane = new AnchorPane();
            Label info = new Label();
            Button settle = new Button();
            settle.setVisible(true);
            settle.setText(Translator.getTranslation(
                    Text.DebtPage.settleDebt));
            settle.setOnAction(x -> payOff(event, debt, settle));
            if (debt.to().getBic().isEmpty() ||
                    debt.to().getIban().isEmpty()) {
                info.setText(Translator.getTranslation(
                        Text.DebtPage.noPaymentInstructions
                ));
            } else {
                String data = debt.to().getName() + "\nIBAN: " +
                        debt.to().getIban() + "\nBIC: " +
                        debt.to().getBic();
                info.setText(data);
            }
            anchorPane.getChildren().add(info);
            anchorPane.getChildren().add(settle);

            anchorPane.setTopAnchor(info, 10.0);
            anchorPane.setLeftAnchor(info, 10.0);

            anchorPane.setTopAnchor(settle,
                    AnchorPane.getTopAnchor(info) +
                            info.getPrefHeight() + 30.0);
            anchorPane.setLeftAnchor(settle,
                    AnchorPane.getLeftAnchor(info) +
                            info.getPrefWidth() + 300.0);
            tp.setContent(anchorPane);
        }
    }


    @SuppressWarnings("checkstyle:MethodLength")
    private void payOff(Event event, Debt debt, Button mark) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(Translator.getTranslation(Text.DebtPage.moneyTransfer));
        ButtonType settleTransfer = new ButtonType(
                Translator.getTranslation(Text.DebtPage.settleTransfer),
                ButtonBar.ButtonData.APPLY);
        ButtonType cancel = new ButtonType(
                Translator.getTranslation(Text.DebtPage.cancel),
                ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes()
                .addAll(cancel, settleTransfer);
        TextField amount = new TextField();
        amount.setText(debt.amount().getAmount().toString());
        dialog.getDialogPane().setContent(amount);
        AtomicBoolean doNotAllowClose = new AtomicBoolean(true);
        dialog.setResultConverter(button -> {
            if (button == settleTransfer) {
                String payment = amount.getText();
                if (verifyPrice(payment, alertWrapper)) {
                    Money paymentAmount = new Money(new BigDecimal(payment),
                            UserConfig.get().getPreferredCurrency());
                    if (isValidPayoffAmount(debt.amount(), paymentAmount,
                            startUpDate)) {
                        addPayoff(event, debt.from(), paymentAmount, debt.to(),
                                startUpDate);
                        doNotAllowClose.set(false);
                    } else {
                        alertWrapper.showAlert(
                                Alert.AlertType.ERROR,
                                Translator.getTranslation(Text.DebtPage.Alert.
                                        invalidPayoffAmountTitle),
                                Translator.getTranslation(Text.DebtPage.Alert.
                                        invalidPayoffAmountContent)
                        );
                        return null;
                    }
                }
            }
            return null;
        });
        dialog.setOnCloseRequest(e -> {
            if (doNotAllowClose.get()) {
                e.consume(); // Prevent the dialog from closing
            }
        });
        dialog.showAndWait();
    }

    /**
     * Check whether the payoff amount is valid. Aka if {@code 0 < payoffAmount
     * <= debt}. <em><strong><font color="#FF0000">PLEASE, PLEASE, PLEASE USE
     * THE DATE OF THE PAGE LOAD AND NOT {@code LocalDate.now()}!!!!!!</font>
     * </strong></em>
     *
     * @param debt         The debt to be paid off.
     * @param payoffAmount The amount the participants
     *                     want to pay off the debt with.
     * @param date         The date of the payoff. SHOULD
     *                     BE THE DATE OF THE PAGE LOAD.
     * @return Whether the payoff amount is valid.
     */
    private boolean isValidPayoffAmount(Money debt, Money payoffAmount,
                                        LocalDate date) {
        if (payoffAmount.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            return false;

        if (debt.getCurrency().equals(payoffAmount.getCurrency()))
            return payoffAmount.compareTo(debt) <= 0;

        return server.convertMoney(
                payoffAmount,
                debt.getCurrency(),
                date
        ).compareTo(debt) <= 0;
    }

    /**
     * Adds a payoff to the event. <em><strong><font color="#FF0000">PLEASE,
     * PLEASE, PLEASE USE THE DATE OF THE PAGE LOAD AND NOT {@code
     * LocalDate.now()}!!!!!!</font></strong></em> This to prevent a
     * synchronization issues at midnight with the exchange rate.
     * Also doubles as a method to add general, unbound payments to another
     * participant (in which case the page load thingy doesn't really matter).
     *
     * @param event    The {@link Event} to add the payoff to.
     * @param payer    The payer of the payoff.
     * @param amount   The amount of the payoff.
     * @param receiver The receiver of the payoff.
     * @param date     The date of the payoff.
     *                 SHOULD BE THE DATE OF THE PAGE LOAD.
     */
    public void addPayoff(Event event, Participant payer, Money amount,
                          Participant receiver, LocalDate date) {
        Transaction t = event.registerPayoff(payer, amount, receiver, date);
        server.saveEvent(event);

    }

    /**
     * Changes the page to event overview
     */
    public void returnToOverview() {
        mainCtrl.showEventOverview(event);
    }

    /**
     * Setter
     *
     * @param event the event
     */
    public void setEvent(Event event) {
        this.event = event;
        server.registerForUpdates(t -> {
            try {
                Platform.runLater(this::refresh);
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }, event);
    }

    /**
     * Setter
     *
     * @param mainCtrl the mainCtrl
     */
    public void setMainCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Setter
     *
     * @param server the server
     */
    public void setServer(ServerUtils server) {
        this.server = server;
    }

    /**
     * Setter
     *
     * @param alertWrapper the alertWrapper
     */
    public void setAlertWrapper(AlertWrapper alertWrapper) {
        this.alertWrapper = alertWrapper;
    }

    /**
     * Setter
     * @param now date of the page load
     */
    public void setStartUpDate(LocalDate now) {
        this.startUpDate = now;
    }
}
