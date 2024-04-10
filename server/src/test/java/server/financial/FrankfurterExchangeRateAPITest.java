package server.financial;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Currency;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class FrankfurterExchangeRateAPITest {

    @Test
    void getBase() {
        assertEquals(Currency.getInstance("EUR"), new FrankfurterExchangeRateAPI().getBase());
        assertEquals(Currency.getInstance("USD"), new FrankfurterExchangeRateAPI(Currency.getInstance("USD")).getBase());
    }

    @Test
    void getRequestDates() {
        ExchangeRateAPI api = new FrankfurterExchangeRateAPI();
        assertNotNull(api.getRequestDates());
        assertEquals(new HashSet<LocalDate>(), api.getRequestDates());
    }
}