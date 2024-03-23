package server.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.time.LocalDate;

import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Creates and stores {@link ExchangeRate ExchangeRates}.
 */
public class ExchangeRateFactory {

    private static final File DEFAULT_DIR = new File("rates");
    private static final ExchangeRateFactory global =
            new ExchangeRateFactory(DEFAULT_DIR);

    /**
     * A set containing all known {@code ExchangeRates}.
     */
    private final HashSet<ExchangeRate> exchangeRates = new HashSet<>();
    private final HashSet<Currency> knownCurrencies = new HashSet<>();
    private final File directory;

    /**
     * Constructs an {@code ExchangeRateFactory}. Direct calls are only for
     * testing purposes. Use {@link ExchangeRateFactory#get()} to get the global
     * non-testing {@code ExchangeRateFactory}.
     *
     * @param   directory
     *          The directory in which file should be saved.
     */
    ExchangeRateFactory(File directory) {
        this.directory = directory;
    }

    /**
     * Gets the default global {@code ExchangeRateFactory} object to be used in
     * this program.
     *
     * @return  The default global {@code ExchangeRateFactory} object.
     */
    public static ExchangeRateFactory get() {
        return global;
    }

    /**
     * Gets all loaded {@link ExchangeRate ExchangeRates}.
     *
     * @return  A set containing all loaded {@code ExchangeRate}s.
     */
    public Set<ExchangeRate> getExchangeRates() {
        return Set.copyOf(exchangeRates);
    }

    /**
     * Gets all loaded {@link Currency Currencies}.
     *
     * @return  A set containing all loaded {@code Currencies}.
     */
    public Set<Currency> getKnownCurrencies() {
        return Set.copyOf(knownCurrencies);
    }

    /**
     * Gets all {@link ExchangeRate}s converting from a specific currency to
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
     *          another currency.
     */
    public Set<ExchangeRate> getExchangeRates(Currency from, Currency to) {
        return Set.copyOf(exchangeRates.stream()
                .filter(er -> Objects.equals(from, er.getFrom())
                        && Objects.equals(to, er.getTo()))
                .collect(Collectors.toSet()));
    }

    /**
     * Gets all {@link ExchangeRate}s converting from a specific currency. Only
     * considers already loaded {@code ExchangeRate}s.
     *
     * @param   from
     *          The base currency in the exchange rate as a {@link Currency}
     *          object.
     *
     * @return  The corresponding {@code ExchangeRate}s.
     */
    public Set<ExchangeRate> getExchangeRates(Currency from) {
        return Set.copyOf(exchangeRates.stream()
                .filter(er -> Objects.equals(from, er.getFrom()))
                .collect(Collectors.toSet()));
    }

