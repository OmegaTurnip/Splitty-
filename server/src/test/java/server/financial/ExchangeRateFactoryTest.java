package server.financial;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateFactoryTest {

    private ExchangeRateFactory exchangeRateFactory;
    private Set<ExchangeRate> exchangeRates;
    private ExchangeRate exchangeRate1;
    private final static Currency EUR = Currency.getInstance("EUR");
    private final static Currency USD = Currency.getInstance("USD");
    private final static Currency JPY = Currency.getInstance("JPY");
    private final static LocalDate date = LocalDate.ofYearDay(2010, 26);

    @BeforeEach
    void setup() {
        exchangeRateFactory = new ExchangeRateFactory(new File("ERFTRates"), null);
        exchangeRates = new HashSet<>();

        exchangeRates.add(new ExchangeRate(date, USD, USD,  1));
        exchangeRates.add(exchangeRate1 = new ExchangeRate(date, EUR, USD, 1.1));
        exchangeRates.add(new ExchangeRate(date, USD, EUR,  1/1.1));
        exchangeRates.add(new ExchangeRate(date, EUR, EUR,  1));

        for (ExchangeRate exchangeRate : exchangeRates)
            exchangeRateFactory.addExchangeRate(exchangeRate);

    }

    @Test
    void GetExchangeRates() {
        Set<ExchangeRate> expected = new HashSet<>();
        expected.add(new ExchangeRate(date, USD, USD,  1));
        expected.add(new ExchangeRate(date, EUR, USD, 1.1));
        expected.add(new ExchangeRate(date, USD, EUR,  1/1.1));
        expected.add(new ExchangeRate(date, EUR, EUR,  1));

        assertEquals(expected, exchangeRateFactory.getExchangeRates());
    }

    @Test
    void getKnownCurrencies() {
        Set<Currency> expected = new HashSet<>();
        expected.add(USD);
        expected.add(EUR);

        assertEquals(expected, exchangeRateFactory.getKnownCurrencies());
    }

    @Test
    void testGetExchangeRates1() {
        Set<ExchangeRate> expected = new HashSet<>();
        expected.add(new ExchangeRate(date, USD, USD,  1));
        expected.add(new ExchangeRate(date, USD, EUR,  1/1.1));

        assertEquals(expected, exchangeRateFactory.getExchangeRates(USD));
    }

    @Test
    void testGetExchangeRates2() {
        Set<ExchangeRate> expected = new HashSet<>();
        expected.add(new ExchangeRate(date, EUR, USD,  1.1));

        assertEquals(expected, exchangeRateFactory.getExchangeRates(EUR, USD));

        ExchangeRate older = new ExchangeRate(date.minusDays(1), EUR, USD,  1.2);
        exchangeRateFactory.addExchangeRate(older);
        expected.add(older);

        assertEquals(expected, exchangeRateFactory.getExchangeRates(EUR, USD));
    }

    @Test
    void testGetExchangeRates3() {
        assertEquals(exchangeRates, exchangeRateFactory.getExchangeRates(date));
        exchangeRateFactory.addExchangeRate(new ExchangeRate(date.minusDays(1), EUR, USD,  1.2));

        assertEquals(exchangeRates, exchangeRateFactory.getExchangeRates(date));
    }


    @Test
    void getExchangeRate() {
        assertEquals(exchangeRate1, exchangeRateFactory.getExchangeRate(date, EUR, USD));
        assertNull(exchangeRateFactory.getExchangeRate(date, EUR, JPY));
    }

    @Test
    void getMostRecent() {
        exchangeRateFactory.addExchangeRate(new ExchangeRate(date.minusDays(1), EUR, USD, 1.2));
        assertEquals(exchangeRate1, exchangeRateFactory.getMostRecent(EUR, USD));
        ExchangeRate newest = new ExchangeRate(date.plusDays(1), EUR, USD, 1.0);
        exchangeRateFactory.addExchangeRate(newest);
        assertEquals(newest, exchangeRateFactory.getMostRecent(EUR, USD));
        assertNull(exchangeRateFactory.getMostRecent(EUR, JPY));
    }

    @Test
    void generateExchangeRates() {
        HashMap<Currency, Double> rates = new HashMap<>();
        rates.put(JPY, 40d);
        exchangeRateFactory.generateExchangeRates(EUR, rates);
        
    }

    @Test
    void hasExchangeRate() {
        assertTrue(exchangeRateFactory.hasExchangeRate(exchangeRate1));
        assertFalse(exchangeRateFactory.hasExchangeRate(new ExchangeRate(date, JPY, USD, 0.05)));
    }

    @Test
    void generateFileName() {
        assertEquals("2010-01-26.EUR.USD.txt", ExchangeRateFactory.generateFileName(date, EUR, USD));
        assertEquals("2010-01-26.EUR.USD.txt", ExchangeRateFactory.generateFileName(exchangeRate1));
    }
}