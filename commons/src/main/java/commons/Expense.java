package commons;


import java.time.LocalDate;

import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

public class Expense {

    private Participant payer;
    private String expenseName;
    private LocalDate date;
    private int price;
    private Map<Participant, Integer> debtors;
    private Event event;

    /**
     * Constructor.
     *
     * @param payer       The person who paid for the expense.
     * @param expenseName The name of the expense.
     * @param price       The price of the expense (in dollars).
     * @param debtors     The people who owe money
     *                    due to this expense (key),
     *                    and the amount they owe (value).
     * @param event       The event the expense belongs to
     */
    public Expense(Participant payer,
                   String expenseName,
                   int price, Collection<Participant> debtors, Event event) {
        this.payer = payer;
        this.expenseName = expenseName;
        this.date = LocalDate.now();
        this.price = price;
        Map<Participant, Integer> mapDebtors = new HashMap<>();
        for (Participant participant : debtors) {
            if (!participant.equals(payer)) {
                mapDebtors.put(participant, price);
            }
        }
        this.debtors = mapDebtors;
        this.event = event;
        event.updateLastActivity();
    }

    /**
     * Setter method. Also updates last activity in the corresponding Event.
     *
     * @param payer .
     */
    public void setPayer(Participant payer) {
        this.payer = payer;
        event.updateLastActivity();
    }

    /**
     * Setter method. Also updates last activity in the corresponding Event.
     *
     * @param expenseName .
     */
    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
        event.updateLastActivity();
    }

    /**
     * Setter method. Also updates last activity in the corresponding Event.
     *
     * @param date .
     */
    public void setDate(LocalDate date) {
        this.date = date;
        event.updateLastActivity();
    }

    /**
     * Setter method. Also updates last activity in the corresponding Event.
     *
     * @param price .
     */
    public void setPrice(int price) {
        this.price = price;
        event.updateLastActivity();
    }

    /**
     * Setter method. Also updates last activity in the corresponding Event.
     *
     * @param debtors .
     */
    public void setDebtors(Map<Participant, Integer> debtors) {
        this.debtors = debtors;
        event.updateLastActivity();
    }

    /**
     * Getter method.
     *
     * @return .
     */
    public Participant getPayer() {
        return payer;
    }

    /**
     * Getter method.
     *
     * @return .
     */
    public String getExpenseName() {
        return expenseName;
    }

    /**
     * Getter method.
     *
     * @return .
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Getter method.
     *
     * @return .
     */
    public int getPrice() {
        return price;
    }

    /**
     * Getter method.
     *
     * @return .
     */
    public Map<Participant, Integer> getDebtors() {
        return debtors;
    }

    /**
     * Getter method for event
     * @return the corresponding Event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Equals method.
     *
     * @param o Expense to test equality on.
     * @return True or false depending on equality.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return price == expense.price
                && Objects.equals(payer, expense.payer)
                && Objects.equals(expenseName, expense.expenseName)
                && Objects.equals(date, expense.date)
                && Objects.equals(debtors, expense.debtors);
    }

    /**
     * Hash code method.
     *
     * @return .
     */
    @Override
    public int hashCode() {
        return Objects.hash(payer, expenseName, date, price, debtors);
    }
}

