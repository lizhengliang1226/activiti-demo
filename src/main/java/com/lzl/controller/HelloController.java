package com.lzl.controller;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 控制器
 *
 * @author LZL
 * @version v1.0
 * @date 2022/9/5-13:03
 */
@RestController
public class HelloController {
    @GetMapping("/build/{name}/{description}")
    public String hello(@PathVariable("name") String name, @PathVariable("description") String description) {
        //        1、创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //        2、得到RepositoryService实例
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //        3、使用RepositoryService进行部署
        Deployment deployment = repositoryService.createDeployment()
                                                 .addClasspathResource(name)
                                                 // 添加bpmn资源
                                                 .name(description)
                                                 .deploy();
        //        4、输出部署信息
        System.out.println("流程部署id：" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
        return "部署流程成功!";
    }

    @GetMapping("start/{process}")
    public String startProcess(@PathVariable("process") String process) {

        //        1、创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //        2、获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //        3、根据流程定义Id启动流程
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(process);
        //        输出内容
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
        System.out.println("当前活动Id：" + processInstance.getActivityId());
        return "启动流程成功！";
    }

    @GetMapping("find/{process}/{user}")
    public A findProcess(@PathVariable("process") String process, @PathVariable("user") String user) {
//        任务负责人
        String assignee = user;
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        创建TaskService
        TaskService taskService = processEngine.getTaskService();
//        根据流程key 和 任务负责人 查询任务
        List<Task> list = taskService.createTaskQuery()
                                     .processDefinitionKey(process) //流程Key
                                     .taskAssignee(assignee)//只查询该任务负责人的任务
                                     .list();

        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
        final A a = new A();
        a.setName("dasdas");
        return a;
    }

    @GetMapping("finish/{process}/{user}")
    public String finish(@PathVariable("process") String process, @PathVariable("user") String user) {
        //        获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        获取taskService
        TaskService taskService = processEngine.getTaskService();

//        根据流程key 和 任务的负责人 查询任务
//        返回一个任务对象
        Task task = taskService.createTaskQuery()
                               .processDefinitionKey(process) //流程Key
                               .taskAssignee(user)  //要查询的负责人
                               .singleResult();

//        完成任务,参数：任务id
        taskService.complete(task.getId());
        return "任务处理完成！";
    }

    @GetMapping("/processDefinition/{process}")
    public String queryProcessDefinition(@PathVariable String process) {
        //        获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        得到ProcessDefinitionQuery 对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
//          查询出当前所有的流程定义
//          条件：processDefinitionKey =evection
//          orderByProcessDefinitionVersion 按照版本排序
//        desc倒叙
//        list 返回集合
        List<ProcessDefinition> definitionList = processDefinitionQuery.processDefinitionKey(process)
                                                                       .orderByProcessDefinitionVersion()
                                                                       .desc().list();
        StringBuilder sb = new StringBuilder();
//      输出流程定义信息
        for (ProcessDefinition processDefinition : definitionList) {
            System.out.println("流程定义 id=" + processDefinition.getId());
            System.out.println("流程定义 name=" + processDefinition.getName());
            System.out.println("流程定义 key=" + processDefinition.getKey());
            System.out.println("流程定义 Version=" + processDefinition.getVersion());
            System.out.println("流程部署ID =" + processDefinition.getDeploymentId());
            sb.append("流程定义 id=" + processDefinition.getId() + "流程定义 name=" + processDefinition.getName() +
                              "流程定义 key=" + processDefinition.getKey() + "流程定义 Version=" + processDefinition.getVersion() +
                              "流程部署ID =" + processDefinition.getDeploymentId());
            sb.append("\n");
        }

        return sb.toString();
    }
}