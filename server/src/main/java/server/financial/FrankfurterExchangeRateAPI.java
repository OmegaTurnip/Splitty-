package server.financial;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Money;

import java.io.IOException;
import java.net.*;
import java.time.LocalDate;
import java.util.*;

import java.util.stream.Collectors;

public class FrankfurterExchangeRateAPI implements ExchangeRateAPI {

    private final URL apiUrlLatest;

    private final String apiUrlOnDate;

    private final Currency base;

    private final Set<LocalDate> requestedDates = new HashSet<>();


    /**
     * Creates a new {@code FrankfurterExchangeRateAPI} object, requesting
     * exchange rates in EUR.
     */
    public FrankfurterExchangeRateAPI() {
        this(null);
    }

    /**
     * Creates a new {@code FrankfurterExchangeRateAPI} object.
     *
     * @param   base
     *          The base currency for the exchange rates. If {@code null}, the
     *          base currency is assumed to be EUR.
     */
    public FrankfurterExchangeRateAPI(Currency base) {
        this.base = base == null ? Currency.getInstance("EUR") : base;
        try {
            this.apiUrlLatest =
                    URI.create("https://api.frankfurter.app/latest?from=" +
                    this.base.getCurrencyCode()).toURL();
            this.apiUrlOnDate = "https://api.frankfurter.app/";
        } catch (MalformedURLException e) {
            throw new RuntimeException("This is impossible (unless somebody " +
                    "messed with the url (╯°□°）╯︵ ┻━┻)", e);
        }
    }

    /**
     * Gets the base currency for the exchange rates.
     *
     * @return  The base currency for the exchange rates.
     */
    public Currency getBase() {
        return base;
    }

    /**
     * Returns the dates of the last api requests.
     *
     * @return  The date of the last api request.
     */
    public Set<LocalDate> getRequestDates() {
        return requestedDates;
    }

    /**
     * Requests and returns the new exchange rates from the API. Should use
     * {@link ExchangeRateAPI#getBase()} as the base currency. Should be used in
     * combination with the {@link
     * ExchangeRateFactory#generateExchangeRates(Currency, Map)} method (and
     * thus return the result in the format required by that function). Returns
     * an empty {@link Optional} if the request fails.
     *
     * @return The new exchange rates.
     */
    @Override
    public Optional<Map<Currency, Double>> getExchangeRates() {
        // let's not do failed requests over and even record failed requests as
        // a request made
        requestedDates.add(LocalDate.now());
        try {
            URLConnection con = apiUrlLatest.openConnection();
            return makeRequest(con);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Requests and returns the new exchange rates from the API. Should use
     * {@link ExchangeRateAPI#getBase()} as the base currency. Should be used in
     * combination with the {@link
     * ExchangeRateFactory#generateExchangeRates(Currency, Map)} method (and
     * thus return the result in the format required by that function). Returns
     * an empty {@link Optional} if the request fails.
     *
     * @param   date
     *          The date for which to request the exchange rates.
     *
     * @return The new exchange rates.
     */
    @Override
    public Optional<Map<Currency, Double>> getExchangeRates(LocalDate date) {
        // let's not do failed requests over and even record failed requests as
        // a request made
        requestedDates.add(date);
        try {
            URLConnection con = URI.create(
                    apiUrlOnDate.concat(
                            date.toString() + "?from=" +
                                    base.getCurrencyCode()
                    )
            ).toURL().openConnection();
            return makeRequest(con);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Makes a request to the API, returning the exchange rates.
     *
     * @param   con
     *          The connection to the API. Should already be opened.
     *
     * @return  The exchange rates.
     *
     * @throws  IOException
     *          If an I/O error occurs.
     */
    private Optional<Map<Currency, Double>> makeRequest(URLConnection con)
            throws IOException {
        con.setConnectTimeout(5_000);
        con.setReadTimeout(5_000);

        String result = new String(con.getInputStream().readAllBytes());

        ObjectMapper mapper = new ObjectMapper();
        Response response = mapper.readValue(result, Response.class);

        return Optional.of(
                response.rates.entrySet().stream()
                        .filter(e -> Money.isValidCurrencyCode(e.getKey()))
                        .collect(Collectors.toMap(
                                e -> Currency.getInstance(e.getKey()),
                                Map.Entry::getValue
                        ))
        );
    }

    private record Response(int amount, String base, String date,
                            Map<String, Double> rates) {
        @JsonCreator
        private Response(@JsonProperty("amount") int amount,
                         @JsonProperty("base") String base,
                         @JsonProperty("date") String date,
                         @JsonProperty("rates") Map<String, Double> rates) {
            this.amount = amount;
            this.base = base;
            this.date = date;
            this.rates = rates;
        }
    }
}
