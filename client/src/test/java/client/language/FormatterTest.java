package client.language;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FormatterTest {

    private HashMap<String, String> hashMap;

    @BeforeEach
    void setup() {
        hashMap = new HashMap<>();
    }


    @Test
    void testEmptyFormatUnsafe() {
        assertEquals("", Formatter.formatUnsafe("", hashMap));
        assertEquals("test", Formatter.formatUnsafe("test", hashMap));
        assertEquals("price: {{price}}", Formatter.formatUnsafe("price: {{price}}", hashMap));
        hashMap.put("price", "€56");
        assertEquals("price", Formatter.formatUnsafe("price", hashMap));
    }

    @Test
    void testFormatUnsafe() {
        hashMap.put("price", "€56");
        assertEquals("price: €56", Formatter.formatUnsafe("price: {{price}}", hashMap));
        assertEquals("price: €56, {{msg}}", Formatter.formatUnsafe("price: {{price}}, {{msg}}", hashMap));
        hashMap.put("msg", "WoW!");
        assertEquals("price: €56, WoW!", Formatter.formatUnsafe("price: {{price}}, {{msg}}", hashMap));
        assertEquals("WoW! WoW!", Formatter.formatUnsafe("{{msg}} {{msg}}", hashMap));


        assertThrows(IllegalArgumentException.class, () -> Formatter.formatUnsafe("test", null));
        assertThrows(IllegalArgumentException.class, () -> Formatter.formatUnsafe("{{", hashMap));
        hashMap.put("?", "value");
        assertThrows(IllegalArgumentException.class, () -> Formatter.formatUnsafe("test", hashMap));
    }

    @Test
    void testParameterOccurrences() {
        HashMap<String, Integer> occurrences = new HashMap<>();
        assertEquals(occurrences,  Formatter.getParameterOccurrences("price: €56"));
        occurrences.put("price", 2);
        assertEquals(occurrences,  Formatter.getParameterOccurrences("dfas sf{{price}}ads{{price}}fds"));
        occurrences.put("name", 1);
        assertEquals(occurrences,  Formatter.getParameterOccurrences("dfas sf{{price}}a{{name}}ds{{price}}fds"));

        assertThrows(IllegalArgumentException.class, () -> Formatter.getParameterOccurrences("{{"));
    }

    @Test
    void testFormat() {
        assertEquals("", Formatter.format("", hashMap));
        assertEquals("test", Formatter.format("test", hashMap));
        assertThrows(IllegalArgumentException.class, () -> Formatter.format("price: {{price}}", hashMap));
        hashMap.put("price", "€56");
        assertEquals("price: €56", Formatter.format("price: {{price}}", hashMap));
        assertThrows(IllegalArgumentException.class, () -> Formatter.format("price: {{price}}, {{msg}}", hashMap));
        assertThrows(IllegalArgumentException.class, () -> Formatter.format("price", hashMap));
    }

    @Test
    void testIsValidFormat() {
        assertFalse(Formatter.isValidFormat(null));
        assertFalse(Formatter.isValidFormat("{{}}"));
        assertFalse(Formatter.isValidFormat("{{???}}"));
        assertFalse(Formatter.isValidFormat("{{}"));
        assertFalse(Formatter.isValidFormat("{{{name}}"));
        assertFalse(Formatter.isValidFormat("{{"));
        assertFalse(Formatter.isValidFormat("{{{{name}}test}}"));
        assertFalse(Formatter.isValidFormat("Lorem {{second word}}"));

        assertTrue(Formatter.isValidFormat(""));
        assertTrue(Formatter.isValidFormat("{{param}}"));
        assertTrue(Formatter.isValidFormat("Lorem Ipsum"));
        assertTrue(Formatter.isValidFormat("Lorem {{word2}}"));
        assertTrue(Formatter.isValidFormat("Lorem {{word2}} {{word3}}"));
    }

    @Test
    void testIsValidParameter() {
        assertFalse(Formatter.isValidParameter(null));
        assertFalse(Formatter.isValidParameter(""));
        assertFalse(Formatter.isValidParameter("?"));
        assertFalse(Formatter.isValidParameter("Second word"));

        assertTrue(Formatter.isValidParameter("1"));
        assertTrue(Formatter.isValidParameter("Aa"));
        assertTrue(Formatter.isValidParameter("AZaz09"));
        assertTrue(Formatter.isValidParameter("secondWord"));
    }



}