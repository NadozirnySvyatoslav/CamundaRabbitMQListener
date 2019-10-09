package com.ktc.rabbitmq;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import com.rabbitmq.client.Delivery;

import org.camunda.bpm.application.PostDeploy;
import org.camunda.bpm.application.PreUndeploy;
import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.variable.Variables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
	    String message=new String(delivery.getBody(),"UTF-8");
	
    	    runtimeService.startProcessInstanceByKey("Process_Name",
        	Variables.createVariables()
        	.putValue("message", message)
	    );

	    //runtimeService:
	    //.correlateMessage(String messageName, String businessKey, Map<String,Object> processVariables);
	    //.startProcessInstanceByKey(String processDefinitionKey, String businessKey, Map<String,Object> variables);
	    //.	startProcessInstanceByMessage(String messageName, String businessKey, Map<String,Object> processVariables)

	    }catch(Exception e){
		LOGGER.error("Error {} "+e.getMessage(),this);
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
