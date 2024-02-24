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

    /**
     * Constructor.
     *
     * @param payer       The person who paid for the expense.
     * @param expenseName The name of the expense.
     * @param price       The price of the expense (in dollars).
     * @param debtors     The people who owe money
     *                    due to this expense (key),
     *                    and the amount they owe (value).
     */
    public Expense(Participant payer,
                   String expenseName,
                   int price, Collection<Participant> debtors) {
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
    }

    /**
     * Setter method.
     *
     * @param payer .
     */
    public void setPayer(Participant payer) {
        this.payer = payer;
    }

    /**
     * Setter method.
     *
     * @param expenseName .
     */
    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    /**
     * Setter method.
     *
     * @param date .
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Setter method.
     *
     * @param price .
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Setter method.
     *
     * @param debtors .
     */
    public void setDebtors(Map<Participant, Integer> debtors) {
        this.debtors = debtors;
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

