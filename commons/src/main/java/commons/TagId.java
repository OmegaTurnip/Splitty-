package commons;

import java.io.Serializable;

public class TagId implements Serializable {
    private Long id;
    private Long event;

    /**
     * Parameterless constructor
     */
    public TagId() {}

    /**
     * Constructor
     * @param id the id of the Tag
     * @param event the id of the Event
     */
    public TagId(Long id, Long event) {
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
}
