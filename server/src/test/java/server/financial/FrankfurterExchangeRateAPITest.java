package server.financial;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class FrankfurterExchangeRateAPITest {

    @Test
    void getBase() {
        assertEquals(Currency.getInstance("EUR"), new FrankfurterExchangeRateAPI().getBase());
        assertEquals(Currency.getInstance("USD"), new FrankfurterExchangeRateAPI(Currency.getInstance("USD")).getBase());
    }
}