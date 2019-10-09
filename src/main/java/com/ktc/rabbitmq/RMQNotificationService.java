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
	protected NotificationWorker notificationWorker;
	protected final List<OnDelivery> handlers = new LinkedList<>();
    

	public RMQNotificationService() {
	}

	public void start() throws Exception {
		executorService = Executors.newSingleThreadExecutor();

		// START THREAD
		/* callback(){ 
			Object message=GET_MESSAGE
			handlers.forEach(handler -> handler.accept(message));
		     }

		*/
		    notificationWorker=new RMQNotificationWorker( (consumerTag,delivery) ->{
			handlers.forEach(handler -> handler.onDelivery(delivery));
		    });

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
        public void registerHandler(OnDelivery handler) {
                handlers.add(handler);
        }

}
