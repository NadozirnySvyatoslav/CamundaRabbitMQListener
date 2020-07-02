package com.ktc.rabbitmq;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.*;
import org.camunda.bpm.application.PostDeploy;
import org.camunda.bpm.application.PreUndeploy;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.variable.Variables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.lang.StringBuilder;

@ProcessApplication(name="RMQ Listener service")
public class RMQListener extends ServletProcessApplication {
  private static final Logger LOGGER = LoggerFactory.getLogger(RMQListener.class);
  private RMQNotificationService notificationService;

  @PostDeploy
  public void startService(ProcessEngine processEngine) throws Exception {
    String rmq_uri=Config.config().getString("rmq_uri");
    HashMap<String,String> services=Config.getServices();

    RuntimeService runtimeService = processEngine.getRuntimeService();
    for(Map.Entry<String, String> entry : services.entrySet() ){
        String queue=entry.getKey();
        String process=entry.getValue();
  		LOGGER.debug("RMQ LISTENER: queue: " + queue + " process: " + process, this);

        notificationService = new RMQNotificationService(rmq_uri, queue, process);

        notificationService.registerHandler(new OnDelivery() {
    	public void onDelivery(Delivery delivery, String process_name){
	        try{
	            AMQP.BasicProperties properties=delivery.getProperties();
        	    String type="";
        	    String message="";
        	    if (properties.getContentType()!=null){
		            type=properties.getContentType();
        	    }
    		LOGGER.debug("RMQ received message type: " + properties.getType(), this);
	    	LOGGER.debug("RMQ received message content-type: " + type, this);

                Map<String,Object> vars=Variables.createVariables()
        		.putValue("contentType",properties.getContentType())
        		.putValue("contentEncoding",properties.getContentEncoding())
        		.putValue("appId",properties.getAppId())
        		.putValue("messageId",properties.getMessageId())
        		.putValue("correlationId",properties.getCorrelationId())
        		.putValue("type",properties.getType())
        		.putValue("replyTo",properties.getReplyTo());

                try{
        		    message=new String(delivery.getBody(),properties.getContentEncoding());
       	            vars.put("message", message);

                }catch(Exception e){
                   	vars.put("messageRaw", delivery.getBody());
        		    LOGGER.error("Error {} ",e);
                }
                String business_key="";
                if ( properties.getCorrelationId() != null )
                     business_key=properties.getCorrelationId();

           	    if (process_name!=null){
        	        LOGGER.debug("RMQ start process: \""+process_name+"\" with businessKey: "+business_key+"\nMessage:\n"+message,this);
            		runtimeService.startProcessInstanceByKey(process_name,business_key,vars);
        	    }else{
		            LOGGER.debug("RMQ received message but not activity created ",this);
	            }

	    //runtimeService:
	    //.correlateMessage(String messageName, String businessKey, Map<String,Object> processVariables);
	    //.startProcessInstanceByKey(String processDefinitionKey, String businessKey, Map<String,Object> variables);
	    //.	startProcessInstanceByMessage(String messageName, String businessKey, Map<String,Object> processVariables)
        	    }catch(Exception e){
		            LOGGER.error("Error {} ",e);
        	    }
        	}
        });
        notificationService.start();
    }
  }

  @PreUndeploy
  public void stopService() throws Exception {
    notificationService.stop();
  }

}
