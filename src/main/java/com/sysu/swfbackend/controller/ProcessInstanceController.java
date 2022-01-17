package com.sysu.swfbackend.controller;

import com.sysu.swfbackend.SecurityUtil;
import com.sysu.swfbackend.pojo.UserInfoBean;
import com.sysu.swfbackend.util.AjaxResponse;
import com.sysu.swfbackend.util.GlobalConfig;
import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/processInstance")
public class ProcessInstanceController {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private ProcessRuntime processRuntime;

    /**
     * 查询当前用户的流程实例
     * @param userInfoBean
     * @return
     */
    @GetMapping(value = "/getInstances")
    public AjaxResponse getInstances(@AuthenticationPrincipal UserInfoBean userInfoBean) {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("triniti");
            }
//            else {
//                securityUtil.logInAs(userInfoBean.getUsername());
//            }

            Page<ProcessInstance> processInstance = processRuntime.processInstances(Pageable.of(0, 100));
            List<ProcessInstance> list = processInstance.getContent();
            list.sort((y,x)->x.getStartDate().toString().compareTo(y.getStartDate().toString()));
            List<HashMap<String, Object>> listMap = new ArrayList<HashMap<String, Object>>();

            for (ProcessInstance pi : list) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", pi.getId());
                hashMap.put("name", pi.getName());
                hashMap.put("status", pi.getStatus());
                hashMap.put("processDefinitionId", pi.getProcessDefinitionId());
                hashMap.put("processDefinitionKey", pi.getProcessDefinitionKey());
                hashMap.put("startDate", pi.getStartDate());
                hashMap.put("processDefinitionVersion", pi.getProcessDefinitionVersion());
                listMap.add(hashMap);
            }

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(), "获取流程定义成功!", listMap);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(), "获取流程定义失败!", e.toString());
        }
    }

    /**
     * 启动流程实例
     * @param processDefinitionKey
     * @param instanceName
     * @return
     */
    @GetMapping(value = "/startProcess")
    public AjaxResponse startProcess(@RequestParam("processDefinitionKey") String processDefinitionKey,
                                     @RequestParam("instanceName") String instanceName) {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("triniti");
            } else {
                securityUtil.logInAs(SecurityContextHolder.getContext().getAuthentication().getName());
            }

            ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder
                    .start()
                    .withProcessDefinitionKey(processDefinitionKey)
                    .withName(instanceName)
                    .withBusinessKey("自定义BusinessKey")
//                    .withVariables("参数名", "参数值")
                    .build()
            );

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(), "启动流程实例成功!", null);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(), "启动流程实例失败!", e.toString());
        }
    }

    /**
     * 挂起流程实例
     * @param instanceID
     * @return
     */
    @GetMapping(value = "/suspendInstance")
    public AjaxResponse suspendInstance(@RequestParam("instanceID") String instanceID) {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("triniti");
            }

            ProcessInstance processInstance = processRuntime.suspend(ProcessPayloadBuilder
                            .suspend()
                            .withProcessInstanceId(instanceID)
                            .build()
            );

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(), "挂起流程实例成功!", processInstance.getName());
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(), "挂起流程实例失败!", e.toString());
        }
    }

    /**
     * 激活流程实例
     * @param instanceID
     * @return
     */
    @GetMapping(value = "/resumeInstance")
    public AjaxResponse resumeInstance(@RequestParam("instanceID") String instanceID) {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("triniti");
            }

            ProcessInstance processInstance = processRuntime.resume(ProcessPayloadBuilder
                    .resume()
                    .withProcessInstanceId(instanceID)
                    .build()
            );

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(), "激活流程实例成功!", processInstance.getName());
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(), "激活流程实例失败!", e.toString());
        }
    }

    // 删除流程实例
    @GetMapping(value = "/deleteInstance")
    public AjaxResponse deleteInstance(@RequestParam("instanceID") String instanceID) {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("triniti");
            }

            ProcessInstance processInstance = processRuntime.delete(ProcessPayloadBuilder
                    .delete()
                    .withProcessInstanceId(instanceID)
                    .build()
            );

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(), "删除流程实例成功!", processInstance.getName());
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(), "删除流程实例失败!", e.toString());
        }
    }

    // 查询流程参数
    @GetMapping(value = "/variables")
    public AjaxResponse variables(@RequestParam("instanceID") String instanceID) {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("triniti");
            }

            List<VariableInstance> variableInstances = processRuntime.variables(ProcessPayloadBuilder
                    .variables()
                    .withProcessInstanceId(instanceID)
                    .build()
            );

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(), "查询流程实例参数成功!", variableInstances);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(), "查询流程实例参数失败!", e.toString());
        }
    }
}