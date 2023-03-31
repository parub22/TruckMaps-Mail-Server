package au.com.truckmaps.mail.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Parbati Budhathoki
 * @Created On Mar 24, 2023 10:48:49 AM
 */
public class AppConfiguration {

    private static final Logger LOG = LogManager.getLogger(AppConfiguration.class.getSimpleName());
    private static final String CONFIG_FILE = "etc/config.properties";
    private static Properties prop;

    /**
     * load app server configuration file. This method is called by other public
     * methods to load the configuration if it has not been loaded.
     */
    private static void loadConfiguration() {
        try ( InputStream input = new FileInputStream(CONFIG_FILE)) {
            // load a properties file
            prop = new Properties();
            prop.load(input);

        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        }

    }

    /**
     * return a subset (section) of the app server configuration. Each section
     * is defined by a prefix in the key. For example in db_name key the section
     * prefix is db_
     *
     * @param section
     * @return properties
     */
    public static Properties getSectionProperties(String section) {
        if (prop == null || prop.isEmpty()) {
            loadConfiguration();
        }

        final Properties p = new Properties();
        prop.forEach((key, value) -> {
            if (key.toString().contains(section)) {
                p.put(key, value);
            }
        });
        return p;
    }

    /**
     * Get configuration value of a specific key in app configuration
     *
     * @param key
     * @return value
     */
    public static String getProperty(String key) {
        if (prop == null || prop.isEmpty()) {
            loadConfiguration();
        }

        return prop.getProperty(key);
    }
}
