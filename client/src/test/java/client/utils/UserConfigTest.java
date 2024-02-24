package client.utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class UserConfigTest {

    private UserConfig settingsShadow;
    private ConfigFile configFile;
    private static final File file = new File("unittest.properties");
    private static final File file2 = new File(UserConfig.PATHNAME);


    @BeforeEach
    public void setup() throws IOException {
        file.delete();
        configFile = new ConfigFile(file, "test");
        settingsShadow = new UserConfig(configFile);
    }

    @AfterAll
    public static void cleanup() {
        UserConfigTest.file.delete();
        UserConfigTest.file2.delete();  // remove faulty config file due to
                                        // relative import
    }

    @Test
    public void testDefaults() {
        assertEquals("http://localhost:8080/", settingsShadow.getServerUrl());
    }

    @Test
    public void testOverwriting() throws IOException {
        assertEquals("http://localhost:8080/", settingsShadow.getServerUrl());
        settingsShadow.setServerUrl("http://randomurl:4040/");
        assertEquals("http://randomurl:4040/", settingsShadow.getServerUrl());
    }

}
