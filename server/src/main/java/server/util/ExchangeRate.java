package server.util;

import java.io.*;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Objects;
import java.util.Scanner;

public class ExchangeRate {

    private static final File DEFAULT_DIR = new File("rates");

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
    private double rate;

    /**
     * Creates an {@code ExchangeRate} object.
     *
     * @param   date
     *          The date on which the exchange rate was retrieved.
     * @param   from
     *          The base currency in the exchange rate as a {@link Currency}
     *          object.
     * @param   to
     *          The converted currency in the exchange rate as a {@link
     *          Currency} object.
     * @param   rate
     *          The actual exchange rate.
     */
    public ExchangeRate(LocalDate date, Currency from, Currency to,
                        double rate) {
        this.date = date;
        this.from = from;
        this.to   = to;
        this.rate = rate;
    }

    /**
     * Saves this {@code ExchangeRate} object to a file in the directory
     * specified by {@link ExchangeRate#DEFAULT_DIR}.
     *
     * @throws  IOException
     *          If an IO error occurs.
     */
    public void save() throws IOException {
        save(DEFAULT_DIR);
    }

    /**
     * Saves this {@code ExchangeRate} object to a file in the directory {@code
     * directory}.
     *
     * @param   directory
     *          The directory in which the file should be saved.
     *
     * @throws  IOException
     *          If an IO error occurs.
     */
    public void save(File directory) throws IOException {
        try (FileWriter writer = new FileWriter(
                new File(directory, generateFileName()))) {
            writer.write(Double.toString(rate));
        }
    }

    /**
     * Reads the rate from the corresponding file in the directory {@code
     * directory}, updating the {@link ExchangeRate#rate} attribute. Returns
     * {@code true} if the rate was successfully read and {@code false}
     * otherwise.
     *
     * @return  Whether the rate was successfully read.
     */
    public boolean read() {
        return read(DEFAULT_DIR);
    }

    /**
     * Reads the rate from the corresponding file in the directory specified by
     * {@link ExchangeRate#DEFAULT_DIR}, updating the {@link ExchangeRate#rate}
     * attribute. Returns {@code true} if the rate was successfully read and
     * {@code false} otherwise.
     *
     * @param   directory
     *          The directory from which the file should be read.
     *
     * @return  Whether the rate was successfully read.
     */
    public boolean read(File directory) {
        ExchangeRate temp = read(date, from, to, directory);
        if (temp == null)
            return false;
        rate = temp.rate;
        return true;
    }

    /**
     * Loads an {@code ExchangeRate} object from the corresponding file in the
     * directory specified by {@link ExchangeRate#DEFAULT_DIR}. Returns
     * {@code null} if for any reason the {@code ExchangeRate} couldn't be
     * fetched from that file.
     *
     * @param   date
     *          The date on which the exchange rate was originally retrieved.
     * @param   from
     *          The base currency in the exchange rate as a {@link Currency}
     *          object.
     * @param   to
     *          The converted currency in the exchange rate as a {@code
     *          Currency} object.
     *
     * @return  A {@code ExchangeRate} object or {@code null}.
     */
    public static ExchangeRate read(LocalDate date, Currency from,
                                    Currency to) {
        return read(date, from, to, DEFAULT_DIR);
    }

    /**
     * Loads an {@code ExchangeRate} object from the corresponding file in the
     * directory {@code directory}. Returns {@code null} if for any reason the
     * {@code ExchangeRate} couldn't be fetched from that file.
     *
     * @param   date
     *          The date on which the exchange rate was originally retrieved.
     * @param   from
     *          The base currency in the exchange rate as a {@link Currency}
     *          object.
     * @param   to
     *          The converted currency in the exchange rate as a {@code
     *          Currency} object.
     * @param   directory
     *          The directory from which the file should be read.
     *
     * @return  A {@code ExchangeRate} object or {@code null}.
     */
    public static ExchangeRate read(LocalDate date, Currency from, Currency to,
                                    File directory) {
        try (Scanner fileReader = new Scanner(
                new File(directory, generateFileName(date, to, from)))) {
            return new ExchangeRate(date, to, from, fileReader.nextDouble());
        } catch (Exception e) {
            return null;
        }
    }

    private String generateFileName() {
        return generateFileName(date, from, to);
    }

    private static String generateFileName(LocalDate date, Currency from,
                                           Currency to) {
        return date + "." + from + "." + to + ".txt";
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
        ExchangeRate that = (ExchangeRate) other;
        return Double.compare(rate, that.rate) == 0
                && Objects.equals(date, that.date)
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
        return Objects.hash(date, from, to, rate);
    }

    /**
     * Generates a {@code String} representing {@code this}.
     *
     * @return  A {@code String} representing {@code this}.
     */
    @Override
    public String toString() {
        return "ExchangeRate on " + date + " from " + from + " to " + to + " is"
                + rate;
    }
}
