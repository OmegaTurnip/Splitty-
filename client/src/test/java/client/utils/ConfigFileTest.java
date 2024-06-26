package client.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;


public class ConfigFileTest {

    private ConfigFile configFile;
    private static final File file = new File("unittest.properties");

    @BeforeEach
    void setup() throws IOException {
        file.delete();
        configFile = new ConfigFile(file, "test");
    }

    @AfterAll
    static void cleanup() {
        ConfigFileTest.file.delete();
        File file = new File(UserConfig.PATHNAME);
        file.delete();
    }

    @Test
    void testAttributes() {
        assertNull(configFile.getAttribute("attr"));
        assertFalse(configFile.hasAttribute("attr"));
        assertDoesNotThrow(() -> configFile.setAttribute("attr", "val"));
        assertEquals("val", configFile.getAttribute("attr"));
        assertTrue(configFile.hasAttribute("attr"));
    }

    @Test
    void testPersistence() throws IOException {
        assertDoesNotThrow(() -> configFile.setAttribute("attr", "val"));
        ConfigFile configFile1 = new ConfigFile(file, "test");
        assertEquals("val", configFile1.getAttribute("attr"));
    }

    @Test
    void testOverwriting() {
        assertDoesNotThrow(() -> configFile.setAttribute("attr", "val"));
        assertEquals("val", configFile.getAttribute("attr"));
        assertDoesNotThrow(() -> configFile.setAttribute("attr", "lav"));
        assertEquals("lav", configFile.getAttribute("attr"));
    }

    @Test
    void testRemoveAttribute() {
        assertDoesNotThrow(() -> configFile.setAttribute("attr", "val"));
        assertDoesNotThrow(() -> configFile.removeAttribute("attr"));
        assertNull(configFile.getAttribute("attr"));
        assertFalse(configFile.hasAttribute("attr"));
    }

    @Test
    void testSetContent() throws IOException {
        Properties content = new Properties();
        content.setProperty("test", "val");
        ConfigFile configFile = new ConfigFile(file, "test", false);
        configFile.setContent(content);
        assertEquals(content, configFile.getContent());
    }

    @Test
    void setAttributeOrRemoveOnNull() {
        assertDoesNotThrow(() -> configFile.setAttributeOrRemoveOnNull("attr", "val"));
        assertEquals("val", configFile.getAttribute("attr"));
        assertTrue(configFile.hasAttribute("attr"));
        assertDoesNotThrow(() -> configFile.setAttributeOrRemoveOnNull("attr", null));
        assertNull(configFile.getAttribute("attr"));
        assertFalse(configFile.hasAttribute("attr"));
    }
}
