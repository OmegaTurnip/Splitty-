package commons;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Debt {

    @Id
    @GeneratedValue
    private Long id;
    private int cent;
    @ManyToOne
    private Participant debtor;

    /**
     * Constructor without parameters
     */
    public Debt() {}

    /**
     * Constructor
     * @param debtor the Participant that owes the debt
     * @param cent the amount of the Debt
     */
    public Debt(Participant debtor, int cent) {
        this.cent = cent;
        this.debtor = debtor;
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

    /**
     * Equality check between a Debt and an Object o
     * @param o the Object to compare to
     * @return the result of the equality check as a boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Debt debt = (Debt) o;
        return cent == debt.cent && Objects.equals(id, debt.id)
                && Objects.equals(debtor, debt.debtor);
    }

    /**
     * Generates a hashCode for the Debt
     * @return the hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, cent, debtor);
    }
}
