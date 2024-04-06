package commons;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

import java.util.*;


@Entity
@IdClass(TransactionId.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @Id
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference
    private Event event;
    @ManyToOne
    private Participant payer;
    private String name;
    private LocalDate date;
    @Column(name = "amount", length = 1024)
    private Money amount;
    private boolean isPayoff;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Participant> participants;
    @ManyToOne
    private Tag tag;

    /**
     * General purpose constructor.
     *
     * @param   payer
     *          The person who paid for the transaction.
     * @param   name
     *          The name of the transaction.
     * @param   amount
     *          The amount of money transferred in the transaction.
     * @param   participants
     *          The people/person who owe/gets money.
     * @param   event
     *          The event the transactions belongs to.
     * @param   tag
     *          The tag of the transaction.
     * @param   isPayoff
     *          Whether this transaction is a payoff or a debt.
     */
    @SuppressWarnings("checkstyle:ParameterNumber") // no other option really
    private Transaction(Participant payer, String name, Money amount,
                        List<Participant> participants, Event event, Tag tag,
                        boolean isPayoff) {
        this.payer = payer;
        this.name = name;
        this.date = LocalDate.now();
        this.amount = amount;
        this.participants = participants;
        this.event = event;
//        event.updateLastActivity();
        this.tag = tag;
        this.isPayoff = isPayoff;
        event.updateLastActivity();
    }

    /**
     * Constructor without parameters.
     */
    public Transaction() {

    }

    /**
     * Creates a transaction that creates a payoff.
     *
     * @param   payer
     *          The person who paid for the transaction.
     * @param   name
     *          The name of the transaction.
     * @param   amount
     *          The amount of money transferred in the transaction.
     * @param   receiver
     *          The person who received money.
     * @param   event
     *          The event the transactions belongs to
     * @param   tag
     *          The tag of the transaction.
     *
     * @return  The resulting transaction.
     */
    public static Transaction createPayoff(Participant payer, String name,
                                            Money amount, Participant receiver,
                                            Event event, Tag tag) {
        return new Transaction(payer, name, amount, List.of(receiver), event,
                tag, true);
    }

    /**
     * Creates a transaction that creates a debt. Note that the difference
     * between payoff and not payoff is the participants list.
     *
     * @param   creditor
     *          The person who paid for the transaction.
     * @param   name
     *          The name of the transaction.
     * @param   amount
     *          The amount of money transferred in the transaction.
     * @param   debtors
     *          The people who owe money.
     * @param   event
     *          The event the transactions belongs to
     * @param   tag
     *          The tag of the transaction.
     *
     * @return  The resulting transaction.
     */
    public static Transaction createDebt(
            Participant creditor, String name, Money amount,
            List<Participant> debtors, Event event, Tag tag) {
        return new Transaction(creditor, name, amount, debtors, event, tag,
                false);
    }



    /**
    * Setter method
    * @param tag the tag of the transaction
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
//        event.updateLastActivity();
    }

    /**
     * Setter method. Also updates last activity in the corresponding Event.
     *
     * @param transactionName .
     */

    public void setName(String transactionName) {
        this.name = transactionName;
    }

    /**
     * Setter method. Also updates last activity in the corresponding Event.
     *
     * @param date .
     */
    public void setDate(LocalDate date) {
        this.date = date;
//        event.updateLastActivity();
    }

    /**
     * Setter method. Also updates last activity in the corresponding Event.
     *
     * @param price .
     */
    public void setAmount(Money price) {
        this.amount = price;
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
    public String getName() {
        return name;
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
    public Money getAmount() {
        return amount;
    }

    /**
     * Returns whether this transaction is a payoff or a 'debt'.
     *
     * @return  Whether this transaction is a payoff or a 'debt'.
     */
    public boolean isPayoff() {
        return isPayoff;
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
     * Checks whether this {@code Transaction} is valid, i.e. no fields are
     * {@code null}.
     *
     * @return  Whether this {@code Transaction} is valid.
     */
    public boolean isValid() {
        return event != null && name != null && !name.isEmpty() && payer != null
                && amount != null && participants != null
                && !participants.isEmpty();
    }


    /**
     * Checks if {@code this} is equal to {@code other}.
     *
     * @param   other
     *          The object to check.
     *
     * @return  Whether {@code this} and {@code other} are equal.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Transaction that = (Transaction) other;
        return Objects.equals(id, that.id)
                && Objects.equals(event, that.event);
    }

    /**
     * Generates a hash code corresponding to {@code this}.
     *
     * @return  A hash value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(event, id);
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

    /**
     * Getter for participants
     * @return the participants
     */
    public List<Participant> getParticipants() {
        return participants;
    }

    /**
     * Setter for participants
     * @param participants the participants to set
     */
    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    /**
     * Setter for event
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }
}
