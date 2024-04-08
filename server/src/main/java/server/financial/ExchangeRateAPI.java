package server.financial;

import java.time.LocalDate;
import java.util.*;

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
     * ExchangeRateFactory#generateExchangeRates(Currency, Map)} method (and
     * thus return the result in the format required by that function). Returns
     * an empty {@link Optional} if the request fails.
     *
     * @return  The new exchange rates.
     */
    Optional<Map<Currency, Double>> getExchangeRates();

    /**
     * Requests and returns the exchange rates from the API. Should use
     * {@link ExchangeRateAPI#getBase()} as the base currency. Should be used in
     * combination with the {@link
     * ExchangeRateFactory#generateExchangeRates(Currency, Map)} method (and
     * thus return the result in the format required by that function). Returns
     * an empty {@link Optional} if the request fails.
     *
     * @param   date
     *          The date for which to request the exchange rates.
     *
     * @return  The new exchange rates.
     */
    Optional<Map<Currency, Double>> getExchangeRates(LocalDate date);

    /**
     * Returns the date of the last api request.
     *
     * @return  The date of the last api request.
     */
    Set<LocalDate> getRequestDates();
}
