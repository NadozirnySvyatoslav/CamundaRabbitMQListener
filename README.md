# Camunda RabbitMQ Listener service

1. Build the WAR using Maven `mvn install`
2. Download a [Camunda Distribution](https://camunda.org/download/)
3. Copy the WAR `rabbitmq-listener-1.0.war` into the webapps / deployments folder
4. Copy the configuration `src/main/resources/rabbitmq.properties` to application server config folder and adjust it.
5. Start the application server
6. Deploy test BPMN process

7. Send a message to queue 
```

```


8. Check that a user task is created - complete it



