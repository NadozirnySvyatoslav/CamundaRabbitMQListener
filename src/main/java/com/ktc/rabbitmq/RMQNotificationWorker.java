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
  protected boolean running = true;
  private DeliverCallback callback;
  private String url="";
  private String queue="";
  public String process="";
  private Channel channel;
  private Connection connection;

   public RMQNotificationWorker(String url, String queue, DeliverCallback callback) 
	throws java.net.URISyntaxException, 
	java.io.IOException,
	java.security.NoSuchAlgorithmException,
	java.security.KeyManagementException,
	java.util.concurrent.TimeoutException {
    this.callback=callback;
    this.url=url;
    this.queue=queue;
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri(url);
    connection = factory.newConnection();
    channel = connection.createChannel();
//      channel.queueDeclare(queue, true, false, false,  null);
  }

  @Override
  public void run() {
    int i=0;
	try{
        while(running){
            LOGGER.debug( "RMQ Start consume: "+ queue,this);
    	    channel.basicConsume(queue,  true, callback, consumerTag -> {});
            if (running)
                LOGGER.debug( "Try Restart : " + i,this);
            i++;
        }
	}catch(Exception e){
        LOGGER.error( "RMQ ERROR: [" + queue + "] " + e.getMessage(),this);
	}
  }

 @Override
  public void stop() {
    running = false;
    try{
        channel.close();
        connection.close();
    }catch(Exception e){
        LOGGER.error( "RMQ ERROR: [" + queue + "] " + e.getMessage(),this);
	}
  }

 @Override
  public String toString() {
    return "RMQNotificationWorker";
  }
}