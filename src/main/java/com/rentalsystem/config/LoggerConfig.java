/**
 * @author <Nguyen Ngoc Dung - s3978535>
 */

package com.rentalsystem.config;

import java.io.IOException;
import java.util.logging.*;


/**
 * Configuration class for setting up the application logger.
 */
public class LoggerConfig {
    private static final Logger LOGGER = Logger.getLogger(LoggerConfig.class.getName());


    // Static initializer to set up the logger
    static {
        try {
            FileHandler fileHandler = new FileHandler("rental_system.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.severe("Failed to initialize logger: " + e.getMessage());
            // Consider throwing a runtime exception here
        }
    }


    /**
     * Retrieves the configured logger instance.
     * @return The logger instance
     */
    public static Logger getLogger() {
        return LOGGER;
    }
}
