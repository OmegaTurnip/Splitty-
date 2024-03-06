package commons;


import jakarta.persistence.*;

import java.time.LocalDate;

import java.util.*;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @OneToOne
    private Participant payer;
    private String transactionName;
    private LocalDate date;
    private int price;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Participant> participants;
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Event event;
    @OneToOne
    private Tag tag;

    /**
     * Constructor.
     * @param payer The person who paid for the transaction.
     * @param transactionName The name of the transaction.
     * @param price       The price of the transaction (in euro cents).
     * @param participants     The people who owe money
     *                    due to this transaction (key),
     *                    and the amount they owe (value).
     * @param event       The event the transactions belongs to
    * @param tag          The tag of the transaction
     */
    Transaction(Participant payer,
                String transactionName,
                int price, List<Participant> participants,
                Event event, Tag tag) {
        this.payer = payer;
        this.transactionName = transactionName;
        this.date = LocalDate.now();
        this.price = price;
        this.participants = participants;
        this.event = event;
        event.updateLastActivity();
        this.tag = tag;
    }

    /**
     * Constructor without parameters
     */
    public Transaction() {

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
        event.updateLastActivity();
    }

    /**
     * Setter method. Also updates last activity in the corresponding Event.
     *
     * @param transactionName .
     */
    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
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
    public String getTransactionName() {
        return transactionName;
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
     * @param o Transaction to test equality on.
     * @return True or false depending on equality.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction transaction = (Transaction) o;
        return price == transaction.price
                && Objects.equals(payer, transaction.payer)
                && Objects.equals(transactionName, transaction.transactionName)
                && Objects.equals(date, transaction.date)
                && Objects.equals(participants, transaction.participants)
                && Objects.equals(id, transaction.id)
                && Objects.equals(tag, transaction.tag);
    }

    /**
     * Hash code method.
     *
     * @return .
     */
    @Override
    public int hashCode() {
        return Objects.hash(payer, transactionName, date,
                price, participants, id);
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
}