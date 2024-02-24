package client.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A class to store user settings, immediately storing them on disk.
 */
public class UserConfig {

    public static final UserConfig USER_SETTINGS;
    static final String PATHNAME = "client_settings.properties";

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
    private String userLanguage;  // key: "userLanguage"

    // when adding new attributes, don't forget to:
    // add getters, setters & update read()!



    /**
     * Creates an empty {@code UserConfig} object.
     *
     * @throws  IOException
     *          If an I/O error occurs writing to or creating the file in which
     *          the configuration is stored.
     */
    UserConfig() throws IOException {
        this(new ConfigFile(new File(PATHNAME), "Client settings"));
    }

    /**
     * Creates an empty {@code UserConfig} object.
     *
     * @param   configFile
     *          The config file.
     *
     * @throws  IOException
     *          If an I/O error occurs writing to or creating the file in which
     *          the configuration is stored.
     */
    UserConfig(ConfigFile configFile) throws IOException {
        this.configFile = configFile;
        read();
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
     * Gets the selected user language as an ISO 639-3 code.
     *
     * @return  the selected user language as an ISO 639-3 code.
     */
    public String getUserLanguage() {
        return userLanguage;
    }

    /**
     * Changes the language that should be used in the UI.
     *
     * @param   userLanguage
     *          the new user language as an ISO 639-3 code.
     *
     * @throws  IOException
     *          If an I/O error occurs writing to or creating the file in which
     *          the configuration is stored.
     */
    public void setUserLanguage(String userLanguage) throws IOException {
        this.userLanguage = userLanguage;
        configFile.setAttribute("userLanguage", userLanguage);
    }

    /**
     * Reads the settings from the properties file.
     *
     * @throws  IOException
     *          If an I/O error occurs writing to or creating the file in which
     *          the configuration is stored.
     */
    public void read() throws IOException {
        Properties properties;
        try {
            properties = configFile.getContent();
        } catch (IOException e) {
            properties = new Properties();
        }

        setServerUrl(
                properties.getProperty("serverUrl", "http://localhost:8080/"));

        setUserLanguage(properties.getProperty("userLanguage", "eng"));

    }

    /**
     * Converts a {@code String} to a {@code double}. Use this function to
     * prevent accidentally converting the {@code double} using a locale.
     *
     * @param   data
     *          The {@code String} to be converted.
     *
     * @return  The converted {@code double}.
     */
    private static double toDouble(String data) {
        return Double.parseDouble(data);
    }

    /**
     * Converts a {@code double} to a {@code String}. Use this function to
     * prevent accidentally converting the {@code String} using a locale.
     *
     * @param   data
     *          The {@code double} to be converted.
     *
     * @return  The converted {@code String}.
     */
    private static String fromDouble(double data) {
        return Double.toString(data);
    }

    /**
     * Converts an {@code String}, formatted to be used as an array, to an
     * actual array. Defaults to using {@code ","} as the separator.
     *
     * @param   string
     *          The {@code String} that needs to be converted to an array.
     *
     * @return  The resulting array.
     */
    private static String[] toArray(String string) {
        return toArray(string, ",");
    }

    /**
     * Converts an {@code String} formatted to be used as an array to an actual
     * array.
     *
     * @param   string
     *          The {@code String} that needs to be converted to an array.
     * @param   sep
     *          The separator to split the array on.
     *
     * @return  The resulting array.
     */
    private static String[] toArray(String string, String sep) {
        return string.split(sep);
    }

    /**
     * Converts an array to a format to be used in combination with Properties.
     * Defaults to using {@code ","} as the separator.
     * @param   array
     *          The array that should be converted.
     *
     * @return  The resulting {@code String}.
     *
     * @throws  IllegalArgumentException
     *          If a {@code String} in the array contains {@code ","}.
     */
    private static String fromArray(String[] array) {
        return fromArray(array, ",");
    }

    /**
     * Converts an array to a format to be used in combination with Properties.
     *
     * @param   array
     *          The array that should be converted.
     * @param   sep
     *          The separator to be used between array entries.
     *
     * @return  The resulting {@code String}.
     *
     * @throws  IllegalArgumentException
     *          If sep is a substring of a {@code String} in the array.
     */
    private static String fromArray(String[] array, String sep) {
        for (String s : array) {
            if (s.contains(sep)) throw new
                    IllegalArgumentException("Array contains sep value!");
        }
        return String.join(sep, array);
    }

    /**
     * Converts an {@code String} formatted to be used as an {@code HashMap} to
     * an actual {@code HashMap}. Defaults to using {@code ","} as the key value
     * separator and {@code ";"} as the entry separator.
     *
     * @param   hashMap
     *          The {@code String} to be converted to an {@code HashMap}.
     *
     * @return  The resulting {@code HashMap}.
     *
     * @throws  IllegalArgumentException
     *          If an entry doesn't have exactly 2 attributes.
     */
    private static HashMap<String, String> toHashMap(String hashMap) {
        return toHashMap(hashMap, ";", ",");
    }

    /**
     * Converts an {@code String} formatted to be used as an {@code HashMap} to
     * an actual {@code HashMap}.
     *
     * @param   hashMap
     *          The {@code String} to be converted to an {@code HashMap}.
     * @param   entrySep
     *          The separator used between map entries.
     * @param   keyValSep
     *          The separator used between the key and value in an entry.
     *
     * @return  The resulting {@code HashMap}.
     *
     * @throws  IllegalArgumentException
     *          If {@code entrySep} contains {@code keyValSep} or
     *          {@code keyValSep} contains {@code entrySep} or if an entry
     *          doesn't have exactly 2 attributes.
     */
    private static HashMap<String, String> toHashMap(
            String hashMap, String entrySep, String keyValSep) {

        if (entrySep.contains(keyValSep) || keyValSep.contains(entrySep))
            throw new IllegalArgumentException("entrySep and keyValSep " +
                    "aren't distinct enough!");

        HashMap<String, String> result = new HashMap<>();
        String[] entries = hashMap.split(entrySep);

        for (String entry : entries) {
            String[] keyVal = entry.split(keyValSep);
            if (keyVal.length != 2) throw new IllegalArgumentException(
                    "Amount of attributes in entry is not equal to 2!");
            result.put(keyVal[0], keyVal[1]);
        }

        return result;
    }

    /**
     * Converts an {@code HashMap} to a format to be used in combination with
     * Properties. Defaults to using {@code ","} as the key value
     * separator and {@code ";"} as the entry separator.
     *
     * @param   hashMap
     *          The {@code HashMap} to be converted.
     *
     * @return  The resulting {@code String}.
     *
     * @throws  IllegalArgumentException
     *          If an entry contains {@code ","} or {@code ";"}.
     */
    private static String fromHashMap(HashMap<String, String> hashMap) {
        return fromHashMap(hashMap, ";", ",");
    }

    /**
     * Converts an {@code HashMap} to a format to be used in combination with
     * Properties.
     *
     * @param   hashMap
     *          The {@code HashMap} to be converted.
     * @param   entrySep
     *          The separator to be used between map entries.
     * @param   keyValSep
     *          The separator to be used between the key and value in an entry.
     *
     * @return  The resulting {@code String}.
     *
     * @throws  IllegalArgumentException
     *          If {@code entrySep} contains {@code keyValSep} or
     *          {@code keyValSep} contains {@code entrySep} or if an entry
     *          contains a separator value.
     */
    private static String fromHashMap(HashMap<String, String> hashMap,
                               String entrySep, String keyValSep) {

        if (entrySep.contains(keyValSep) || keyValSep.contains(entrySep))
            throw new IllegalArgumentException("entrySep and keyValSep " +
                    "aren't distinct enough!");

        String[] entries = new String[hashMap.size()];
        int entriesIdx = 0;

        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            boolean isIllegal =
                    key.contains(entrySep) || key.contains(keyValSep)
                    || value.contains(entrySep) || value.contains(keyValSep);

            if (isIllegal) throw new IllegalArgumentException(
                    "Hashmap contains separator values!");

            entries[entriesIdx++] = key + keyValSep + value;
        }

        return String.join(entrySep, entries);
    }
}
