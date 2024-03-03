package commons;


import jakarta.persistence.*;

import java.time.LocalDate;

import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @OneToOne
    private Participant payer;
    private String expenseName;
    private LocalDate date;
    private int price;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Debt> debts;
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Event event;
    @OneToOne
    private Tag tag;

    /**
     * Constructor.
     * @param payer The person who paid for the expense.
     * @param expenseName The name of the expense.
     * @param price       The price of the expense (in dollars).
     * @param debtors     The people who owe money
     *                    due to this expense (key),
     *                    and the amount they owe (value).
     * @param event       The event the expense belongs to
    * @param tag          The tag of the expense
     */
    public Expense(Participant payer,
                   String expenseName,
                   int price, Collection<Participant> debtors,
                   Event event, Tag tag) {
        this.payer = payer;
        this.expenseName = expenseName;
        this.date = LocalDate.now();
        this.price = price;
        this.debts = debtors.stream()
                .map(p -> new Debt(p, price/debtors.size()))
                .collect(Collectors.toList());
        this.event = event;
        event.updateLastActivity();
        this.tag = tag;
    }

    /**
     * Constructor without parameters
     */
    public Expense() {

    }

    /**
 * Setter method
 * @param tag the tag of the expense
 */

    public void setTag(Tag tag) {
        this.tag = tag;
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
     * Getter method for event
     * @return the corresponding Event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Getter of the tag
     * @return tag
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * Equals method.
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
                && Objects.equals(debts, expense.debts)
                && Objects.equals(id, expense.id)
                && Objects.equals(tag, expense.tag);
    }

    /**
     * Hash code method.
     *
     * @return .
     */
    @Override
    public int hashCode() {
        return Objects.hash(payer, expenseName, date, price, debts, id);
    }

    /**
     * Setter for id
     * @param id the id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for id
     * @return the id
     */
    public Long getId() {
        return id;
    }
}
