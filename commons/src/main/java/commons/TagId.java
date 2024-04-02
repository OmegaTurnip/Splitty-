package commons;

import java.io.Serializable;
import java.util.Objects;

public class TagId implements Serializable {
    private Long tagId;
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
        this.tagId = id;
        this.event = event;
    }

    /**
     * Getter for id
     * @return the id
     */
    public Long getTagId() {
        return tagId;
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
    public void setTagId(Long id) {
        this.tagId = id;
    }

    /**
     * Setter for event
     * @param event the new event id
     */
    public void setEvent(Long event) {
        this.event = event;
    }

    /**
     * Equality check between a TagId and an object
     * @param o the object to compare to
     * @return the result of the check as a boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagId that = (TagId) o;
        return Objects.equals(tagId, that.tagId) && Objects.equals(event, that.event);
    }

    @Override
    public String toString() {
        return "TagId{" +
                "id=" + tagId +
                ", event=" + event +
                '}';
    }

    /**
     * Generates a hashCode for the TagId
     * @return the hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(tagId, event);
    }
}


