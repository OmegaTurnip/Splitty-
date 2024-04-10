package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import commons.Money;
import commons.Participant;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DebtPageCtrl {
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

    private ServerUtils server;


    /**
     * Check whether the payoff amount is valid. Aka if {@code 0 < payoffAmount
     * <= debt}. <em><strong><font color="#FF0000">PLEASE, PLEASE, PLEASE USE
     * THE DATE OF THE PAGE LOAD AND NOT {@code LocalDate.now()}!!!!!!</font>
     * </strong></em>
     *
     * @param   debt
     *          The debt to be paid off.
     * @param   payoffAmount
     *          The amount the participants want to pay off the debt with.
     * @param   date
     *          The date of the payoff. SHOULD BE THE DATE OF THE PAGE LOAD.
     *
     * @return  Whether the payoff amount is valid.
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
     * @param   event
     *          The {@link Event} to add the payoff to.
     * @param   payer
     *          The payer of the payoff.
     * @param   amount
     *          The amount of the payoff.
     * @param   receiver
     *          The receiver of the payoff.
     * @param   date
     *          The date of the payoff. SHOULD BE THE DATE OF THE PAGE LOAD.
     *
     * @return  The resulting event.
     */
    public Event addPayoff(Event event, Participant payer, Money amount,
                           Participant receiver, LocalDate date) {
        event.registerPayoff(payer, amount, receiver, date);
        return server.saveEvent(event);
    }
}
