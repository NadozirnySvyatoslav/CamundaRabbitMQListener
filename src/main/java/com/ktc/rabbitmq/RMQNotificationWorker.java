package com.ktc.rabbitmq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.net.URISyntaxException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class RMQNotificationWorker implements NotificationWorker {

  private final static Logger LOGGER = LoggerFactory.getLogger(RMQNotificationWorker.class);
  protected boolean runnning = true;
  private DeliverCallback callback;
  private String url="";
  private String queue="";
  private Channel channel;
  private Connection connection;

   public RMQNotificationWorker(DeliverCallback callback) 
	throws java.net.URISyntaxException, 
	java.io.IOException,
	java.security.NoSuchAlgorithmException,
	java.security.KeyManagementException,
	java.util.concurrent.TimeoutException {
      this.callback=callback;
      RMQConfiguration configuration = RMQConfigurationFactory.getConfiguration();
      url=configuration.getUrl();
      queue=configuration.getQueue();
	LOGGER.debug("Url: "+url,this);
	LOGGER.debug("Queue: "+queue,this);
      ConnectionFactory factory = new ConnectionFactory();
      factory.setUri(url);
      connection = factory.newConnection();
      channel = connection.createChannel();
//      channel.queueDeclare(queue, true, false, false,  null);
  }

  @Override
  public void run() {
	try{
	channel.basicConsume(queue,  true, callback, consumerTag -> {});
	}catch(Exception e){

	}
  }

 @Override
  public void stop() {
    runnning = false;
    try{
    channel.close();
    connection.close();
    }catch(Exception e){

	}
  }

 @Override
  public String toString() {
    return "RMQNotificationWorker";
  }

    

}