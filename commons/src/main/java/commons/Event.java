package commons;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;

public class Event {

    private String eventName;
    private final Date eventCreationDate;
    private String inviteCode;
    private Collection<Expense> expenses;
    private Collection<Participant> participants;

    /**
     * Constructor for an event. The attributes should be editable.
     * @param eventName The name of the event.
     * @param eventCreationDate The creation date of the event.
     * @param expenses The list of expenses made during the event.
     * @param participants The participants of the event. Note that this
     *                     attribute is needed since not all participants
     *                     may owe a debt/have paid an expense.
     */
    public Event(String eventName, Date eventCreationDate,
                 Collection<Expense> expenses,
                 Collection<Participant> participants) {
        this.eventName = eventName;
        this.eventCreationDate = eventCreationDate;
        this.inviteCode = generateInviteCode();
        this.expenses = expenses;
        this.participants = participants;
    }

    /**
     * Method for generating a random invite code upon calling.
     * @return Random invite code.
     */
    private String generateInviteCode() {
        return "ffff"; //We are going to fix this method to
                       // generate a proper, random invite code.
    }

    /**
     * Getter method.
     * @return .
     */
    public Collection<Participant> getParticipants() {
        return participants;
    }

    /**
     * Setter method.
     * @param participants .
     */
    public void setParticipants(Collection<Participant> participants) {
        this.participants = participants;
    }

    /**
     * Getter method.
     * @return .
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Setter method
     * @param eventName .
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Getter method.
     * @return .
     */
    public Date getEventCreationDate() {
        return eventCreationDate;
    }

    /**
     * Getter method.
     * @return .
     */
    public String getInviteCode() {
        return inviteCode;
    }

    /**
     * Setter method.
     * @param inviteCode .
     */
    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    /**
     * Getter method.
     * @return .
     */
    public Collection<Expense> getExpenses() {
        return expenses;
    }

    /**
     * Setter method.
     * @param expenses .
     */
    public void setExpenses(Collection<Expense> expenses) {
        this.expenses = expenses;
    }

    /**
     * Equals method.
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
                && Objects.equals(expenses, event.expenses);
    }

    /**
     * Hash code method.
     * @return .
     */
    @Override
    public int hashCode() {
        return Objects.hash(eventName, eventCreationDate, inviteCode, expenses);
    }
}
