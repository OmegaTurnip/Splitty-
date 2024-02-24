package commons;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class Event {

    private String eventName;
    private LocalDate eventCreationDate;
    private String inviteCode;
    private Collection<Expense> expenses;
    private Collection<Participant> participants;
    private LocalDateTime lastActivity;

    /**
     * Constructor for an event. The attributes should be editable.
     *
     * @param eventName         The name of the event.
     */
    public Event(String eventName) {
        this.eventName = eventName;
        this.eventCreationDate = LocalDate.now();
        this.inviteCode = generateInviteCode();
        this.expenses = new ArrayList<>();
        this.participants = new ArrayList<>();
        updateLastActivity();
    }

    /**
     * Method for generating a random invite code upon calling.
     *
     * @return Random invite code.
     */
    public static String generateInviteCode() {
        UUID randomCode = UUID.randomUUID();
        StringBuilder inBetween = new StringBuilder(randomCode.toString()
                .replaceAll("_", ""));
        inBetween.delete(8, 32);
        return inBetween.toString();
    }

    /**
     * Calculates the total sum of all the expenses in the event.
     *
     * @return The total sum.
     */
    public int totalSumOfExpenses() {
        int result = 0;
        for (Expense expense : expenses) {
            result += expense.getPrice();
        }
        return result;
    }

    /**
     * Getter method.
     *
     * @return .
     */
    public Collection<Participant> getParticipants() {
        return participants;
    }

    /**
     * Setter method.
     *
     * @param participants .
     */
    public void setParticipants(Collection<Participant> participants) {
        this.participants = participants;

    }

    public void addParticipant(Participant participant) {
        this.participants.add(participant);
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
    }

    /**
     * Getter method.
     *
     * @return .
     */
    public LocalDate getEventCreationDate() {
        return eventCreationDate;
    }

    public void setEventCreationDate(LocalDate eventCreationDate) {
        this.eventCreationDate = eventCreationDate;
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
    }

    /**
     * Getter method.
     *
     * @return .
     */
    public Collection<Expense> getExpenses() {
        return expenses;
    }

    /**
     * Setter method.
     *
     * @param expenses .
     */
    public void setExpenses(Collection<Expense> expenses) {
        this.expenses = expenses;
    }

    /**
     * Getter for the last activity on an event
     * @return lastActivity
     */
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    /**
     * Setter for lastActivity
     * @param lastActivity the LocaDateTime to set it to
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
                && Objects.equals(expenses, event.expenses)
                && Objects.equals(lastActivity, event.lastActivity);
    }

    /**
     * Hash code method.
     *
     * @return .
     */
    @Override
    public int hashCode() {
        return Objects.hash(eventName, eventCreationDate, inviteCode, expenses, lastActivity);
    }
}

