package commons;

import java.io.Serial;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

public class Money implements java.io.Serializable, Comparable<Money> {

    @Serial
    private static final long serialVersionUID = -5942679521526989754L;

    private BigDecimal amount;
    private final Currency currency;

    /**
     * Constructs a new {@code Money} object.
     *
     * @param   amount
     *          The value of the money object in the form of a {@link
     *          BigDecimal} instance.
     * @param   currency
     *          The currency in which the original amount was specified in the
     *          form of a {@link Currency} instance.
     */
    public Money(BigDecimal amount, Currency currency) {
        if (amount == null || currency == null)
            throw new NullPointerException("argument is null");

        this.currency = currency;
        setAmount(amount);
    }

    /**
     * Gets the value of the money object in the form of a {@link BigDecimal}
     * instance.
     *
     * @return  The value of the money object in the form of a {@link
     *          BigDecimal} instance.
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the value of the money object in the form of a {@link BigDecimal}
     * instance.
     *
     * @param   amount
     *          The new value of the money object in the form of a {@link
     *          BigDecimal} instance.
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount.setScale(currency.getDefaultFractionDigits(),
                                        RoundingMode.HALF_UP);
    }

    /**
     * Gets the currency in which the original amount was specified in the form
     * of a {@link Currency} instance.
     *
     * @return  The currency in which the original amount was specified.
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Formats this {@code Money} object in the specified locale.
     *
     * @param   locale
     *          The locale to use when formatting.
     *
     * @return  The formatted representation of {@code this}.
     */
    public String format(Locale locale) {
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        format.setCurrency(currency);
        format.setMaximumFractionDigits(currency.getDefaultFractionDigits());
        format.setMinimumFractionDigits(currency.getDefaultFractionDigits());
        return format.format(amount);
    }

    /**
     * Compares {@code this} to {@code other}. Throws {@link
     * IllegalArgumentException} if {@link Money#currency this.currency}
     * {@code != other.currency}.
     *
     * @param   other
     *          The object to be compared.
     *
     * @return  An {@code int} representing the natural ordering.
     */
    @Override
    public int compareTo(Money other) {
        if (!currency.equals(other.currency))
            throw new IllegalArgumentException("incompatible currencies");
        return amount.compareTo(other.amount);
    }

    /**
     * Checks if {@code this} is equal to {@code other}.
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
        Money money = (Money) other;
        return Objects.equals(amount, money.amount)
                && Objects.equals(currency, money.currency);
    }

    /**
     * Generates a hash code corresponding to {@code this}.
     *
     * @return  A hash value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(currency, amount);
    }

    /**
     * Generates a {@code String} representing {@code this}.
     *
     * @return  A {@code String} representing {@code this}.
     */
    @Override
    public String toString() {
        return "Money { " + amount + " " + currency + " }";
    }

    /**
     * Checks whether the {@code String currency} is a valid
     * <a href="https://en.wikipedia.org/wiki/ISO_4217">ISO 4217</a> code.
     *
     * @param   currencyCode
     *          The {@code String} to be checked.
     *
     * @return  Whether the {@code String} is a valid ISO 4217 code.
     */
    public static boolean isValidCurrencyCode(String currencyCode) {
        try {
            Currency.getInstance(currencyCode);
            return true;
        } catch (NullPointerException | IllegalArgumentException e) {
            return false;
        }
    }
}
