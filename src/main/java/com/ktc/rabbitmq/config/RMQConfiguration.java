package com.ktc.rabbitmq;

import java.time.Duration;
import java.util.Properties;

public interface RMQConfiguration {

  String getUrl();
  String getQueue();
  Properties getProperties();

}