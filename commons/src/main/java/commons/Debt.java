package commons;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * This class is called {@code Debt} and not {@code Transaction} to prevent
 * name collisions with the {@link Transaction} class.
 * A payoff should be represented as a debt in the reverse direction.
 *
 * @param   from
 *          The {@code Participant} owing the debt.
 * @param   to
 *          The {@code Participant} that should receive the payment.
 * @param   amount
 *          The debt.
 */
public record Debt(Participant from, Participant to, Money amount)
        implements Serializable {

    @Serial
    private static final long serialVersionUID = -4803975468020501568L;

    /**
     * Creates an object storing the debt between two {@link Participant}s.
     */
    public Debt {
        Objects.requireNonNull(from, "from is null");
        Objects.requireNonNull(to, "to is null");
        Objects.requireNonNull(amount, "amount is null");

        if (Objects.equals(from, to))
            throw new IllegalArgumentException(
                    "someone cannot owe themselves a debt");

        if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Debt is not positive");
    }

    /**
     * Generates a {@code String} representing {@code this}.
     *
     * @return  A {@code String} representing {@code this}.
     */
    @Override
    public String toString() {
        return "Debt { from " + from + " to " + to + " is " + amount + " }";
    }
}