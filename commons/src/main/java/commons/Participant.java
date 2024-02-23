package commons;

import java.util.Objects;

public class Participant {

    private String name;

    /**
     * Constructor.
     *
     * @param name Name of the participant.
     */
    public Participant(String name) {
        this.name = name;
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
        return Objects.equals(name, that.name);
    }

    /**
     * Hash code method.
     *
     * @return .
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}


