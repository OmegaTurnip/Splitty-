package commons;

import java.util.Objects;

public class Participant {

    private String name;
    private Event event;

    /**
     * Constructor.
     *
     * @param name Name of the participant.
     */
    public Participant(String name, Event event) {
        this.name = name;
        this.event = event;
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
     * Setter method.
     *
     * @param name .
     */
    public void setName(String name) {
        this.name = name;
        event.updateLastActivity();
    }

    /**
     * Getter for event
     * @return event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Equals method.
     *
     * @param o Participant to test equality on.
     * @return True or false depending on equality.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Objects.equals(name, that.name) && Objects.equals(event, that.event);
    }

    /**
     * Hash code method.
     *
     * @return .
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, event);
    }
}


