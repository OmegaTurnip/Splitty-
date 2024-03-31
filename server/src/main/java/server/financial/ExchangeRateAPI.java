package server.financial;

import java.time.LocalDate;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface ExchangeRateAPI {

    /**
     * The base currency for the exchange rates.
     *
     * @return  The base currency for the exchange rates.
     */
    Currency getBase();

    /**
     * Requests and returns the new exchange rates from the API. Should use
     * {@link ExchangeRateAPI#getBase()} as the base currency. Should be used in
     * combination with the {@link
     * ExchangeRateFactory#generateExchangeRates(Currency, HashMap)} method (and
     * thus return the result in the format required by that function). Returns
     * an empty {@link Optional} if the request fails.
     *
     * @return  The new exchange rates.
     */
    Optional<Map<Currency, Double>> getExchangeRates();

    /**
     * Returns the date of the last api request.
     *
     * @return  The date of the last api request.
     */
    Optional<LocalDate> lastRequestDate();
}
