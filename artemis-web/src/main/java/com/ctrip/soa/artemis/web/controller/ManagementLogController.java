package com.ctrip.soa.artemis.web.controller;

import com.ctrip.soa.artemis.management.log.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ctrip.soa.artemis.management.config.RestPaths;
import com.ctrip.soa.artemis.management.ManagementLogServiceImpl;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
@RestController
@RequestMapping(path = RestPaths.MANAGEMENT_LOG_PATH)
public class ManagementLogController {
    private ManagementLogService managementLogService = ManagementLogServiceImpl.getInstance();
    
    @RequestMapping(path = RestPaths.MANAGEMENT_INSTANCE_OPERATION_LOGS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetInstanceOperationLogsResponse getInstanceOperationLogs(@RequestBody GetInstanceOperationLogsRequest request) {
        return managementLogService.getInstanceOperationLogs(request);
    }
    
    @RequestMapping(path = RestPaths.MANAGEMENT_SERVER_OPERATION_LOGS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetServerOperationLogsResponse getServerOperationLogs(@RequestBody GetServerOperationLogsRequest request) {
        return managementLogService.getServerOperationLogs(request);
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GROUP_OPERATION_LOGS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetGroupOperationLogsResponse getGroupOperationLogs(@RequestBody GetGroupOperationLogsRequest request) {
        return managementLogService.getGroupOperationLogs(request);
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GROUP_LOGS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetGroupLogsResponse getGroupOperationLogs(@RequestBody GetGroupLogsRequest request) {
        return managementLogService.getGroupLogs(request);
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_ROUTE_RULE_LOGS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetRouteRuleLogsResponse getRouteRuleLogs(@RequestBody GetRouteRuleLogsRequest request) {
        return managementLogService.getRouteRuleLogs(request);
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_ROUTE_RULE_GROUP_LOGS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetRouteRuleGroupLogsResponse getGroupOperationLogs(@RequestBody GetRouteRuleGroupLogsRequest request) {
        return managementLogService.getRouteRuleGroupLogs(request);
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_ZONE_OPERATION_LOGS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetZoneOperationLogsResponse getZoneOperationLogs(@RequestBody GetZoneOperationLogsRequest request) {
        return managementLogService.getZoneOperationLogs(request);
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GROUP_INSTANCE_LOGS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetGroupInstanceLogsResponse getGroupInstanceLogs(@RequestBody GetGroupInstanceLogsRequest request) {
        return managementLogService.getGroupInstanceLogs(request);
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_SERVICE_INSTANCE_LOGS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetServiceInstanceLogsResponse getGroupInstanceLogs(@RequestBody GetServiceInstanceLogsRequest request) {
        return managementLogService.getServiceInstanceLogs(request);
    }
}