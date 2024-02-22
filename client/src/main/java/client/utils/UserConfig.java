package client.utils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * A class to store user settings, immediately storing them on disk.
 */
public class UserConfig {

    public static final UserConfig USER_SETTINGS;

    static {
        try {
            USER_SETTINGS = new UserConfig();
        } catch (IOException e) {
            throw new RuntimeException("Settings file couldn't be open with " +
                    "the following error message: " + e.getMessage());
        }
    }

    private final ConfigFile configFile;

    private String serverUrl;  // key: "serverUrl"

    // when adding new attributes, don't forget to:
    // add getters, setters, update getDefault() & read()!



    /**
     * Creates an empty {@code UserConfig} object.
     */
    private UserConfig() throws IOException {
        configFile = new ConfigFile(new File("clientsettings.properties"));
        read();
    }

    /**
     * Creates an empty {@code UserConfig} object.
     *
     * @param   configFile
     *          The config file.
     */
    private UserConfig(ConfigFile configFile) {
        this.configFile = configFile;
    }


    /**
     * Gets the url used to connect to the server.
     *
     * @return  the url used to connect to the server.
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * Changes the url that should be used to connect to the server in the
     * config file.
     *
     * @param   serverUrl
     *          The new server URL.
     *
     * @throws  IOException
     *          If an I/O error occurs writing to or creating the file in which
     *          the configuration is stored.
     */
    public void setServerUrl(String serverUrl) throws IOException {
        this.serverUrl = serverUrl;
        configFile.setAttribute("serverUrl", serverUrl);
    }


    /**
     * Reads the settings from the properties file.
     *
     * @throws  IOException
     *          If an I/O error occurs writing to or creating the file in which
     *          the configuration is stored.
     */
    public void read() throws IOException {
        UserConfig defaultConfig = UserConfig.getDefault();

        Properties properties;
        try {
            properties = configFile.getContent();
        } catch (IOException e) {
            properties = new Properties();
        }

        setServerUrl(
                properties.getProperty("serverUrl", defaultConfig.serverUrl));

    }


    /**
     * Generates a default {@code UserConfig} object for the case that the file
     * or a specific setting is missing.
     *
     * @return a default {@code UserConfig} object.
     */
    private static UserConfig getDefault() {
        UserConfig defaultConfig = new UserConfig(null);

        defaultConfig.serverUrl = "http://localhost:8080/";

        return defaultConfig;
    }
}
