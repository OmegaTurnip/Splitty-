package client.utils;

import java.io.*;
import java.util.Properties;

public class ConfigFile {

    private final File file;
    private Properties content;
    private final boolean doAutoFlush;


    /**
     * Creates an object containing a Properties file.
     *
     * @param   file
     *          The path to the file.
     *
     * @throws  IOException
     *          If an I/O error occurs reading from the file.
     */
    public ConfigFile(File file) throws IOException {
        this(file, new Properties(), true);
    }

    /**
     * Creates an object class containing a Properties file.
     *
     * @param   file
     *          The path to the file.
     * @param   doAutoFlush
     *          Whether a change to the content should immediately be flushed or
     *          not.
     *
     * @throws  IOException
     *          If an I/O error occurs reading from the file.
     */
    public ConfigFile(File file, boolean doAutoFlush) throws IOException {
        this(file, new Properties(), doAutoFlush);
    }

    private ConfigFile(File file, Properties content, boolean doAutoFlush)
            throws IOException {
        this.file = file;
        this.content = content;
        this.doAutoFlush = doAutoFlush;

        read();
    }

    /**
     * Gets the file content.
     *
     * @return  The file content.
     */
    public Properties getContent() throws IOException {
        return content;
    }

    /**
     * Sets the file content. If {@link #doAutoFlush doAutoFlush} is set, this
     * setter will also immediately flush the content to the file on disk.
     *
     * @param   content
     *          The updated content of the file.
     *
     * @throws  IOException
     *          If an I/O error occurs writing to or creating the file in which
     *          the configuration is stored.
     */
    public void setContent(Properties content) throws IOException {
        this.content = content;
        if (doAutoFlush) flush();
    }

    /**
     * Gets an attribute.
     *
     * @param   key
     *          The attribute name.
     *
     * @return  The attribute or null if the attribute is not set.
     */
    public String getAttribute(String key) {
        return content.getProperty(key);
    }

    /**
     * Sets an attribute.
     *
     * @param   key
     *          The attribute name.
     * @param   value
     *          The attribute value.
     *
     * @throws  IOException
     *          If an I/O error occurs writing to or creating the file in which
     *          the configuration is stored.
     */
    public void setAttribute(String key, String value) throws IOException {
        content.setProperty(key, value);
        if (doAutoFlush) flush();
    }

    /**
     * Flushes the file to disk.
     *
     * @throws  IOException
     *          If an I/O error occurs writing to or creating the file.
     */
    public void flush() throws IOException {
        FileOutputStream fol = new FileOutputStream(file);
        content.store(fol, "Client settings");
        fol.close();
    }

    /**
     * Reads the file from disk.
     *
     * @throws  IOException
     *          If an I/O error occurs reading from the file.
     */
    public void read() throws IOException {
        file.createNewFile();
        FileInputStream fil = new FileInputStream(file);
        content.load(fil);
        fil.close();
    }
}
