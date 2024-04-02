package commons;


import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "event")
    private Collection<Transaction> transactions;
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "event")
    private Collection<Participant> participants;
    private LocalDateTime lastActivity;
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "event")
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
        addTag(new Tag("Food", "#A1D9D8"));
        addTag(new Tag("Entrance fees", "#9FB433"));
        addTag(new Tag("Travel", "#000000"));
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

//    /**
//     * Calculates the total sum of all the transactions in the event in euro
//     * cents.
//     *
//     * @return The total sum.
//     */
//    public long totalSumOfExpenses() {
//        throw new NotImplementedException("conversion to euros cents needs " +
//                "to be implemented");
//        long result = 0;
//        for (Transaction transaction : transactions) {
//            result += transaction.getAmount().getAmount();
//        }
//        return result;
//    }

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
     * Setter method (for Jackson).
     *
     * @param participants .
     */
    public void setParticipants(Collection<Participant> participants) {
        this.participants = participants;
    }

    /**
     * Edit participants (with updateLastActivity)
     * @param participants The participants
     */
    public void editParticipants(Collection<Participant> participants) {
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
     * Setter method (for Jackson).
     *
     * @param eventName .
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Edit event name (with updateLastActivity)
     * @param eventName The name of the event
     */
    public void editEventName(String eventName) {
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
     * Setter for eventCreationDate (for Jackson).
     *
     * @param eventCreationDate the date to set
     */
    public void setEventCreationDate(LocalDate eventCreationDate) {
        this.eventCreationDate = eventCreationDate;
    }

    /**
     * Edit event creation date (with updateLastActivity)
     * @param eventCreationDate The date of the event
     */
    public void editEventCreationDate(LocalDate eventCreationDate) {
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
     * Setter method (for Jackson).
     *
     * @param inviteCode .
     */
    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    /**
     * Edit invite code (with updateLastActivity)
     * @param inviteCode The invite code
     */
    public void editInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
        updateLastActivity();
    }

    /**
     * Getter method.
     *
     * @return .
     */
    public List<Transaction> getTransactions() {
        return (List<Transaction>) transactions;
    }

    /**
     * Setter method (for Jackson).
     *
     * @param transactions .
     */
    public void setTransactions(Collection<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * Edit transactions (with updateLastActivity)
     * @param transactions The transactions
     */
    public void editTransactions(Collection<Transaction> transactions) {
        this.transactions = transactions;
        updateLastActivity();
    }

    /**
     * Register a transaction carrying a debt with an event.
     *
     * @param   creditor
     *          The {@link Participant} that paid.
     * @param   name
     *          The name of the {@link Transaction} to be registered.
     * @param   price
     *          The money spend.
     * @param   participants
     *          The {@code participant}s over which the {@code Transaction}
     *          should be divided.
     * @param   tag
     *          The {@link Tag}.
     *
     * @return  The registered {@code Transaction}.
     */
    public Transaction registerDebt(Participant creditor, String name,
                                    Money price, List<Participant> participants,
                                    Tag tag) {
        Transaction t =
                Transaction.createDebt(
                        creditor, name, price, participants, this, tag
                );
        transactions.add(t);
        updateLastActivity();
        return t;
    }

    /**
     * Register a transaction carrying a payoff with an event.
     *
     * @param   payer
     *          The {@link Participant} that paid.
     * @param   name
     *          The name of the {@link Transaction} to be registered.
     * @param   amount
     *          The money paid.
     * @param   receiver
     *          The {@code participant} receiving the money.
     * @param   tag
     *          The {@link Tag}.
     *
     * @return  The registered {@code Transaction}.
     */
    public Transaction registerPayoff(Participant payer, String name,
                                      Money amount, Participant receiver,
                                      Tag tag) {
        Transaction t =
                Transaction.createPayoff(
                        payer, name, amount, receiver, this, tag
                );
        transactions.add(t);
        updateLastActivity();
        return t;
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
     * Setter for lastActivity (for Jackson)
     *
     * @param lastActivity the LocalDateTime to set it to
     */
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
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
    public String stringOfLastActivity() {
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
        return Objects.equals(id, event.id);
    }

    /**
     * Hash code method.
     *
     * @return .
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
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

    /**
     * Gets the participant from participants that has the same id, used for
     * making sure the same participant instance is used across an event
     * @param id the id
     * @return the participant of the same id
     */
    public Participant getParticipantById(Long id) {
        for (Participant participant : participants) {
            if (id != null && id.equals(participant.getParticipantId()))
                return participant;
        }
        return null;
    }

    /**
     * Get the Tag by its id
     * @param id Tag id
     * @return the tag
     */
    public Tag getTagbyId(Long id) {
        for (Tag tag : tags) {
            if (id != null && id.equals(tag.getTagId())) {
                return tag;
            }
        }
        return null;
    }
}