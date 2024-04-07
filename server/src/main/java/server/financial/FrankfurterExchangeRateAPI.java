package server.financial;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Money;

import java.net.*;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Map;

import java.util.Optional;
import java.util.stream.Collectors;

public class FrankfurterExchangeRateAPI implements ExchangeRateAPI {

    private final URL apiUrl;

    private final Currency base;

    private LocalDate lastRequestDate;


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
            this.apiUrl = URI.create("https://api.frankfurter.app/latest?from="+
                    this.base.getCurrencyCode()).toURL();
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
     * Returns the date of the last api request.
     *
     * @return  The date of the last api request.
     */
    public Optional<LocalDate> lastRequestDate() {
        return Optional.ofNullable(lastRequestDate);
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
        lastRequestDate = LocalDate.now();

        try {
            URLConnection con = apiUrl.openConnection();
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
        } catch (Exception e) {
            return Optional.empty();
        }
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