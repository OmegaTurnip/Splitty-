package server.financial;

import commons.Money;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Stores exchange rates. At most two exchange rates (to and from) between two
 * currencies should exist for any day.
 */
public class ExchangeRate {

    /**
     * The date on which the exchange rate was polled.
     */
    private final LocalDate date;

    /**
     * The base currency in the exchange rate ("from") as a {@link Currency}
     * object.
     */
    private final Currency from;

    /**
     * The converted currency in the exchange rate ("to") as a {@link Currency}
     * object.
     */
    private final Currency to;

    /**
     * The actual exchange rate. Monetary values shouldn't be stored as {@code
     * double}s but there is no choice for exchange rates. When converting
     * currencies always go back to datatypes with a non-dynamic range and in
     * the same base as that currency.
     */
    private final double rate;

    /**
     * Shadow of {@link ExchangeRate#rate} to prevent unnecessary allocations.
     */
    private final transient BigDecimal rateBD;

    /**
     * Creates an {@code ExchangeRate} object.
     *
     * @param   date
     *          The date on which the exchange rate was retrieved.
     * @param   from
     *          The base currency in the exchange rate as a {@link Currency}
     *          object.
     * @param   to
     *          The converted currency in the exchange rate as a {@code
     *          Currency} object.
     * @param   rate
     *          The actual exchange rate.
     */
    public ExchangeRate(LocalDate date, Currency from, Currency to,
                        double rate) {
        Objects.requireNonNull(date, "date is null");
        Objects.requireNonNull(from, "from is null");
        Objects.requireNonNull(to, "to is null");

        if (rate < 0)
            throw new IllegalArgumentException("rate is negative");

        // because this will break an assumption in the conversion function...
        if (Objects.equals(from, to) && rate != 1d)
            throw new IllegalArgumentException("Conversion to the same " +
                    "currency doesn't have a rate of 1");

        this.date = date;
        this.from = from;
        this.to   = to;
        this.rate = rate;
        this.rateBD = new BigDecimal(rate);
    }

    /**
     * Gets the date on which the exchange rate was retrieved.
     *
     * @return  The date on which the exchange rate was retrieved.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Gets the base currency in the exchange rate as a {@link Currency} object.
     *
     * @return  The base currency in the exchange rate as a {@code Currency}
     *          object.
     */
    public Currency getFrom() {
        return from;
    }

    /**
     * Gets the converted currency in the exchange rate as a {@link Currency}
     * object.
     *
     * @return  The converted currency in the exchange rate as a {@code
     *          Currency} object.
     */
    public Currency getTo() {
        return to;
    }

    /**
     * Gets the actual exchange rate.
     *
     * @return  The actual exchange rate.
     */
    public double getRate() {
        return rate;
    }

    /**
     * Converts an {@link Money} object of currency {@link ExchangeRate#from} to
     * an {@code Money} object of currency {@link ExchangeRate#to} using the
     * given {@link ExchangeRate#rate exchange rate}, returning the result.
     *
     * @param   amount
     *          The {@code Money} object to be converted.
     *
     * @return  The {@code Money} object resulting from the conversion.
     */
    public Money convert(Money amount) {
        Objects.requireNonNull(amount, "amount is null");

        if (!amount.getCurrency().equals(from))
            throw new IllegalArgumentException("the currency of amount " +
                    "doesn't match with the base currency!");

        if (from.equals(to))
            return amount;

        return new Money(amount.getAmount().multiply(rateBD), to);
    }

    /**
     * Checks if {@code this} is equal to {@code other}. The rate is not taken
     * into account.
     *
     * @param   other
     *          The object to check.
     *
     * @return  Whether {@code this} and {@code other} are equal.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ExchangeRate that = (ExchangeRate) other;
        return Objects.equals(date, that.date)
                && Objects.equals(from, that.from)
                && Objects.equals(to, that.to);
    }

    /**
     * Generates a hash code corresponding to {@code this}.
     *
     * @return  A hash value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(date, from, to);
    }

    /**
     * Generates a {@code String} representing {@code this}.
     *
     * @return  A {@code String} representing {@code this}.
     */
    @Override
    public String toString() {
        return "ExchangeRate on " + date + " from " + from + " to " + to +
                " is " + rate;
    }
}
