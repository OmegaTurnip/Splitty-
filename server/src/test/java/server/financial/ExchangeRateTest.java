package server.financial;

import commons.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.financial.ExchangeRate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateTest {

    private ExchangeRate exchangeRate;
    private final static Currency EUR = Currency.getInstance("EUR");
    private final static Currency USD = Currency.getInstance("USD");
    private final static LocalDate date = LocalDate.ofYearDay(2005, 13);

    @BeforeEach
    void setup() {
        exchangeRate = new ExchangeRate(date, EUR, USD, 1.1);
    }

    @Test
    void constructor() {
        assertThrows(NullPointerException.class,  () -> new ExchangeRate(null, EUR, USD, 1.1));
        assertThrows(NullPointerException.class,  () -> new ExchangeRate(date, null, USD, 1.1));
        assertThrows(NullPointerException.class,  () -> new ExchangeRate(date, EUR, null, 1.1));

        assertThrows(IllegalArgumentException.class,  () -> new ExchangeRate(date, EUR, USD, -1.1));
        assertThrows(IllegalArgumentException.class,  () -> new ExchangeRate(date, EUR, EUR, 2d));
    }

    @Test
    void getDate() {
        assertEquals(LocalDate.ofYearDay(2005, 13), exchangeRate.getDate());
    }

    @Test
    void getFrom() {
        assertEquals(EUR, exchangeRate.getFrom());
    }

    @Test
    void getTo() {
        assertEquals(USD, exchangeRate.getTo());
    }

    @Test
    void getRate() {
        assertEquals(1.1, exchangeRate.getRate());
    }

    @Test
    void convert() {
        assertEquals(new Money(new BigDecimal(11), USD), exchangeRate.convert(new Money(BigDecimal.TEN, EUR)));

        assertThrows(NullPointerException.class, () -> exchangeRate.convert(null));
        assertThrows(IllegalArgumentException.class, () -> exchangeRate.convert(new Money(BigDecimal.TEN, USD)));

        ExchangeRate usdToUsd = new ExchangeRate(date, USD, USD, 1d);
        Money tenDollar = new Money(BigDecimal.TEN, USD);
        assertSame(tenDollar, usdToUsd.convert(tenDollar));
    }

    @Test
    void testEquals() {
        assertEquals(new ExchangeRate(LocalDate.ofYearDay(2005, 13), EUR, USD, 1.1), exchangeRate);
        assertEquals(new ExchangeRate(LocalDate.ofYearDay(2005, 13), EUR, USD, 546), exchangeRate);
    }

    @Test
    void testHashCode() {
        assertEquals((new ExchangeRate(LocalDate.ofYearDay(2005, 13), EUR, USD, 1.1)).hashCode(), exchangeRate.hashCode());
        assertEquals((new ExchangeRate(LocalDate.ofYearDay(2005, 13), EUR, USD, 546)).hashCode(), exchangeRate.hashCode());

    }

    @Test
    void testToString() {
        assertEquals("ExchangeRate on 2005-01-13 from EUR to USD is 1.1", exchangeRate.toString());
    }
}