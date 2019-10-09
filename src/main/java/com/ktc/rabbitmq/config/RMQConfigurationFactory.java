package com.ktc.rabbitmq;

public class RMQConfigurationFactory {

  private static RMQConfiguration INSTANCE;

  public static RMQConfiguration getConfiguration() {
    if (INSTANCE == null) {
      INSTANCE = new PropertiesRMQConfiguration();
    }
    return INSTANCE;
  }

  public static void setConfiguration(RMQConfiguration configuration) {
    INSTANCE = configuration;
  }
}
