package com.lzl.listener;

import org.activiti.engine.delegate.*;

/**
 * 测试监听器
 *
 * @author LZL
 * @version v1.0
 * @date 2022/9/10-13:56
 */
public class TestListener implements ExecutionListener,TaskListener {


    @Override
    public void notify(DelegateExecution delegateExecution) {
      if ("提交申请".equals(delegateExecution.getCurrentFlowElement().getName())){
          System.out.println("监听到了提交申请节点");
          System.out.println("事件");
          System.out.println(delegateExecution.getEventName());
      }
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println(delegateTask.getName());
        if (delegateTask.getEventName().equals(ExecutionListener.EVENTNAME_START)) {
            delegateTask.setAssignee("小白");
        }
    }
}