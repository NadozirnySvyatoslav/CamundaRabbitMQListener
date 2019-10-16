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
import java.util.Map;


@ProcessApplication(name="RMQ Listener service")
public class RMQListener extends ServletProcessApplication {
  private static final Logger LOGGER = LoggerFactory.getLogger(RMQListener.class);
  private RMQNotificationService notificationService;

  @PostDeploy
  public void startService(ProcessEngine processEngine) throws Exception {

    RuntimeService runtimeService = processEngine.getRuntimeService();
    notificationService = new RMQNotificationService();

    notificationService.registerHandler(new OnDelivery() {
	public void onDelivery(Delivery delivery){
	    try{
	    AMQP.BasicProperties properties=delivery.getProperties();
	    String type="text/plain";
	    String message="";
	    if (properties.getContentType()!=null){
		type=properties.getContentType();
	    }
	    if (type=="text/plain" || type=="application/json" || type=="text/html"){
		if (properties.getContentEncoding()==null || properties.getContentEncoding().isEmpty()){
		    message=new String(delivery.getBody(),"UTF-8");
		}else{
		    message=new String(delivery.getBody(),properties.getContentEncoding());
		}
	    }
	    Map<String,Object> headers=properties.getHeaders();
	    String process_name=null;
	    String message_name=null;
	    String business_key=null;
	    if (headers.get("process_name")!=null)
		process_name=headers.get("process_name").toString();
	    if (headers.get("message_name")!=null)
		message_name=headers.get("message_name").toString();
	    if (headers.get("business_key")!=null)
		business_key=headers.get("business_key").toString();
	    Map<String,Object> vars=Variables.createVariables()
		.putValue("contentType",properties.getContentType())
		.putValue("contentEncoding",properties.getContentEncoding())
		.putValue("appId",properties.getAppId())
		.putValue("messageId",properties.getMessageId())
		.putValue("correlationId",properties.getCorrelationId())
		.putValue("type",properties.getType())
		.putValue("replyTo",properties.getReplyTo())
        	.putValue("message", message);

    	    if (process_name!=null){
	        LOGGER.debug("RMQ start process: "+process_name+" with "+business_key,this);
    		runtimeService.startProcessInstanceByKey(process_name,business_key,vars);
	    }else
	    if (message_name!=null){
		LOGGER.debug("RMQ send message: "+message_name+" with "+business_key,this);
    		runtimeService.correlateMessage(message_name,business_key,vars);
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


  @PreUndeploy
  public void stopService() throws Exception {
    notificationService.stop();
  }

}
