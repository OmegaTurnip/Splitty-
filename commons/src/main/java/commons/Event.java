package commons;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.List;
@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String eventName;
    private LocalDate eventCreationDate;
    private String inviteCode;
    @OneToMany(cascade = CascadeType.ALL)
    private Collection<Transaction> transactions;
    @OneToMany(cascade = CascadeType.ALL)
    private Collection<Participant> participants;
    private LocalDateTime lastActivity;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Tag> tags;

    /**
     * Constructor for an event. The attributes should be editable.
     *
     * @param eventName The name of the event.
     */
    public Event(String eventName) {
        this.eventName = eventName;
        this.eventCreationDate = LocalDate.now();
        this.inviteCode = generateInviteCode();
        this.transactions = new ArrayList<Transaction>();
        this.participants = new ArrayList<Participant>();
        this.tags = new ArrayList<Tag>();
        basicTags();
        updateLastActivity();
    }

    /**
     * method for adding the three standard tags
     */
    public void basicTags() {
        addTag(new Tag("food", "blue"));
        addTag(new Tag("entrance fees", "green"));
        addTag(new Tag("Travel", "yellow"));
    }

    /**
     * Constructor without parameters
     */
    public Event() {
    }

    /**
     * Method for generating a random invite code upon calling.
     *
     * @return Random invite code.
     */
    public static String generateInviteCode() {
        UUID randomCode = UUID.randomUUID();
        return randomCode.toString()
                .replaceAll("-", "");
    }

    /**
     * Calculates the total sum of all the transactions in the event.
     *
     * @return The total sum.
     */
    public int totalSumOfExpenses() {
        int result = 0;
        for (Transaction transaction : transactions) {
            result += transaction.getPrice();
        }
        return result;
    }

    /**
     * getter method
     *
     * @return list of tags
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * adds a tag
     *
     * @param tag to be added
     */
    public void addTag(Tag tag) {
        tags.add(tag);
    }

    /**
     * Getter method.
     *
     * @return participants
     */
    public List<Participant> getParticipants() {
        return (List<Participant>) participants;
    }

    /**
     * Remove participant from event
     * @param participant Participant to be removed.
     */
    public void removeParticipant(Participant participant) {
        participants.remove(participant);
    }
    /**
     * Setter method.
     *
     * @param participants .
     */
    public void setParticipants(Collection<Participant> participants) {
        this.participants = participants;
        updateLastActivity();
    }

    /**
     * Adds a participant to the event
     *
     * @param name name of the Participant to add
     * @return     the participant that was added
     */
    public Participant addParticipant(String name) {
        Participant participant = new Participant(name, this);
        this.participants.add(participant);
        updateLastActivity();
        return participant;
    }

    /**
     * Getter method.
     *
     * @return .
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Setter method
     *
     * @param eventName .
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
        updateLastActivity();
    }

    /**
     * Getter method.
     *
     * @return .
     */
    public LocalDate getEventCreationDate() {
        return eventCreationDate;
    }

    /**
     * Setter for eventCreationDate
     *
     * @param eventCreationDate the date to set
     */
    public void setEventCreationDate(LocalDate eventCreationDate) {
        this.eventCreationDate = eventCreationDate;
        updateLastActivity();
    }

    /**
     * Getter method.
     *
     * @return .
     */
    public String getInviteCode() {
        return inviteCode;
    }

    /**
     * Setter method.
     *
     * @param inviteCode .
     */
    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
        updateLastActivity();
    }

    /**
     * Getter method.
     *
     * @return .
     */
    public Collection<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Setter method.
     *
     * @param transactions .
     */
    public void setTransactions(Collection<Transaction> transactions) {
        this.transactions = transactions;
        updateLastActivity();
    }

    /**
     * Adds an transaction to the event.
     * Checks if the tag is already in the list of tags.
     * If it lists a tag with the same name but diff colour,
     * it changes the colour.
     *
     * @param e transaction
     */
    public void addTransaction(Transaction e) {
        transactions.add(e);
        Tag transactionTag = e.getTag();
        for (Tag tag : tags) {
            if (tag.equals(transactionTag)) {
                return;
            }
            if (tag.nameEquals(transactionTag)) {
                e.setTag(tag);
                return;
            }
        }

        tags.add(transactionTag);
    }

    /**
     * Register a transaction with an event
     *
     * @param payer       the Participant that paid
     * @param transactionName the name of the Transaction to be registered
     * @param price       the price of the Transaction
     * @param participants the participants of the Transaction
     * @param tag         the tag
     * @return            the transaction registered
     */
    public Transaction registerTransaction(Participant payer,
                                           String transactionName,
                                           int price,
                                           List<Participant> participants,
                                           Tag tag) {
        Transaction e = new Transaction(payer, transactionName, price,
                participants, this, tag);
        transactions.add(e);
        updateLastActivity();
        return e;
    }

    /**
     * Getter for the last activity on an event
     *
     * @return lastActivity
     */
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    /**
     * Setter for lastActivity
     *
     * @param lastActivity the LocalDateTime to set it to
     */
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
        updateLastActivity();
    }

    /**
     * Updates the last time of activity to now
     */
    public void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }

    /**
     * Gets the lastActivity as a String of the format dd/MM/yy hh:mm
     *
     * @return lastActivity as a String
     */
    public String getStringOfLastActivity() {
        return lastActivity.getDayOfMonth() + "/" +
                lastActivity.getMonthValue() + '/' +
                lastActivity.getYear() + ' ' +
                lastActivity.getHour() + ':' +
                lastActivity.getMinute();
    }

    /**
     * Equals method.
     *
     * @param o Event to test equality on.
     * @return True or false depending on equality.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(eventName, event.eventName)
                && Objects.equals(eventCreationDate, event.eventCreationDate)
                && Objects.equals(inviteCode, event.inviteCode)
                && Objects.equals(transactions, event.transactions)
                && Objects.equals(lastActivity, event.lastActivity)
                && Objects.equals(tags, event.tags)
                && Objects.equals(id, event.id);
    }

    /**
     * Hash code method.
     *
     * @return .
     */
    @Override
    public int hashCode() {
        return Objects.hash(eventName, eventCreationDate, inviteCode,
                transactions, lastActivity, id);
    }

    /**
     * Setter for id
     *
     * @param id the id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for id
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * toString method
     *
     * @return String representation of the Event
     */
    public String toString() {
        return eventName;
    }

    /**
     * Deletes a transaction from the event
     * @param transaction transaction to be deleted
     * @return  true if the transaction was deleted, false if it was not found
     */
    public boolean deleteTransaction(Transaction transaction) {
        return transactions.remove(transaction);
    }
}