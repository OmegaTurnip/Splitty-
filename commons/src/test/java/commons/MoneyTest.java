package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    private Money money1;
    private Money money2;
    private Money money3;
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency JPY = Currency.getInstance("JPY");

    @BeforeEach
    void setup() {
        money1 = new Money(BigDecimal.TEN, EUR);
        money2 = new Money(new BigDecimal(100_000_000), USD);
        money3 = new Money(BigDecimal.TWO, JPY);
    }

    @Test
    void constructor() {
        assertThrows(NullPointerException.class, () -> new Money(null, null));
    }

    @Test
    void getAmount() {
        assertEquals(new BigDecimal(10).setScale(2), money1.getAmount());
        assertEquals(new BigDecimal(2).setScale(0), money3.getAmount());
    }

    @Test
    void setAmount() {
        money1.setAmount(BigDecimal.TWO);
        assertEquals(new BigDecimal(2).setScale(2), money1.getAmount());
    }

    @Test
    void getCurrency() {
        assertEquals(EUR, money1.getCurrency());
    }

    @Test
    void format() {
        // \u00A0 is a special space that prevents line wrapping
        assertEquals("US$100,000,000.00", money2.format(Locale.forLanguageTag("en-GB")));
        assertEquals("US$\u00A0100.000.000,00", money2.format(Locale.forLanguageTag("nl-NL")));
        assertEquals("100.000.000,00\u00A0$", money2.format(Locale.forLanguageTag("de-DE")));
        assertEquals("$100,000,000.00", money2.format(Locale.forLanguageTag("ja-JP")));
        // \u00A5 is the yen sign (¥)
        assertEquals("JP\u00A52", money3.format(Locale.forLanguageTag("en-GB")));
        assertEquals("JP\u00A5\u00A02", money3.format(Locale.forLanguageTag("nl-NL")));
        assertEquals("2\u00A0\u00A5", money3.format(Locale.forLanguageTag("de-DE")));
        // special Japanese yen symbol (￥)
        assertEquals("\uffe52", money3.format(Locale.forLanguageTag("ja-JP")));
    }

    @Test
    void compareTo() {
        assertTrue(money2.compareTo(new Money(new BigDecimal(100), USD)) > 0);
        assertThrows(IllegalArgumentException.class, () -> money1.compareTo(money2));
    }

    @Test
    void testEquals() {
        assertNotEquals(money1, money2);
        assertEquals(money1, new Money(BigDecimal.TEN, EUR));
    }

    @Test
    void testHashCode() {
        assertEquals(money1.hashCode(), new Money(BigDecimal.TEN, EUR).hashCode());
    }

    @Test
    void testToString() {
        assertEquals("Money { 10.00 EUR }", money1.toString());
        assertEquals("Money { 100000000.00 USD }", money2.toString());
        assertEquals("Money { 2 JPY }", money3.toString());
    }

    @Test
    void isValidCurrencyCode() {
        assertTrue(Money.isValidCurrencyCode("USD"));
        assertFalse(Money.isValidCurrencyCode("fogh"));
        assertFalse(Money.isValidCurrencyCode(null));
    }
}