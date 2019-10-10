package com.ktc.rabbitmq;

import static org.junit.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.util.List;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.JobQuery;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class AppTest 
{

  @Rule
  public ProcessEngineRule engineRule = new ProcessEngineRule();

  private RMQListener processApplication;
  private RuntimeService runtimeService;
  private TaskService taskService;

  @Before
  public void init() throws IOException {
    runtimeService = engineRule.getRuntimeService();
    taskService = engineRule.getTaskService();

    processApplication = new RMQListener();
  }

  @Deployment(resources = "processes/Process.bpmn")

   @Test
  public void processPrintMail() throws Exception {

    processApplication.startService(engineRule.getProcessEngine());

    TaskQuery taskQuery = taskService.createTaskQuery().taskName("print it");

    // wait for first mail
    while(taskQuery.count() == 0) {
      Thread.sleep(500);
    }

    List<Task> tasks = taskQuery.list();
    assertThat(tasks).isNotEmpty();

    for (Task task : tasks) {
      taskService.complete(task.getId());
    }
    assertThat(runtimeService.createProcessInstanceQuery().list()).isEmpty();
  }


 @After
  public void cleanup() throws Exception {
    processApplication.stopService();
  }

}
