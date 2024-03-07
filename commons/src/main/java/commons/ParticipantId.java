package commons;

import java.io.Serializable;
import java.util.Objects;

public class ParticipantId implements Serializable {
    private Long id;
    private Long event;

    /**
     * Parameterless constructor
     */
    public ParticipantId() {}

    /**
     * Constructor
     * @param id the id of the Participant
     * @param event the id of the Event
     */
    public ParticipantId(Long id, Long event) {
        this.id = id;
        this.event = event;
    }

    /**
     * Getter for id
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Getter for event
     * @return the event
     */
    public Long getEvent() {
        return event;
    }

    /**
     * Setter for id
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Setter for event
     * @param event the new event id
     */
    public void setEvent(Long event) {
        this.event = event;
    }

    /**
     * Equality check between a ParticipantId and an object
     * @param o the object to compare to
     * @return the result of the check as a boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantId that = (ParticipantId) o;
        return Objects.equals(id, that.id) && Objects.equals(event, that.event);
    }

    /**
     * Generates a hashCode for the ParticipantId
     * @return the hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, event);
    }
}
