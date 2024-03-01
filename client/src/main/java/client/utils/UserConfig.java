package client.utils;

import client.language.Language;
import client.language.Translator;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * A class to store user settings, immediately storing them on disk.
 */
public class UserConfig {

    public static final UserConfig USER_SETTINGS;
    static final String PATHNAME = "client_settings.properties";

    static {
        UserConfig userSettings1;
        try {
            userSettings1 = new UserConfig(
                    new ConfigFile(new File(PATHNAME), "Client settings")
            );
        } catch (IOException e) {
            userSettings1 = null;
        }
        USER_SETTINGS = userSettings1;
    }

    private final ConfigFile configFile;

    // key: "serverUrl"
    private String serverUrl;

    // key: "userLanguage"
    private String userLanguage;

    // key: "languages"
    private HashMap<String, File> availableLanguages;

    // key: "events"
    private List<String> eventCodes;

    // When adding new attributes, don't forget to:
    //  - add getters, setters
    //  - update read()
    //  - update the attribute only after writing to the file to prevent desyncs
    //    in the case an IOException is thrown.


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
        if (serverUrl == null)
            throw new IllegalArgumentException("serverUrl is null");

        configFile.setAttribute("serverUrl", serverUrl);
        this.serverUrl = serverUrl;
    }

    /**
     * Gets the selected language as a
     * <a href="https://iso639-3.sil.org/code_tables/639/data">ISO 639-3</a>
     * code.
     *
     * @return  The ISO 639-3 code.
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
        if (!Language.languages.containsKey(userLanguage))
            throw new IllegalArgumentException("Language doesn't exist!");

        configFile.setAttribute("userLanguage", userLanguage);
        this.userLanguage = userLanguage;
        Translator.setCurrentLanguage(Language.languages.get(userLanguage));
    }

    /**
     * Gets a {@code HashMap} containing all available languages. The key is
     * the language
     * <a href="https://iso639-3.sil.org/code_tables/639/data">ISO 639-3</a>
     * code, the value is the file in which the translations are stored. Any
     * changes made to this {@code HashMap} are not reflected in this class but
     * should be stored using the
     * {@link UserConfig#setAvailableLanguages(HashMap)} method.
     *
     * @return  A {@code HashMap} containing all available languages.
     */
    public HashMap<String, File> getAvailableLanguages() {
        return new HashMap<>(availableLanguages);
    }

    /**
     * Sets all available languages using a {@code HashMap}. The key is the
     * language
     * <a href="https://iso639-3.sil.org/code_tables/639/data">ISO 639-3</a>
     * code, the value is the file in which the translations are stored.
     *
     * @param   availableLanguages
     *          A {@code HashMap} containing all available languages.
     *
     * @throws  IOException
     *          If an I/O error occurs writing to or creating the file in which
     *          the configuration is stored.
     */
    public void setAvailableLanguages(HashMap<String, File> availableLanguages)
            throws IOException {

        if (availableLanguages == null)
            throw new IllegalArgumentException("availableLanguages is null!");

        configFile.setAttribute("languages",
                fromHashMap(availableLanguages, File::getAbsolutePath));
        this.availableLanguages = availableLanguages;
    }

    /**
     * Gets the event codes entered by this user as a {@code List}. Any changes
     * made to this {@code List<String>} are not reflected in this class but
     * should be stored using the {@link UserConfig#setEventCodes(List)} method.
     *
     * @return  A {@code List} containing all event codes.
     */
    public List<String> getEventCodes() {
        return new ArrayList<>(eventCodes);
    }

    private void setEventCodes(String[] eventCodes) throws IOException {
        setEventCodes(Arrays.asList(eventCodes));
    }

    /**
     * Sets the event codes entered by this user as a {@code List}.
     *
     * @param   eventCodes
     *          A {@code List} containing all event codes.
     *
     * @throws  IOException
     *          If an I/O error occurs writing to or creating the file in which
     *          the configuration is stored.
     */
    public void setEventCodes(List<String> eventCodes) throws IOException {
        if (eventCodes == null)
            throw new IllegalArgumentException("eventCodes is null!");

        ArrayList<String> eventCodesArrayList = new ArrayList<>(eventCodes);

        // An empty array is stored by not having the property exist in the
        // first place (as having the property empty would be read as [""]).
        if (eventCodes.isEmpty())
            configFile.removeAttribute("events");
        else
            configFile.setAttribute("events", fromArray(
                    eventCodesArrayList.toArray(new String[0])
            ));

        this.eventCodes = eventCodesArrayList;
    }


    /**
     * Reads the settings from the properties file.
     *
     * @throws  IOException
     *          If an I/O error occurs writing to or creating the file in which
     *          the configuration is stored.
     */
    public void read() throws IOException {
        Properties properties = configFile.getContent();

        readServerUrl(properties);
        readAvailableLanguages(properties);
        readUserLanguage(properties);
        readEventCodes(properties);
    }

    private void readServerUrl(Properties properties) throws IOException {
        setServerUrl(
                properties.getProperty("serverUrl", "http://localhost:8080/"));
    }

    private void readAvailableLanguages(Properties properties)
            throws IOException {
        HashMap<String, File> availableLanguages = toHashMap(
                properties.getProperty("languages",
                        "eng,includedLanguages/eng.properties;" +
                                "nld,includedLanguages/nld.properties;" +
                                "deu,includedLanguages/deu.properties"
                ), File::new
        );

        // load all languages to the language set in the Language class
        for (Map.Entry<String, File> language : availableLanguages.entrySet()) {
            Language.fromLanguageFile(language.getKey(), language.getValue());
        }

        setAvailableLanguages(availableLanguages);
    }

    private void readUserLanguage(Properties properties) throws IOException {
        setUserLanguage(properties.getProperty("userLanguage", "eng"));
    }

    private void readEventCodes(Properties properties) throws IOException {
        String events = properties.getProperty("events");
        setEventCodes(events != null ? toArray(events) : new String[0]);
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
    static double toDouble(String data) {
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
    static String fromDouble(double data) {
        return Double.toString(data);
    }

    /**
     * Converts an {@code String}, formatted to be used as an array, to an
     * actual array. Defaults to using {@code ','} as the separator.
     *
     * @param   string
     *          The {@code String} that needs to be converted to an array.
     *
     * @return  The resulting array.
     */
    static String[] toArray(String string) {
        return toArray(string, ',');
    }

    /**
     * Converts an {@code String} formatted to be used as an array to an actual
     * array.
     *
     * @param   string
     *          The {@code String} that needs to be converted to an array.
     * @param   separator
     *          The separator to split the array on.
     *
     * @return  The resulting array.
     */
    static String[] toArray(String string, char separator) {
        return string.split(Pattern.quote(String.valueOf(separator)));
    }

    /**
     * Converts an array to a format to be used in combination with Properties.
     * Defaults to using {@code ','} as the separator.
     * @param   array
     *          The array that should be converted.
     *
     * @return  The resulting {@code String}.
     *
     * @throws  IllegalArgumentException
     *          If a {@code String} in the array contains {@code ','}.
     */
    static String fromArray(String[] array) {
        return fromArray(array, ',');
    }

    /**
     * Converts an array to a format to be used in combination with Properties.
     *
     * @param   array
     *          The array that should be converted.
     * @param   separator
     *          The separator to be used between array entries.
     *
     * @return  The resulting {@code String}.
     *
     * @throws  IllegalArgumentException
     *          If a {@code String} in the array contains the separator.
     */
    static String fromArray(String[] array, char separator) {
        String sep = String.valueOf(separator);
        for (String s : array) {
            if (s.contains(sep)) throw new
                    IllegalArgumentException("Array contains separator value!");
        }
        return String.join(sep, array);
    }

    /**
     * Converts an {@code String} formatted to be used as an {@code HashMap} to
     * an actual {@code HashMap}. Defaults to using {@code ','} as the key value
     * separator and {@code ';'} as the entry separator.
     *
     * @param   hashMap
     *          The {@code String} to be converted to an {@code HashMap}.
     *
     * @return  The resulting {@code HashMap}.
     *
     * @throws  IllegalArgumentException
     *          If an entry doesn't have exactly 2 attributes.
     */
    static HashMap<String, String> toHashMap(String hashMap) {
        return toHashMap(hashMap, ',', ';', (String s) -> s);
    }

    /**
     * Converts an {@code String} formatted to be used as an {@code HashMap} to
     * an actual {@code HashMap}. Defaults to using {@code ','} as the key value
     * separator and {@code ';'} as the entry separator.
     *
     * @param   hashMap
     *          The {@code String} to be converted to an {@code HashMap}.
     * @param   stringConverter
     *          The function that converts the value of type {@code String} to a
     *          value of type {@code T}.
     * @param   <T>
     *          The value type of the resulting {@code HashMap}.
     *
     * @return  The resulting {@code HashMap}.
     *
     * @throws  IllegalArgumentException
     *          If an entry doesn't have exactly 2 attributes.
     */
    static <T> HashMap<String, T> toHashMap(
            String hashMap, Function<String, T> stringConverter) {
        return toHashMap(hashMap, ',', ';', stringConverter);
    }

    /**
     * Converts an {@code String} formatted to be used as an {@code HashMap} to
     * an actual {@code HashMap}.
     *
     * @param   hashMap
     *          The {@code String} to be converted to an {@code HashMap}.
     * @param   entrySeparator
     *          The separator used between map entries.
     * @param   keyValueSeparator
     *          The separator used between the key and value in an entry.
     * @param   stringConverter
     *          The function that converts the value of type {@code String} to a
     *          value of type {@code T}.
     * @param   <T>
     *          The value type of the resulting {@code HashMap}.
     *
     * @return  The resulting {@code HashMap}.
     *
     * @throws  IllegalArgumentException
     *          If {@code entrySeparator} is equal to {@code keyValueSeparator}
     *          or if an entry doesn't have exactly 2 attributes.
     */
    static <T> HashMap<String, T> toHashMap(
            String hashMap, char keyValueSeparator, char entrySeparator,
            Function<String, T> stringConverter) {

        String entrySep = String.valueOf(entrySeparator);
        String keyValSep = String.valueOf(keyValueSeparator);

        // This check is intentionally not mentioned in the javadoc.
        if (stringConverter == null)
            throw new IllegalArgumentException("stringConverter is null!");

        if (entrySep.contains(keyValSep) || keyValSep.contains(entrySep))
            throw new IllegalArgumentException("entrySep and keyValSep " +
                    "aren't distinct!");

        HashMap<String, T> result = new HashMap<>();
        if (hashMap == null || hashMap.isEmpty())
            return result;

        String[] entries = hashMap.split(Pattern.quote(entrySep));

        for (String entry : entries) {
            String[] keyVal = entry.split(Pattern.quote(keyValSep));
            if (keyVal.length != 2)
                throw new IllegalArgumentException(
                    "Amount of attributes in entry is not equal to 2!"
                );
            result.put(keyVal[0], stringConverter.apply(keyVal[1]));
        }

        return result;
    }


    /**
     * Converts an {@code HashMap} to a format to be used in combination with
     * Properties. Defaults to using {@code ','} as the key value separator
     * and {@code ';'} as the entry separator.
     *
     * @param   hashMap
     *          The {@code HashMap} to be converted.
     *
     * @return  The resulting {@code String}.
     *
     * @throws  IllegalArgumentException
     *          If an entry contains {@code ','} or {@code ';'}.
     */
    static String fromHashMap(HashMap<String, String> hashMap) {
        return fromHashMap(hashMap, ',', ';', (String s) -> s);
    }

    /**
     * Converts an {@code HashMap} to a format to be used in combination with
     * Properties. Defaults to using {@code ','} as the key value separator
     * and {@code ';'} as the entry separator.
     *
     * @param   hashMap
     *          The {@code HashMap} to be converted.
     * @param   stringConverter
     *          The function that converts the value of type {@code T} to a
     *          value of type {@code String}.
     * @param   <T>
     *          The value type of the given {@code HashMap}.
     *
     * @return  The resulting {@code String}.
     *
     * @throws  IllegalArgumentException
     *          If an entry contains {@code ','} or {@code ';'}.
     */
    static <T> String fromHashMap(HashMap<String, T> hashMap,
                                      Function<T, String> stringConverter) {
        return fromHashMap(hashMap, ',', ';', stringConverter);
    }


    /**
     * Converts an {@code HashMap} to a format to be used in combination with
     * Properties. Returns {@code null} if the {@code HashMap} is empty.
     *
     * @param   hashMap
     *          The {@code HashMap} to be converted.
     * @param   entrySeparator
     *          The separator to be used between map entries.
     * @param   keyValueSeparator
     *          The separator to be used between the key and value in an entry.
     * @param   stringConverter
     *          The function that converts the value of type {@code T} to a
     *          value of type {@code String}.
     * @param   <T>
     *          The value type of the given {@code HashMap}.
     *
     * @return  The resulting {@code String}.
     *
     * @throws  IllegalArgumentException
     *          If {@code entrySeparator} is equal to {@code keyValueSeparator}
     *          or if an entry contains a separator value.
     */
    static <T> String fromHashMap(HashMap<String, T> hashMap,
            char keyValueSeparator, char entrySeparator,
            Function<T, String> stringConverter) {

        String entrySep = String.valueOf(entrySeparator);
        String keyValSep = String.valueOf(keyValueSeparator);

        // This check is intentionally not mentioned in the javadoc.
        if (stringConverter == null)
            throw new IllegalArgumentException("stringConverter is null!");

        if (entrySep.equals(keyValSep))
            throw new IllegalArgumentException(
                    "entrySeparator and keyValSeparator are equal!"
            );
        if (hashMap == null || hashMap.isEmpty()) return null;
        String[] entries = new String[hashMap.size()];
        int entriesIdx = 0;

        for (Map.Entry<String, T> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            String value = stringConverter.apply(entry.getValue());

            if (key.contains(entrySep) || key.contains(keyValSep)
                || value.contains(entrySep) || value.contains(keyValSep))
                throw new IllegalArgumentException("Hashmap contains " +
                        "separator values!"
                );
            entries[entriesIdx++] = key + keyValSep + value;
        }
        return String.join(entrySep, entries);
    }
}
