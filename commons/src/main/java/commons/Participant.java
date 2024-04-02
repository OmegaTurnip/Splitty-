package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Objects;
@Entity
@IdClass(ParticipantId.class)
public class Participant {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long participantId;
    @Id
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference
    private Event event;
    private String name;



    /**
     * Constructor.
     *
     * @param name Name of the participant.
     * @param event Event the participant belongs to.
     */
    Participant(String name, Event event) {
        this.name = name;
        this.event = event;
    }

    /**
     * Constructor without parameters
     */
    public Participant() {}

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
    }

    /**
     * Getter for event
     * @return event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Sets the event for a participant
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
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
        return Objects.equals(event, that.event)
                && Objects.equals(participantId, that.participantId);
    }

    /**
     * Hash code method.
     *
     * @return .
     */
    @Override
    public int hashCode() {
        return Objects.hash(event, participantId);
    }

    /**
     * Generates a {@code String} representing {@code this}.
     *
     * @return  A {@code String} representing {@code this}.
     */
    @Override
    public String toString() {
        return "Participant { '" + name +  "' (id: " + participantId +
                ") in the event '" + event.getEventName() + "' }";
    }

    /**
     * Setter for id
     * @param id the id
     */
    public void setParticipantId(Long id) {
        this.participantId = id;
    }

    /**
     * Getter for id
     * @return the id
     */
    public Long getParticipantId() {
        return participantId;
    }

}
