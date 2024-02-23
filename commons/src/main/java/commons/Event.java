package commons;

import java.util.*;

public class Event {

    private String eventName;
    private final Date eventCreationDate;
    private String inviteCode;
    private Collection<Expense> expenses;
    private Collection<Participant> participants;

    private List<String> tags;

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
        this.tags = new ArrayList<>();
        basicTags();
    }

    /**
     * method for adding the three standard tags
     */
    public void basicTags(){
        addTag("food");
        addTag("entrance fees");
        addTag("Travel");
    }

    /**
     * Method for generating a random invite code upon calling.
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
     * getter method
     * @return list of tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * adds a tag
     * @param tag to be added
     */
    public void addTag(String tag){
        tags.add(tag);
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

