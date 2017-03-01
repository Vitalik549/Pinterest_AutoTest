package com.pinterest.uk.helpers;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public abstract class AbstractPropertiesHandler {

    private static final Logger LOGGER = Logger.getLogger(AbstractPropertiesHandler.class);
    private Properties properties = new Properties();
    private static final String ENVIRONMENT = Optional.ofNullable(System.getProperty("testEnv")).orElse("default");

    public AbstractPropertiesHandler(String classPath) {
        try {
            LOGGER.debug("Trying to load properties from file " + classPath);
            properties.load(getClass().getClassLoader().getResourceAsStream(classPath));

        } catch (IOException e) {
            final String msg = "Something went wrong while trying to load properties from: " + classPath;
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        LOGGER.info("Loaded properties from location: " + classPath);
    }

    public String getProperty(String propertyNameFromDefaultEnv) {
        return getProperty(ENVIRONMENT, propertyNameFromDefaultEnv);
    }

    public String getProperty(String environment, String property) {
        String propertyKey = environment + "." + property;
        LOGGER.debug("Getting property with key: " + propertyKey);
        if (!properties.containsKey(propertyKey)) {
            return null;
        }
        if (properties.getProperty(propertyKey).equals("")) {
            return null;
        }
        return properties.getProperty(propertyKey);
    }
}
