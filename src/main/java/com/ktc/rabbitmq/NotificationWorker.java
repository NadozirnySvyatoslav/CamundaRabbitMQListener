package com.ktc.rabbitmq;
public interface NotificationWorker extends Runnable {
  void stop();
}
