package client.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class UserConfigTest {

    private HashMap<String, String> hashMapString;
    private HashMap<String, Integer> hashMapInteger;

    @BeforeEach
    void setup() {
        hashMapString = new HashMap<>();
        hashMapInteger = new HashMap<>();

        hashMapString.put("ab", "1");
        hashMapString.put("bc", "2");
        hashMapInteger.put("ab", 1);
        hashMapInteger.put("bc", 2);
    }


    @Test
    void toDouble() {
        assertEquals(1.0, UserConfig.toDouble("1.0"));
    }

    @Test
    void fromDouble() {
        assertEquals("1.0", UserConfig.fromDouble(1.0));
    }

    @Test
    void toArray() {
        assertArrayEquals(new String[0], UserConfig.toArray(null));
        assertArrayEquals(new String[] {""}, UserConfig.toArray(""));
        assertArrayEquals(new String[] {"ab", "bc"}, UserConfig.toArray("ab,bc"));
    }

    @Test
    void testToArray() {
        assertArrayEquals(new String[] {"ab", "bc"}, UserConfig.toArray("ab;bc", ';'));
    }

    @Test
    void fromArray() {
        assertNull(UserConfig.fromArray(new String[0]));
        assertNull(UserConfig.fromArray(null));
        assertEquals("", UserConfig.fromArray(new String[] {""}));
        assertEquals("ab,bc", UserConfig.fromArray(new String[] {"ab", "bc"}));
    }

    @Test
    void testFromArray() {
        assertEquals("ab;bc", UserConfig.fromArray(new String[] {"ab", "bc"}, ';'));
    }

    @Test
    void toHashMap() {
        assertEquals(new HashMap<String, String>(), UserConfig.toHashMap(null));
        assertEquals(new HashMap<String, String>(), UserConfig.toHashMap(""));
        assertEquals(hashMapString, UserConfig.toHashMap("ab,1;bc,2"));
        assertThrows(IllegalArgumentException.class, () -> UserConfig.toHashMap("ab,1,2;bc,2"));
    }

    @Test
    void testToHashMap() {
        assertEquals(hashMapInteger, UserConfig.toHashMap("ab,1;bc,2", Integer::valueOf));
        assertThrows(IllegalArgumentException.class, () -> UserConfig.toHashMap("ab|1/bc|2", null));
    }

    @Test
    void testToHashMap1() {
        assertEquals(hashMapInteger, UserConfig.toHashMap("ab|1/bc|2", '|', '/', Integer::valueOf));
        assertThrows(IllegalArgumentException.class, () -> UserConfig.toHashMap("ab|1/bc|2", '|', '|', Integer::valueOf));
    }

    @Test
    void fromHashMap() {
        assertNull(UserConfig.fromHashMap(null));
        assertNull(UserConfig.fromHashMap(new HashMap<>()));
        assertEquals("ab,1;bc,2", UserConfig.fromHashMap(hashMapString));
        hashMapString.put(",", ";");  // should raise an exception as a separator value is present in the hashmap
        assertThrows(IllegalArgumentException.class, () -> UserConfig.fromHashMap(hashMapString));
    }

    @Test
    void testFromHashMap() {
        assertEquals("ab,1;bc,2", UserConfig.fromHashMap(hashMapInteger, Object::toString));
        assertThrows(IllegalArgumentException.class, () -> UserConfig.fromHashMap(hashMapInteger, null));
    }

    @Test
    void testFromHashMap1() {
        assertEquals("ab|1/bc|2", UserConfig.fromHashMap(hashMapInteger, '|', '/', Object::toString));
        assertThrows(IllegalArgumentException.class, () -> UserConfig.fromHashMap(hashMapInteger, ',', ',', Object::toString));
    }
}