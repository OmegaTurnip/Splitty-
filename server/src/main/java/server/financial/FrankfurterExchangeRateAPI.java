package server.financial;

import commons.Money;
import org.springframework.boot.json.JacksonJsonParser;

import java.io.IOException;
import java.net.*;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Map;
import java.util.HashMap;

import java.util.Optional;
import java.util.stream.Collectors;

public class FrankfurterExchangeRateAPI implements ExchangeRateAPI {

    private final URL apiUrl;

    private final Currency base;

    private LocalDate lastRequest;


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
        return Optional.ofNullable(lastRequest);
    }

    /**
     * Requests and returns the new exchange rates from the API. Should use
     * {@link ExchangeRateAPI#getBase()} as the base currency. Should be used in
     * combination with the {@link
     * ExchangeRateFactory#generateExchangeRates(Currency, HashMap)} method (and
     * thus return the result in the format required by that function). Returns
     * an empty {@link Optional} if the request fails.
     *
     * @return The new exchange rates.
     */
    @Override
    // make the compiler shut up about the unchecked cast:
    @SuppressWarnings("unchecked")
    public Optional<Map<Currency, Double>> getExchangeRates() {
        // let's not do failed requests over and even record failed requests as
        // a request made
        lastRequest = LocalDate.now();

        // this might be the most horrendous code I've ever written
        // I hate it with every fiber of my being
        try {
            URLConnection con = apiUrl.openConnection();
            con.setConnectTimeout(5_000);
            con.setReadTimeout(5_000);

            // idc
            String result = new String(con.getInputStream().readAllBytes());

            JacksonJsonParser mapper = new JacksonJsonParser();
            return Optional.of(
                    // idc, I hate this, but idc
                    ((Map<String, Double>) mapper.parseMap(result)
                            .get("rates"))
                            .entrySet()
                            .stream()
                            .filter(e -> Money.isValidCurrencyCode(e.getKey()))
                            .collect(
                                    Collectors.toMap(
                                            e -> Currency
                                                    .getInstance(e.getKey()),
                                            Map.Entry::getValue
                                    )
                            )
            );
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