    /**
     * Gets all {@link ExchangeRate}s from the date {@code}. Only considers
     * already loaded {@code ExchangeRate}s.
     *
     * @param   date
     *          The date on which the exchange rate was retrieved.
     *
     * @return  The corresponding {@code ExchangeRate}s.
     */
    public Set<ExchangeRate> getExchangeRates(LocalDate date) {
        return exchangeRates.stream()
                .filter(er -> Objects.equals(date, er.getDate()))
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Gets the {@link ExchangeRate} specified by the parameters. Returns {@code
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
    public ExchangeRate getExchangeRate(LocalDate date, Currency from,
                                        Currency to) {
        return exchangeRates.stream()
                .filter(er -> Objects.equals(date, er.getDate())
                        && Objects.equals(from, er.getFrom())
                        && Objects.equals(to, er.getTo()))
                .findFirst().orElse(null);
    }

    /**
     * Gets the most recent {@link ExchangeRate} that qualifies the parameters.
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
    public ExchangeRate getMostRecent(Currency from, Currency to) {
        return exchangeRates.stream()
                .filter(er -> Objects.equals(from, er.getFrom())
                        && Objects.equals(to, er.getTo()))
                .max(Comparator.comparing(ExchangeRate::getDate))
                .orElse(null);
    }

    /**
     * Checks whether an {@link ExchangeRate} is present in this {@code
     * ExchangeRateFactory}.
     *
     * @param   exchangeRate
     *          The {@code ExchangeRate} to check.
     *
     * @return  Whether {@code exchangeRate} is present.
     */
    public boolean hasExchangeRate(ExchangeRate exchangeRate) {
        return exchangeRates.contains(exchangeRate);
    }

    private static final Pattern FILE_PATTERN =
            Pattern.compile("^(?<date>[0-9]{4}-(?>0[1-9]|1[012])-" +
                    "(?>0[1-9]|[12][0-9]|3[01]))\\." +
                    "(?<from>[A-Z]{3})\\.(?<to>[A-Z]{3})\\.txt$");

    /**
     * Loads all {@code ExchangeRate} objects into memory from the {@link
     * ExchangeRateFactory#directory}.
     *
     * @throws  IOException
     *          If an IO error occurred.
     */
    public void loadAll() throws IOException {
        try (DirectoryStream<Path> stream =
                     Files.newDirectoryStream(directory.toPath())) {
            // this is a bad idea as this doesn't scale well, as the space
            // complexity is O(c²d), with c = amount of currencies and d =
            // amount of days, but then they should've let us use the database
            // (and for this small project it doesn't really matter anyway).
            for (Path file : stream) {
                Matcher matcher = FILE_PATTERN.matcher(
                        file.getFileName().toString()
                );
                if (matcher.matches()) {
                    try {
                        read(
                                LocalDate.parse(matcher.group("date")),
                                Currency.getInstance(matcher.group("from")),
                                Currency.getInstance(matcher.group("to"))
                        );
                    } catch (Exception e) {
                        // do nothing, as invalid files are invalid and you cant
                        // make them be any more valid than that :)

                        // tho print it to save a future dev debugging this
                        // 5 minutes of his/her time
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Loads an {@code ExchangeRate} object from the corresponding file in the
     * {@link ExchangeRateFactory#directory}. Returns {@code null} if
     * for any reason the {@code ExchangeRate} couldn't be fetched from that
     * file.
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
     *
     * @throws  FileNotFoundException
     *          If the file in which the exchange rates was expected was not
     *          found.
     */
    public ExchangeRate read(LocalDate date, Currency from, Currency to)
            throws FileNotFoundException {
        try (Scanner reader = new Scanner(
                new File(directory, generateFileName(date, to, from))
        )) {
            ExchangeRate result =
                    new ExchangeRate(date, to, from, reader.nextDouble());
            // remove to update any old exchange rates
            exchangeRates.remove(result);
            exchangeRates.add(result);
            return result;
        }
    }

    /**
     * Saves a {@link ExchangeRate} object to the corresponding file in the
     * directory {@link ExchangeRateFactory#directory}.
     *
     * @param   exchangeRate
     *          The {@code ExchangeRate} object to save.
     *
     * @throws  IOException
     *          If an IO error occurs.
     */
    public void write(ExchangeRate exchangeRate) throws IOException {
        try (FileWriter writer = new FileWriter(
                new File(directory, generateFileName(exchangeRate))
        )) {
            writer.write(Double.toString(exchangeRate.getRate()));
        }
    }

    /**
     * Generates new {@code ExchangeRate} objects based on the new api data
     * (which should be from today) and saves them in the directory {@link
     * ExchangeRateFactory#directory}. The rates should be specified in the
     * form <i>"1 base currency unit = x other currency units"</i>. If a
     * currency was removed from the api it will make up for this by using the
     * last known exchange rate for that currency. So, preferably, <em><strong>
     * call this method after a call to {@link ExchangeRateFactory#loadAll()}.
     * </strong></em>
     *
     * @param   base
     *          The base currency on which all values are based.
     * @param   rates
     *          The values of the currencies relative to 1 unit of the base
     *          currency.
     */
    public void generateExchangeRates(Currency base,
                                             HashMap<Currency, Double> rates) {
        knownCurrencies.add(base);
        knownCurrencies.addAll(rates.keySet());

        LocalDate today = LocalDate.now();

        // simplify the algorithm
        rates.put(base, 1.0);

        for (Currency from : knownCurrencies) {
            for (Currency to : knownCurrencies) {
                try {
                    ExchangeRate result =
                            generate(base, rates, from, to, today);

                    // remove to update any old exchange rates
                    exchangeRates.remove(result);
                    exchangeRates.add(result);
                } catch (Exception e) {
                    // let errors pass silently to not obstruct the rest of the
                    // program (they're probably fine if ignored, but print them
                    // to be sure)
                    e.printStackTrace();
                }
            }
        }
    }

    /*====================================================================||
    ||                                                                    ||
    ||   These functions are only package private in order to test them   ||
    ||                        (So don't call them)                        ||
    ||                                                                    ||
    ||====================================================================*/

    /**
     * Adds an {@link ExchangeRate} object to this factory. <em><strong>SHOULD
     * ONLY BE USED FOR TESTING.</strong></em> Use {@link
     * ExchangeRateFactory#generateExchangeRates(Currency, HashMap)} for
     * non-testing adding of exchange rates.
     *
     * @param   exchangeRate
     *          The {@code ExchangeRate} to add.
     */
    void addExchangeRate(ExchangeRate exchangeRate) {
        exchangeRates.add(exchangeRate);
        knownCurrencies.add(exchangeRate.getFrom());
        knownCurrencies.add(exchangeRate.getTo());
    }

    /**
     * Interpolates {@link ExchangeRate ExchangeRates} from the present data.
     * <em><strong>SHOULD ONLY BE USED INTERNALLY AND IN UNIT TESTS!</strong>
     * </em>
     *
     * @param   base
     *          The base {@link Currency} of {@code rates}.
     * @param   rates
     *          Today's rates.
     * @param   from
     *          The base {@code Currency} of the requested {@code ExchangeRate}.
     * @param   to
     *          The converted {@code Currency} of the requested {@code
     *          ExchangeRate}.
     * @param   today
     *          A {@link LocalDate} object storing the day.
     *
     * @return  The interpolated {@code ExchangeRate}.
     */
    ExchangeRate generate(Currency base,
                                  HashMap<Currency, Double> rates,
                                  Currency from, Currency to, LocalDate today) {
        if (rates.containsKey(from) && rates.containsKey(to))
            return new ExchangeRate(today, from, to,
                    rates.get(to) / rates.get(from));

        // in case of no longer supported currencies:

        // will throw NullPointerException if no rate between base and the
        // problematic currency can be found (semi intended behaviour, as it
        // should throw an Exception but maybe NullPointerException isn't the
        // most descriptive one)
        if (rates.containsKey(from) && !rates.containsKey(to))
            return new ExchangeRate(today, from, to,
                    getMostRecent(base, to).getRate() / rates.get(from));

        if (!rates.containsKey(from) && rates.containsKey(to))
            return new ExchangeRate(today, from, to,
                    rates.get(to) / getMostRecent(base, from).getRate());

        // will throw NullPointerException if no rate between the
        // problematic currencies can be found
        return new ExchangeRate(today, from, to,
                getMostRecent(from, to).getRate());
    }

    /**
     * Generates a file name based upon the provided {@link ExchangeRate}.
     * <em><strong>SHOULD ONLY BE USED INTERNALLY AND IN UNIT TESTS!</strong>
     * </em>
     *
     * @param   exchangeRate
     *          The provided {@code ExchangeRate}.
     *
     * @return  The resulting file name.
     */
    static String generateFileName(ExchangeRate exchangeRate) {
        return generateFileName(exchangeRate.getDate(), exchangeRate.getFrom(),
                exchangeRate.getTo());
    }

    /**
     * Generates a file name based upon the provided parameters.
     * <em><strong>SHOULD ONLY BE USED INTERNALLY AND IN UNIT TESTS!</strong>
     * </em>
     *
     * @return  The resulting file name.
     */
    static String generateFileName(LocalDate date, Currency from,
                                           Currency to) {
        return date + "." + from + "." + to + ".txt";
    }
}
