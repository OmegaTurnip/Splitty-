package commons;

import java.io.Serializable;
import java.util.Objects;

public class TransactionId implements Serializable {
    private Long id;
    private Long event;

    /**
     * Parameterless constructor
     */
    public TransactionId() {}

    /**
     * Constructor
     * @param id the id of the Transaction
     * @param event the id of the Event
     */
    public TransactionId(Long id, Long event) {
        this.id = id;
        this.event = event;
    }

    /**
     * Getter for id
     * @return the id
     */
    public Long getTransactionId() {
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
    public void setTransactionId(Long id) {
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
     * Equality check between a TransactionId and an object
     * @param o the object to compare to
     * @return the result of the check as a boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionId that = (TransactionId) o;
        return Objects.equals(id, that.id) && Objects.equals(event, that.event);
    }

    /**
     * Generates a hashCode for the TransactionId
     * @return the hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, event);
    }
}
