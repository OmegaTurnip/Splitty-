package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Objects;
@Entity
@IdClass(ParticipantId.class)
public class Participant {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
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
     * Setter method. Also updates last activity in the corresponding Event.
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
        return Objects.equals(event, that.event)
                && Objects.equals(id, that.id);
    }

    /**
     * Hash code method.
     *
     * @return .
     */
    @Override
    public int hashCode() {
        return Objects.hash(event, id);
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
}