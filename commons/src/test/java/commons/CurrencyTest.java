package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyTest {

    @Test
    void getCentsWithDouble() {
        Currency c1 = new Currency(120.42, "EUR");
        assertEquals(12042, c1.getCents());
    }

    @Test
    void getCentsWithInt() {
        Currency c1 = new Currency(12042, "EUR");
        assertEquals(12042, c1.getCents());
    }

    @Test
    void setCents() {
        Currency c1 = new Currency(1402, "EUR");
        c1.setCents(1500);
        assertEquals(1500, c1.getCents());
    }

    @Test
    void getCurrency() {
        Currency c1 = new Currency(12042, "EUR");
        assertEquals("EUR", c1.getCurrency());

    }

    @Test
    void getConvertedValueSameTest(){
        Currency c1 = new Currency(1, "EUR");
        c1.conversion("EUR");
        assertEquals(1, c1.getConvertedValue());
    }

    @Test
    void getConvertedValueUSDtoEUR(){
        Currency c1 = new Currency(1.0, "USD");
        c1.conversion("EUR");
        assertEquals(92, c1.getConvertedValue());
    }

    @Test
    void getConvertedValueEURtoUSD(){
        Currency c1 = new Currency(100, "EUR");
        c1.conversion("USD");
        assertEquals(108, c1.getConvertedValue());
    }

    @Test
    void getConvertedValueEURtoCHF(){
        Currency c1 = new Currency(100, "EUR");
        c1.conversion("CHF");
        assertEquals("CHF", c1.getCurrency());
        assertEquals(95, c1.getConvertedValue());
    }


    @Test
    void testEquals() {
        Currency c1 = new Currency(100, "EUR");
        Currency c2 = new Currency(100, "EUR");
        assertEquals(c1, c2);
    }

    @Test
    void testNotEquals() {
        Currency c1 = new Currency(101, "EUR");
        Currency c2 = new Currency(100, "EUR");
        assertNotEquals(c1, c2);
    }

    @Test
    void testHashCode() {
        Currency c1 = new Currency(100, "EUR");
        Currency c2 = new Currency(100, "EUR");
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testToString() {
        Currency c1 = new Currency(1242, "EUR");
        assertEquals("12.42 EUR", c1.toString());
    }
}