package server.util;

import commons.Money;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExchangeRate {

    /**
     * A set containing all known {@code ExchangeRates}.
     */
    private static final HashSet<ExchangeRate> exchangeRates = new HashSet<>();
    private static final HashSet<Currency> knownCurrencies = new HashSet<>();

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
     * Shadow of {@link ExchangeRate#rate} to prevent unnecessary allocations.
     */
    private transient BigDecimal rateBD;

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

        if (date == null || from == null || to == null)
            throw new NullPointerException("Argument is null");

        // because this will break an assumption in the conversion function...
        if (Objects.equals(from, to) && rate != 1.0)
            throw new IllegalArgumentException("Conversion to the same " +
                    "currency doesn't have a rate of 1.0");

        knownCurrencies.add(from);
        knownCurrencies.add(to);

        this.date = date;
        this.from = from;
        this.to   = to;
        if (exchangeRates.contains(this))
            throw new IllegalArgumentException("ExchangeRate already exists");
        setRate(rate);
        exchangeRates.add(this);
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

    private void setRate(double rate) {
        this.rate = rate;
        this.rateBD = new BigDecimal(rate);
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
        if (amount == null)
            throw new NullPointerException("amount is null!");

        if (!amount.getCurrency().equals(from))
            throw new IllegalArgumentException("the currency of amount " +
                    "doesn't match with the base currency!");

        if (from.equals(to))
            return amount;

        return new Money(amount.getAmount().multiply(rateBD), to);
    }


    /**
     * Gets all loaded {@code ExchangeRate}s.
     *
     * @return  A set containing all loaded {@code ExchangeRate}s.
     */
    public static Set<ExchangeRate> get() {
        return Set.copyOf(exchangeRates);
    }

    /**
     * Gets all {@code ExchangeRate}s converting from a specific currency to
     * another currency. Only considers already loaded {@code ExchangeRate}s.
     *
     * @param   from
     *          The base currency in the exchange rate as a {@link Currency}
     *          object.
     * @param   to
     *          The converted currency in the exchange rate as a {@code
     *          Currency} object.
     *
     * @return  All {@code ExchangeRate}s converting from a specific currency to
      *         another currency.
     */
    public static Set<ExchangeRate> get(Currency from, Currency to) {
        return Set.copyOf(exchangeRates.stream()
                .filter(er -> Objects.equals(from, er.from)
                        && Objects.equals(to, er.to))
                .collect(Collectors.toSet()));
    }

    /**
     * Gets all {@code ExchangeRate}s converting from a specific currency. Only
     * considers already loaded {@code ExchangeRate}s.
     *
     * @param   from
     *          The base currency in the exchange rate as a {@link Currency}
     *          object.
     *
     * @return  The corresponding {@code ExchangeRate}s.
     */
    public static Set<ExchangeRate> get(Currency from) {
        return Set.copyOf(exchangeRates.stream()
                .filter(er -> Objects.equals(from, er.from))
                .collect(Collectors.toSet()));
    }

    /**
     * Gets all {@code ExchangeRate}s from the date {@code}. Only considers
     * already loaded {@code ExchangeRate}s.
     *
     * @param   date
     *          The date on which the exchange rate was retrieved.
     *
     * @return  The corresponding {@code ExchangeRate}s.
     */
    public static Set<ExchangeRate> get(LocalDate date) {
        return exchangeRates.stream()
                .filter(er -> Objects.equals(date, er.date))
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Gets the {@code ExchangeRate} specified by the parameters. Returns {@code
     * null} if no {@code ExchangeRate} was found. Only considers already loaded
     * {@code ExchangeRate}s.
     *
     * @param   date
     *          The date on which the exchange rate was retrieved.
     * @param   from
     *          The base currency in the exchange rate as a {@link Currency}
     *          object.
     * @param   to
     *          The converted currency in the exchange rate as a {@code
     *          Currency} object.
     *
     * @return  The corresponding {@code ExchangeRate} object or {@code null} if
     *          it is not found.
     */
    public static ExchangeRate get(LocalDate date, Currency from, Currency to) {
        return exchangeRates.stream()
                .filter(er -> Objects.equals(date, er.date)
                        && Objects.equals(from, er.from)
                        && Objects.equals(to, er.to))
                .findFirst().orElse(null);
    }

    /**
     * Gets the most recent {@code ExchangeRate} that qualifies the parameters.
     * Returns {@code null} if no {@code ExchangeRate} was found. Only considers
     * already loaded {@code ExchangeRate}s.
     *
     * @param   from
     *          The base currency in the exchange rate as a {@link Currency}
     *          object.
     * @param   to
     *          The converted currency in the exchange rate as a {@code
     *          Currency} object.
     *
     * @return  The corresponding {@code ExchangeRate} object or {@code null} if
     *          it is not found.
     */
    public static ExchangeRate getMostRecent(Currency from, Currency to) {
        return exchangeRates.stream()
                    .filter(er -> Objects.equals(from, er.from)
                        && Objects.equals(to, er.to))
                    .max(Comparator.comparing(o -> o.date))
                    .orElse(null);
    }

    private static final Pattern FILE_PATTERN =
            Pattern.compile("^(?<date>[0-9]{4}-(?>0[1-9]|1[012])-" +
                            "(?>0[1-9]|[12][0-9]|3[01]))\\." +
                            "(?<from>[A-Z]{3})\\.(?<to>[A-Z]{3})\\.txt$");

    /**
     * Loads all {@code ExchangeRate} objects in to memory from a specified
     * directory.
     *
     * @throws  IOException
     *          If an IO error occurred.
     */
    public static void loadAll() throws IOException {
        loadAll(DEFAULT_DIR);
    }

    /**
     * Loads all {@code ExchangeRate} objects in to memory from the directory
     * specified by {@link ExchangeRate#DEFAULT_DIR}.
     *
     * @param   directory
     *          The directory to search in for {@code ExchangeRate}s.
     *
     * @throws  IOException
     *          If an IO error occurred.
     */
    public static void loadAll(File directory) throws IOException {
        try (DirectoryStream<Path> stream =
                     Files.newDirectoryStream(directory.toPath())) {
            // this is a bad idea as this doesn't scale well, as the space
            // complexity is O(c²d), with c = amount of currencies and
            // d = amount of days, but then they should've let us use the
            // database (and for this small project it doesn't really matter
            // anyway).
            for (Path file : stream) {
                Matcher matcher = FILE_PATTERN.matcher(
                        file.getFileName().toString()
                );
                if (matcher.matches()) {
                    try {
                        read(
                                LocalDate.parse(matcher.group("date")),
                                Currency.getInstance(matcher.group("from")),
                                Currency.getInstance(matcher.group("to")),
                                directory
                        );
                    } catch (Exception e) {
                        // do nothing, as invalid files are invalid :)
                    }
                }
            }
        }
    }

    /**
     * Generates new {@code ExchangeRate} objects based on the new api data
     * (which should be from today) and saves them in the directory {@link
     * ExchangeRate#DEFAULT_DIR}. The rates should be specified in the form
     * <i>"1 base currency unit = x other currency units"</i>. If a currency was
     * removed from the api it will make up for this by using the last known
     * exchange rate for that currency (if possible).
     *
     * @param   base
     *          The base currency on which all values are based.
     * @param   rates
     *          The values of the currencies relative to 1 unit of the base
     *          currency.
     */
    public static void generateExchangeRates(Currency base,
                                             HashMap<Currency, Double> rates) {
        generateExchangeRates(base, rates, DEFAULT_DIR);
    }

    /**
     * Generates new {@code ExchangeRate} objects based on the new api data
     * (which should be from today) and saves them in the directory {@code
     * directory}. The rates should be specified in the form <i>"1 base
     * currency unit = x other currency units"</i>. If a currency was removed
     * from the api it will make up for this by using the last known exchange
     * rate for that currency (if possible).
     *
     * @param   base
     *          The base currency on which all values are based.
     * @param   rates
     *          The values of the currencies relative to 1 unit of the base
     *          currency.
     * @param   directory
     *          The directory in which the exchange rates should be saved.
     */
    public static void generateExchangeRates(Currency base,
                                             HashMap<Currency, Double> rates,
                                             File directory) {
        knownCurrencies.add(base);
        knownCurrencies.addAll(rates.keySet());

        LocalDate today = LocalDate.now();

        // simplify the algorithm
        rates.put(base, 1.0);

        for (Currency from : knownCurrencies) {
            for (Currency to : knownCurrencies) {
                try {
                    generate(base, rates, directory, from, to, today);
                } catch (Exception e) {
                    // let errors pass silently to not obstruct the rest of the
                    // program (they're probably fine if ignored, but print them
                    // to be sure)
                    e.printStackTrace();
                }
            }
        }
    }

    private static void generate(Currency base, HashMap<Currency, Double> rates,
                                 File directory, Currency from, Currency to,
                                 LocalDate today) throws IOException {
        if (rates.containsKey(from) && rates.containsKey(to)) {
            ExchangeRate newExchangeRate = new ExchangeRate(today, from, to,
                            rates.get(to) / rates.get(from));

            newExchangeRate.save(directory);
        }
        // in case of no longer supported currencies:
        else if (rates.containsKey(from) && !rates.containsKey(to)) {
            ExchangeRate newExchangeRate = new ExchangeRate(today, from, to,
                            getMostRecent(base, to).rate / rates.get(from));

            newExchangeRate.save(directory);

        } else if (!rates.containsKey(from) && rates.containsKey(to)) {
            ExchangeRate newExchangeRate = new ExchangeRate(today, from, to,
                            rates.get(to) / getMostRecent(base, from).rate);

            newExchangeRate.save(directory);

        } else {
            ExchangeRate newExchangeRate = new ExchangeRate(today, from, to,
                                                getMostRecent(from, to).rate);
            newExchangeRate.save(directory);
        }
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

        // because this will break an assumption in the conversion function...
        if (from != null && Objects.equals(from, to) && temp.rate != 1.0)
            throw new IllegalArgumentException("Conversion to the same " +
                    "currency doesn't have a rate of 1.0!");

        setRate(rate);
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
     * Checks whether {@code currency} is a valid
     * <a href="https://en.wikipedia.org/wiki/ISO_4217">ISO 4217</a> code.
     *
     * @param   currencyCode
     *          The {@code String} to be checked.
     *
     * @return  Whether the {@code String} is a valid ISO 4217 code.
     */
    public static boolean isValidCurrencyCode(String currencyCode) {
        return Money.isValidCurrencyCode(currencyCode);
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
        return "ExchangeRate on " + date + " from " + from + " to " + to + " is"
                + rate;
    }
}
