package client.scenes;

import client.language.Language;
import client.language.Text;
import client.language.TextPage;
import client.language.Translator;
import client.utils.ServerUtils;
import client.utils.UserConfig;
import com.google.inject.Inject;
import commons.Debt;
import commons.Event;
import commons.Money;
import commons.Participant;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.Set;

public class DebtPageCtrl extends TextPage implements Initializable {

    /*
     * ServerUtils:
     * simplifyDebts(Event, Currency)
     * getTransactionsOfEvent(Event, Currency)
     * getSumOfAllExpenses(Event, Currency)
     * getBalanceOfParticipants(Event, Currency)
     *
     * Any additional information needed in the payment instruction is 'stored'
     * in debt.to() (retrieved using simplifyDebts(Event, Currency)).
     *
     * All requests should be made using the preferred currency of the user.
     */

    @FXML
    private Accordion openDebtsList;
    @FXML
    private Label openDebtsLabel;
    private Event event;
    private MainCtrl mainCtrl;
    private ServerUtils server;
    private AlertWrapper alertWrapper;
    /**
     * Initializes the controller
     * @param server .
     * @param mainCtrl .
     */
    @Inject
    public DebtPageCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.alertWrapper = new AlertWrapper();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetchLanguages();
    }

    public void refresh() {
        Set<Debt> debts = server
                .simplifyDebts(event, UserConfig.get().getPreferredCurrency());
        openDebtsList.getPanes().clear();
        for(Debt debt : debts) {
            populateAccordion(event, debt);
        }
        refreshText();
    }

    @Override
    public void refreshText() {
        refreshIcon(Translator.getCurrentLanguage().getLanguageCode(),
                languageMenu, Language.languages);
    }

    private void populateAccordion(Event event, Debt debt) {
        if (!debt.from().getName().equals(debt.to().getName())) {
            String title = String.format("%s: %.2f %s => %s",
                    debt.from().getName(),
                    debt.amount().getAmount(),
                    debt.amount().getCurrency(),
                    debt.to().getName());
            TitledPane tp = new TitledPane(title, null);
            openDebtsList.getPanes().add(tp);
            AnchorPane anchorPane = new AnchorPane();
            Label info = new Label();
            Button mark = new Button();
            mark.setVisible(true);
            mark.setText("Settle debt");
            mark.setOnAction(x ->
            {
                addPayoff(event, debt.from(), debt.amount(),
                        debt.to(), mainCtrl.getStartUpDate());
                mark.setText("Settled");
            });
            if (debt.to().getBic()==null ||
                    debt.to().getIban() == null) {
                info.setText("Payment instructions unavailable");
            } else {
                String data = debt.to().getName() + "\nIBAN: " +
                        debt.to().getIban() + "\nBIC: " +
                        debt.to().getBic();

                info.setText(data);

            }

            anchorPane.getChildren().add(info);
            anchorPane.getChildren().add(mark);

            anchorPane.setTopAnchor(info, 10.0);
            anchorPane.setLeftAnchor(info, 10.0);

            // Position the button relative to the label
            anchorPane.setTopAnchor(mark,
                    AnchorPane.getTopAnchor(info) + info.getPrefHeight() + 30.0);
            anchorPane.setLeftAnchor(mark,
                    AnchorPane.getLeftAnchor(info) + info.getPrefWidth() + 300.0);
            tp.setContent(anchorPane);
        }
    }


    /**
     * Check whether the payoff amount is valid. Aka if {@code 0 < payoffAmount
     * <= debt}. <em><strong><font color="#FF0000">PLEASE, PLEASE, PLEASE USE
     * THE DATE OF THE PAGE LOAD AND NOT {@code LocalDate.now()}!!!!!!</font>
     * </strong></em>
     *
     * @param debt         The debt to be paid off.
     * @param payoffAmount The amount the participants want to pay off the debt with.
     * @param date         The date of the payoff. SHOULD BE THE DATE OF THE PAGE LOAD.
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
     * @param date     The date of the payoff. SHOULD BE THE DATE OF THE PAGE LOAD.
     * @return The resulting event.
     */
    public Event addPayoff(Event event, Participant payer, Money amount,
                           Participant receiver, LocalDate date) {
        event.registerPayoff(payer, amount, receiver, date);
        return server.saveEvent(event);
    }

    public void returnToOverview() {
        mainCtrl.showEventOverview(event);
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setMainCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void setServer(ServerUtils server) {
        this.server = server;
    }

    public void setAlertWrapper(AlertWrapper alertWrapper) {
        this.alertWrapper = alertWrapper;
    }
}
