package com.ktc.rabbitmq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesRMQConfiguration implements RMQConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesRMQConfiguration.class);

  public static final String ENV_PROPERTIES_PATH = "RMQ_CONFIG";
  public static final String PROPERTIES_CLASSPATH_PREFIX = "classpath:";
  public static final String DEFAULT_PROPERTIES_PATH = PROPERTIES_CLASSPATH_PREFIX + "/rabbitmq.properties";

  public static final String PROPERTY_URL = "com.ktc.rabbitmq.url";
  public static final String PROPERTY_QUEUE = "com.ktc.rabbitmq.queue";

  protected Properties properties = null;
  protected String path = null;

  public PropertiesRMQConfiguration() {
  }

  public PropertiesRMQConfiguration(String path) {
    this.path = path;
  }

  @Override
  public String getUrl() {
    return getProperties().getProperty(PROPERTY_URL);
  }


  @Override
  public String getQueue() {
    return getProperties().getProperty(PROPERTY_QUEUE);
  }

  @Override
  public Properties getProperties() {
    if (properties == null) {
      properties = loadProperties();
    }
    return properties;
  }

  protected Properties loadProperties() {
    Properties properties = new Properties();
    String path = getPropertiesPath();

    try {
      InputStream inputStream = getProperiesAsStream(path);
      if (inputStream != null) {
        properties.load(inputStream);
        return properties;

      } else {
        throw new IllegalStateException("Cannot find RMQ configuration at: " + path);
      }

    } catch (IOException e) {
      throw new IllegalStateException("Unable to load RMQ configuration from: " + path, e);
    }
  }

  protected String getPropertiesPath() {
    return Optional.ofNullable(path).orElseGet(() ->
      Optional.ofNullable(System.getenv(ENV_PROPERTIES_PATH))
        .orElse(DEFAULT_PROPERTIES_PATH));
  }

  protected InputStream getProperiesAsStream(String path) throws FileNotFoundException {
    if (path.startsWith(PROPERTIES_CLASSPATH_PREFIX)) {
      String pathWithoutPrefix = path.substring(PROPERTIES_CLASSPATH_PREFIX.length());
      LOGGER.debug("load RMQ properties from classpath '{}'", pathWithoutPrefix);
      return getClass().getResourceAsStream(pathWithoutPrefix);
    } else {
      Path config = Paths.get(path);
      LOGGER.debug("load RMQ properties from path '{}'", config.toAbsolutePath());
      File file = config.toFile();
      if (file.exists()) {
        return new FileInputStream(file);
      } else {
        return null;
      }
    }
  }
}
