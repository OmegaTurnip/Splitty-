package client.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class PropertiesFile {

    protected final File file;
    protected Properties content;


    /**
     * Creates an object class containing a readonly Properties file.
     *
     * @param   file
     *          The path to the file.
     *
     * @throws  IOException
     *          If an I/O error occurs reading from the file.
     */
    public PropertiesFile(File file) throws IOException {
        this(file, new Properties());
    }

    PropertiesFile(File file, Properties content) throws IOException {
        this.file = file;
        this.content = content;

        read();
    }

    /**
     * Gets the file content.
     *
     * @return  The file content.
     */
    public Properties getContent() {
        return content;
    }

    /**
     * Gets an attribute. It will return {@code null} if the attribute is not
     * set.
     *
     * @param   key
     *          The attribute name.
     *
     * @return  The attribute or {@code null} if the attribute is not set.
     */
    public String getAttribute(String key) {
        return content.getProperty(key);
    }


    /**
     * Checks if an attribute is present.
     *
     * @param   key
     *          The attribute name.
     *
     * @return  whether the attribute is present.
     */
    public boolean hasAttribute(String key) {
        return content.containsKey(key);
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
