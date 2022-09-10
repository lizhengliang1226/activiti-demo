package com.lzl.controller;

import cn.hutool.core.io.IoUtil;
import com.lzl.common.entity.Result;
import com.lzl.common.entity.ResultCode;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 *
 *
 * @author LZL
 * @version v1.0
 * @date 2022/9/5-13:03
 */
@RestController
public class HelloController {
    /**
     * 部署流程
     * @param name 流程描述名
     * @param bpmnName 流程文件名
     * @return
     */
    @GetMapping("/build/{name}/{bpmnName}")
    public Result buildProcess(@PathVariable String name, @PathVariable String bpmnName) {
        // 1、创建ProcessEngine，此时会自动创建数据库表
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2、得到RepositoryService实例
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3、使用RepositoryService进行部署
        Deployment deployment = repositoryService.createDeployment()
                                                 .addClasspathResource("bpmn/"+bpmnName+".bpmn20.xml")
                                                 .addClasspathResource("bpmn/"+bpmnName+".png")
                                                 // 添加bpmn资源
                                                 .name(name)
                                                 .deploy();
        // 4、输出部署信息
        System.out.println("流程部署id：" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
        return new Result(ResultCode.SUCCESS,name+"部署成功！");
    }

    /**
     * 通过zip部署流程
     * @param name
     * @param bpmnName
     * @return
     */
    @GetMapping("/buildByZip/{name}/{bpmnName}")
    public Result deployByZip(@PathVariable String name, @PathVariable String bpmnName) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        InputStream inputStream=this.getClass().getClassLoader().getResourceAsStream("bpmn/*.zip");
        final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deployment = repositoryService.createDeployment().addZipInputStream(zipInputStream)
                                                 .deploy();
        // 4、输出部署信息
        System.out.println("流程部署id：" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
        return new Result(ResultCode.SUCCESS,name+"部署成功！");
    }
    /**
     * 启动流程
     * @param process
     * @return
     */
    @GetMapping("start/{process}")
    public Result startProcess(@PathVariable("process") String process) {
        // 1、创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2、获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 3、根据流程定义Id启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(process);
        // 输出内容
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
        System.out.println("当前活动Id：" + processInstance.getActivityId());
        return new Result(ResultCode.SUCCESS,"启动"+process+"流程成功！");
    }
    /**
     * 启动流uel表达式的程
     * @param process
     * @return
     */
    @GetMapping("startUel/{process}")
    public Result startUelProcess(@PathVariable("process") String process) {
        // 1、创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2、获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 3、根据流程定义Id启动流程
        HashMap<String,Object> parameters = new HashMap<>();
        parameters.put("assign0","露娜");
        parameters.put("assign1","李白");
        parameters.put("assign2","元歌");
        parameters.put("assign3","张良");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(process,parameters);
        // 输出内容
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
        System.out.println("当前活动Id：" + processInstance.getActivityId());
        return new Result(ResultCode.SUCCESS,"启动"+process+"流程成功！");
    }
    /**
     * 查询某人待办任务列表
     * @param process
     * @param user
     * @return
     */
    @GetMapping("find/{process}/{user}")
    public Result findProcess(@PathVariable("process") String process, @PathVariable("user") String user) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                                     .processDefinitionKey(process)
                                     .taskAssignee(user)
                                     .list();
        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
        return Result.SUCCESS();
    }

