package client.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;


public class ConfigFileTest {

    private ConfigFile configFile;
    private static final File file = new File("unittest.properties");

    @BeforeEach
    public void setup() throws IOException {
        file.delete();
        configFile = new ConfigFile(file);
    }

    @AfterAll
    public static void cleanup() {
        ConfigFileTest.file.delete();
    }

    @Test
    public void testAttributes() {
        assertNull(configFile.getAttribute("attr"));
        assertDoesNotThrow(() -> configFile.setAttribute("attr", "val"));
        assertEquals("val", configFile.getAttribute("attr"));
    }

    @Test
    public void testPersistence() throws IOException {
        assertDoesNotThrow(() -> configFile.setAttribute("attr", "val"));
        ConfigFile configFile1 = new ConfigFile(file);
        assertEquals("val", configFile1.getAttribute("attr"));
    }

    @Test
    public void testOverwriting() throws IOException {
        assertDoesNotThrow(() -> configFile.setAttribute("attr", "val"));
        assertEquals("val", configFile.getAttribute("attr"));
        assertDoesNotThrow(() -> configFile.setAttribute("attr", "lav"));
        assertEquals("lav", configFile.getAttribute("attr"));
    }
}
