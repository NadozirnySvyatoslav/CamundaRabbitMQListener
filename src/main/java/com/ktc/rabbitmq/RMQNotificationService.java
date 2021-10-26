package com.ktc.rabbitmq;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import com.rabbitmq.client.Delivery;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RMQNotificationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RMQNotificationService.class);

	protected ExecutorService executorService = null;
	public RMQNotificationWorker notificationWorker;
	protected OnDelivery handler;
    protected String url;    
    protected String queue;    
    protected String process;    

	public RMQNotificationService(String url, String queue, String process) {
        this.url=url;
        this.queue=queue;
        this.process=process;
	}

	public void start() throws Exception {
		executorService = Executors.newSingleThreadExecutor();
	    notificationWorker=new RMQNotificationWorker(url, queue, (consumerTag, delivery )->{ handler.onDelivery( delivery, process ); } );
		LOGGER.debug("start RMQ notification service: {}", notificationWorker);
		executorService.submit(notificationWorker);
	}

	public void stop() {
		if (notificationWorker != null) {
			LOGGER.debug("stop RMQ notification service");
			notificationWorker.stop();
			executorService.shutdown();
			executorService = null;
		}
	}
        public void registerHandler( OnDelivery handler) {
               this.handler = handler;
        }

}