    /**
     * 某个用户完成任务
     * @param process
     * @param user
     * @return
     */
    @GetMapping("finish/{process}/{user}")
    public Result finish(@PathVariable String process,@PathVariable  String user) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        final Task task = taskService.createTaskQuery()
                                     .processDefinitionKey(process)
                                     .taskAssignee(user).singleResult();
        // 完成任务,参数：任务id
        taskService.complete(task.getId());
        return new Result(ResultCode.SUCCESS,user+"的"+process+"流程"+task.getName()+"任务处理完成！");
    }

    /**
     * 查询流程定义的信息
     * @param process 流程id
     * @return
     */
    @GetMapping("/processDefinition/{process}")
    public Result queryProcessDefinition(@PathVariable String process) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
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
            sb.append("流程定义 id=" + processDefinition.getId() +
                              "流程定义 name=" + processDefinition.getName() +
                              "流程定义 key=" + processDefinition.getKey() +
                              "流程定义 Version=" + processDefinition.getVersion() +
                              "流程部署ID =" + processDefinition.getDeploymentId());
            sb.append("\n");
        }
        return new Result(ResultCode.SUCCESS,sb);
    }

    /**
     * 删除流程，默认不是级联删除，在流程没走完删除会报错，需要级联删除
     * @param process 流程id
     * @return
     */
    @GetMapping("/delProcess/{process}")
    public Result delProcess(@PathVariable String process) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        final ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> definitionList = processDefinitionQuery.processDefinitionKey(process)
                                                                       .orderByProcessDefinitionVersion()
                                                                       .desc().list();
        definitionList.forEach(d->{
            // 级联删除
            repositoryService.deleteDeployment(d.getDeploymentId(),true);
            // 普通删除
            // repositoryService.deleteDeployment(d.getDeploymentId());
        });
        return new Result(ResultCode.SUCCESS,process+"流程删除成功！");
    }

    /**
     * 下载流程图片资源
     * @param process 流程id
     * @return
     */
    @GetMapping("/getProcessResource/{process}")
    public void getProcessResource(@PathVariable String process, HttpServletResponse response) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        final ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> definitionList = processDefinitionQuery.processDefinitionKey(process)
                                                                       .orderByProcessDefinitionVersion()
                                                                       .desc().list();
        // 设置响应数据格式为流
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        definitionList.forEach(d->{
            final String deploymentId = d.getDeploymentId();
            // 获取流程xml
            final InputStream xmlRes = repositoryService.getResourceAsStream(deploymentId, d.getResourceName());
            // 获取图片
            final InputStream pngRes = repositoryService.getResourceAsStream(deploymentId, d.getDiagramResourceName());
            try {
                // FileCopyUtils.copy(xmlRes, response.getOutputStream());
                IoUtil.copy(pngRes, response.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // return new Result(ResultCode.SUCCESS,process+"流程删除成功！");
    }

    /**
     * 查询历史信息
     * @param process
     * @return
     */
    @GetMapping("findHistory/{process}")
    public Result findHistory(@PathVariable String process) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        final RepositoryService repService = processEngine.getRepositoryService();
        final ProcessDefinitionQuery query = repService.createProcessDefinitionQuery();
        final ProcessDefinition processDefinition = query.processDefinitionKey(process).singleResult();
        final HistoryService historyService = processEngine.getHistoryService();
        final HistoricActivityInstanceQuery instanceQuery = historyService.createHistoricActivityInstanceQuery();
        instanceQuery.processDefinitionId(processDefinition.getId()).orderByHistoricActivityInstanceStartTime().asc().list().forEach(hisAct->{
            System.out.println("流程分配对象："+hisAct.getAssignee());
            System.out.println("实例名"+hisAct.getActivityName());
            System.out.println("actID:"+hisAct.getActivityId());
            System.out.println("流程id"+hisAct.getProcessDefinitionId());
            System.out.println("流程实例id"+hisAct.getProcessInstanceId());
        });
        return new Result(ResultCode.SUCCESS,process+"流程历史记录查询成功");
    }
    /**
     * 绑定busKey
     * @param process
     * @return
     */
    @GetMapping("bindKey/{process}")
    public Result bindBusKey(@PathVariable String process) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        final RuntimeService runtimeService = processEngine.getRuntimeService();
        final ProcessInstance instance = runtimeService.startProcessInstanceByKey(process, "1001");
        System.out.println("busKey"+instance.getBusinessKey());
        return new Result(ResultCode.SUCCESS,process+"流程启动成功 key:"+instance.getBusinessKey());
    }

    /**
     * 全部流程的挂起与激活
     * @param process
     * @return
     */
    @GetMapping("suspendAllProcess/{process}")
    public Result suspendAllProcess(@PathVariable String process) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        final RepositoryService repositoryService = processEngine.getRepositoryService();
        final ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        final ProcessDefinition definition = query.processDefinitionKey(process).singleResult();
            final String id = definition.getId();
        if (definition.isSuspended()) {
            // 激活流程
            repositoryService.activateProcessDefinitionById(id,true,null);
            System.out.println("流程已激活！");
        }
        if(!definition.isSuspended()){
            // 挂起流程
            repositoryService.suspendProcessDefinitionById(id,true,null);
            System.out.println("流程已挂起！");
        }
        return new Result(ResultCode.SUCCESS,process+"激活或挂起成功！");
    }
    /**
     * 单个流程实例的挂起与激活
     * @param process
     * @return
     */
    @GetMapping("suspendProcess/{process}/{busKey}")
    public Result suspendProcess(@PathVariable String process,@PathVariable String busKey) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        final RuntimeService runtimeService = processEngine.getRuntimeService();
        final ProcessInstanceQuery query1 = runtimeService.createProcessInstanceQuery();
        final ProcessInstance instance = query1.processInstanceBusinessKey(busKey, process).singleResult();
        final String id = instance.getId();
        if (instance.isSuspended()) {
            // 激活流程
            runtimeService.activateProcessInstanceById(id);
            System.out.println(busKey + process+"流程已激活！");
        }
        if(!instance.isSuspended()){
            // 挂起流程
            runtimeService.suspendProcessInstanceById(id);
            System.out.println(busKey+process+"流程已挂起！");
        }
        return new Result(ResultCode.SUCCESS,process+busKey+"激活或挂起成功！");
    }
}