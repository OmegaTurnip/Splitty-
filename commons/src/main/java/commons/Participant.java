package commons;

import jakarta.persistence.*;

import java.util.Objects;
@Entity
public class Participant {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long participantId;
    private String name;
    private String email;
    private String iban;
    private String bic;



    /**
     * Constructor.
     *
     * @param name Name of the participant.
     * @param event Event the participant belongs to.
     */
    Participant(String name, Event event) {
        this.name = name;
    }

    /**
     * Constructor.
     * @param event the event
     * @param name the name
     * @param email the email
     * @param iban the iban
     * @param bic the bic
     */
    Participant(Event event, String name,
                String email, String iban, String bic) {
        this.name = name;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
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
        return Objects.equals(participantId, that.participantId);
    }

    /**
     * Hash code method.
     *
     * @return .
     */
    @Override
    public int hashCode() {
        return Objects.hash(participantId);
    }

    /**
     * Generates a {@code String} representing {@code this}.
     *
     * @return  A {@code String} representing {@code this}.
     */
    @Override
    public String toString() {
        return "Participant { '" + name +  "' (id: " + participantId +
                ") }";
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

    /**
     * Setter for email
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Setter for iban
     * @param iban the iban
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * Getter for email
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Getter for iban
     * @return the iban
     */
    public String getIban() {
        return iban;
    }

    /**
     * Getter for bic
     * @return the bic
     */
    public String getBic() {
        return bic;
    }

    /**
     * Setter for bic
     * @param bic the bic
     */
    public void setBic(String bic) {
        this.bic = bic;
    }
}
