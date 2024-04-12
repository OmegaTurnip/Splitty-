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
    private Long transactionId;
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
    @ManyToMany
    private List<Participant> participants;
    @ManyToOne
    private Tag tag;

    private Long longPollingEventId; // for serialization


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
     * @param   date
     *          The date of the transaction.
     * @param   isPayoff
     *          Whether this transaction is a payoff or a debt.
     */
    @SuppressWarnings("checkstyle:ParameterNumber") // no other option really
    public Transaction(Participant payer, String name, Money amount,
                       List<Participant> participants, Event event, Tag tag,
                       LocalDate date, boolean isPayoff) {
        this.payer = payer;
        this.name = name;
        this.date = date;
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
     * @param   amount
     *          The amount of money transferred in the transaction.
     * @param   receiver
     *          The person who received money.
     * @param   event
     *          The event the transactions belongs to
     * @param   date
     *          The date of the payoff.
     *
     * @return  The resulting transaction.
     */
    public static Transaction createPayoff(Participant payer, Money amount,
                                           Participant receiver, Event event,
                                           LocalDate date) {
        return new Transaction(payer, null, amount, List.of(receiver), event,
                null, date,  true);
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
     * @param   date
     *          The date of the payoff.
     * @param   tag
     *          The tag of the transaction.
     *
     * @return  The resulting transaction.
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public static Transaction createDebt(
            Participant creditor, String name, Money amount,
            List<Participant> debtors, Event event, LocalDate date, Tag tag) {
        return new Transaction(creditor, name, amount, debtors, event, tag,
                date, false);
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
        return Objects.equals(transactionId, that.transactionId)
                && Objects.equals(event, that.event);
    }

    /**
     * Generates a hash code corresponding to {@code this}.
     *
     * @return  A hash value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(event, transactionId);
    }

    /**
     * Setter for id
     * @param id the id
     */
    public void setTransactionId(Long id) {
        this.transactionId = id;
    }

    /**
     * Getter for id
     * @return the id
     */
    public Long getTransactionId() {
        return transactionId;
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

    /**
     * getter for EventId
     * @return the eventId
     */
    public Long getLongPollingEventId() {
        return longPollingEventId;
    }
    /**
     * setter for EventId
     * This allows for the long-polling
     * to work properly by knowing to which event
     * the transaction belongs
     * @param eventId the eventId to set
     */
    public void setLongPollingEventId(Long eventId) {
        this.longPollingEventId = eventId;
    }

    /**
     * Turns a transaction into a human-readable string
     * @return the string
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", event=" + event +
                ", payer=" + payer +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", amount=" + amount +
                ", isPayoff=" + isPayoff +
                ", participants=" + participants +
                ", tag=" + tag +
                '}';
    }
}
