package com.rentalsystem.config;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Configuration class for loading and accessing application settings.
 */
public class AppConfig {
    private static final String CONFIG_FILE = "resources/assets/config/settings.xml";
    private static Properties properties;


    // Static initializer to load properties from XML file
    static {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.loadFromXML(fis);
        } catch (IOException e) {
            e.printStackTrace();
            // Consider logging this error or throwing a runtime exception
        }
    }


    /**
     * Retrieves a property value as a String.
     * @param key The key of the property to retrieve
     * @return The value of the property, or null if not found
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }


    /**
     * Retrieves a property value as an integer.
     * @param key The key of the property to retrieve
     * @return The integer value of the property
     * @throws NumberFormatException if the property value is not a valid integer
     */
    public static int getIntProperty(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }


    /**
     * Retrieves a property value as a boolean.
     * @param key The key of the property to retrieve
     * @return The boolean value of the property
     */
    public static boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }
}
