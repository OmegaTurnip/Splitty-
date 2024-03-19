package client.utils;

import java.io.*;
import java.util.Properties;

public class ConfigFile extends PropertiesFile {

    private final String description;
    private final boolean doAutoFlush;


    /**
     * Creates an object containing a Properties file with writing capabilities.
     * 
     * @param   file
     *          The path to the file.
     * @param   description
     *          A description that will be stored on top of the config file.
     *
     * @throws  IOException
     *          If an I/O error occurs reading from the file.
     */
    public ConfigFile(File file, String description) throws IOException {
        this(file, new Properties(), description, true);
    }

    /**
     * Creates an object containing a Properties file with writing capabilities.
     *
     * @param   file
     *          The path to the file.
     * @param   description
     *          A description that will be stored on top of the config file.
     * @param   doAutoFlush
     *          Whether a change to the content should immediately be flushed or
     *          not.
     *
     * @throws  IOException
     *          If an I/O error occurs reading from the file.
     */
    public ConfigFile(File file, String description, boolean doAutoFlush)
            throws IOException {
        this(file, new Properties(), description, doAutoFlush);
    }


    ConfigFile(File file, Properties content, String description,
               boolean doAutoFlush) throws IOException {
        super(file, content);
        this.description = description;
        this.doAutoFlush = doAutoFlush;
    }

    /**
     * Sets the file content. If {@link #doAutoFlush} is set, this
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
     * Sets an attribute. Doesn't allow the value to be {@code null}. If
     * {@link #doAutoFlush} is set, this setter will also immediately flush the
     * change to the file on disk.
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
     * Sets an attribute if the value is not {@code null}, deletes it otherwise.
     * If {@link #doAutoFlush} is set, this function will also immediately flush
     * the change to the file on disk.
     *
     * @param   key
     *          The attribute name.
     * @param   value
     *          The attribute value or {@code null} to delete the attribute.
     *
     * @throws  IOException
     *          If an I/O error occurs writing to or creating the file in which
     *          the configuration is stored.
     */
    public void setAttributeOrRemoveOnNull(String key, String value)
            throws IOException {
        if (value == null)
            removeAttribute(key);
        else
            setAttribute(key, value);
    }

    /**
     * Removes an attribute. If {@link #doAutoFlush} is set, this
     * function will also immediately flush the change to the file on disk.
     *
     * @param   key
     *          The attribute name.
     *
     * @throws  IOException
     *          If an I/O error occurs writing to or creating the file in which
     *          the configuration is stored.
     */
    public void removeAttribute(String key) throws IOException {
        content.remove(key);
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
        content.store(fol, this.description);
        fol.close();
    }
}
