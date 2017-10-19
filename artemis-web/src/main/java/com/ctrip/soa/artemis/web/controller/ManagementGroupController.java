package com.ctrip.soa.artemis.web.controller;

import com.ctrip.soa.artemis.management.config.RestPaths;
import com.ctrip.soa.artemis.management.GroupServiceImpl;
import com.ctrip.soa.artemis.management.group.*;
import com.ctrip.soa.artemis.metric.MetricLoggerHelper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by fang_j on 10/07/2016.
 */
@RestController
@RequestMapping(path = RestPaths.MANAGEMENT_GROUP_PATH)
public class ManagementGroupController {
    private final GroupServiceImpl groupService = GroupServiceImpl.getInstance();

    @RequestMapping(path = RestPaths.MANAGEMENT_INSERT_ROUTE_RULES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public InsertRouteRulesResponse insertRouteRules(@RequestBody InsertRouteRulesRequest request) {
        InsertRouteRulesResponse response = groupService.insertRouteRules(request);
        MetricLoggerHelper.logResponseEvent("management-group", "insert-route-rules", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_UPDATE_ROUTE_RULES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public UpdateRouteRulesResponse updateRouteRules(@RequestBody UpdateRouteRulesRequest request) {
        UpdateRouteRulesResponse response = groupService.updateRouteRules(request);
        MetricLoggerHelper.logResponseEvent("management-group", "update-route-rules", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_DELETE_ROUTE_RULES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public DeleteRouteRulesResponse deleteRouteRules(@RequestBody DeleteRouteRulesRequest request) {
        DeleteRouteRulesResponse response = groupService.deleteRouteRules(request);
        MetricLoggerHelper.logResponseEvent("management-group", "delete-route-rules", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GET_ALL_ROUTE_RULES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetAllRouteRulesResponse getAllRouteRules(@RequestBody GetAllRouteRulesRequest request) {
        GetAllRouteRulesResponse response = groupService.getAllRouteRules(request);
        MetricLoggerHelper.logResponseEvent("management-group", "get-all-route-rules", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GET_ROUTE_RULES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetRouteRulesResponse getRouteRules(@RequestBody GetRouteRulesRequest request) {
        GetRouteRulesResponse response = groupService.getRouteRules(request);
        MetricLoggerHelper.logResponseEvent("management-group", "get-route-rules", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_INSERT_ROUTE_RULE_GROUPS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public InsertRouteRuleGroupsResponse insertRouteRuleGroup(@RequestBody InsertRouteRuleGroupsRequest request) {
        InsertRouteRuleGroupsResponse response = groupService.insertRouteRuleGroups(request);
        MetricLoggerHelper.logResponseEvent("management-group", "insert-route-rule-groups", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_UPDATE_ROUTE_RULE_GROUPS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public UpdateRouteRuleGroupsResponse updateRouteRuleGroup(@RequestBody UpdateRouteRuleGroupsRequest request) {
        UpdateRouteRuleGroupsResponse response = groupService.updateRouteRuleGroups(request);
        MetricLoggerHelper.logResponseEvent("management-group", "update-route-rule-groups", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_RELEASE_ROUTE_RULE_GROUPS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ReleaseRouteRuleGroupsResponse releaseRouteRuleGroup(@RequestBody ReleaseRouteRuleGroupsRequest request) {
        ReleaseRouteRuleGroupsResponse response = groupService.releaseRouteRuleGroups(request);
        MetricLoggerHelper.logResponseEvent("management-group", "release-route-rule-groups", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_DELETE_ROUTE_RULE_GROUPS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public DeleteRouteRuleGroupsResponse deleteRouteRuleGroups(@RequestBody DeleteRouteRuleGroupsRequest request) {
        DeleteRouteRuleGroupsResponse response = groupService.deleteRouteRuleGroups(request);
        MetricLoggerHelper.logResponseEvent("management-group", "delete-route-rule-groups", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GET_ALL_ROUTE_RULE_GROUPS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetAllRouteRuleGroupsResponse getAllRouteRuleGroups(@RequestBody GetAllRouteRuleGroupsRequest request) {
        GetAllRouteRuleGroupsResponse response = groupService.getAllRouteRuleGroups(request);
        MetricLoggerHelper.logResponseEvent("management-group", "get-all-route-rule-groups", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GET_ROUTE_RULE_GROUPS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetRouteRuleGroupsResponse getRouteRuleGroups(@RequestBody GetRouteRuleGroupsRequest request) {
        GetRouteRuleGroupsResponse response = groupService.getRouteRuleGroups(request);
        MetricLoggerHelper.logResponseEvent("management-group", "get-route-rule-groups", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_INSERT_GROUPS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public InsertGroupsResponse insertGroups(@RequestBody InsertGroupsRequest request) {
        InsertGroupsResponse response = groupService.insertGroups(request);
        MetricLoggerHelper.logResponseEvent("management-group", "insert-groups", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_UPDATE_GROUPS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public UpdateGroupsResponse updateGroups(@RequestBody UpdateGroupsRequest request) {
        UpdateGroupsResponse response = groupService.updateGroups(request);
        MetricLoggerHelper.logResponseEvent("management-group", "update-groups", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_DELETE_GROUPS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public DeleteGroupsResponse deleteGroups(@RequestBody DeleteGroupsRequest request) {
        DeleteGroupsResponse response = groupService.deleteGroups(request);
        MetricLoggerHelper.logResponseEvent("management-group", "delete-groups", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GET_ALL_GROUPS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetAllGroupsResponse getAllGroups(@RequestBody GetAllGroupsRequest request) {
        GetAllGroupsResponse response = groupService.getAllGroups(request);
        MetricLoggerHelper.logResponseEvent("management-group", "get-all-groups", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GET_GROUPS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetGroupsResponse getGroups(@RequestBody GetGroupsRequest request) {
        GetGroupsResponse response = groupService.getGroups(request);
        MetricLoggerHelper.logResponseEvent("management-group", "get-groups", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_INSERT_GROUP_TAGS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public InsertGroupTagsResponse insertGroupTags(@RequestBody InsertGroupTagsRequest request) {
        InsertGroupTagsResponse response = groupService.insertGroupTags(request);
        MetricLoggerHelper.logResponseEvent("management-group", "insert-group-tags", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_UPDATE_GROUP_TAGS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public UpdateGroupTagsResponse updateGroupTags(@RequestBody UpdateGroupTagsRequest request) {
        UpdateGroupTagsResponse response = groupService.updateGroupTags(request);
        MetricLoggerHelper.logResponseEvent("management-group", "update-group-tags", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_DELETE_GROUP_TAGS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public DeleteGroupTagsResponse deleteGroupTags(@RequestBody DeleteGroupTagsRequest request) {
        DeleteGroupTagsResponse response = groupService.deleteGroupTags(request);
        MetricLoggerHelper.logResponseEvent("management-group", "delete-group-tags", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GET_ALL_GROUP_TAGS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetAllGroupTagsResponse getAllGroupTags(@RequestBody GetAllGroupTagsRequest request) {
        GetAllGroupTagsResponse response = groupService.getAllGroupTags(request);
        MetricLoggerHelper.logResponseEvent("management-group", "get-all-group-tags", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GET_GROUP_TAGS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetGroupTagsResponse getGroupTags(@RequestBody GetGroupTagsRequest request) {
        GetGroupTagsResponse response = groupService.getGroupTags(request);
        MetricLoggerHelper.logResponseEvent("management-group", "get-group-tags", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_OPERATE_GROUP_OPERATIONS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public OperateGroupOperationsResponse operateGroupOperations(@RequestBody OperateGroupOperationsRequest request) {
        OperateGroupOperationsResponse response = groupService.operateGroupOperations(request);
        MetricLoggerHelper.logResponseEvent("management-group", "operate-group-operations", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GET_ALL_GROUP_OPERATIONS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetAllGroupOperationsResponse getAllGroupOperations(@RequestBody GetAllGroupOperationsRequest request) {
        GetAllGroupOperationsResponse response = groupService.getAllGroupOperations(request);
        MetricLoggerHelper.logResponseEvent("management-group", "get-all-group-operations", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GET_GROUP_OPERATIONS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetGroupOperationsResponse getGroupOperations(@RequestBody GetGroupOperationsRequest request) {
        GetGroupOperationsResponse response = groupService.getGroupOperations(request);
        MetricLoggerHelper.logResponseEvent("management-group", "get-group-operations", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_CREATE_ROUTE_RULE_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public CreateRouteRuleResponse getGroupOperations(@RequestBody CreateRouteRuleRequest request) {
        CreateRouteRuleResponse response = groupService.createRouteRule(request);
        MetricLoggerHelper.logResponseEvent("management-group", "create-route-rule", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_OPERATE_GROUP_OPERATION_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public OperateGroupOperationResponse operateGroupOperation(@RequestBody OperateGroupOperationRequest request) {
        OperateGroupOperationResponse response = groupService.operateGroupOperation(request);
        MetricLoggerHelper.logResponseEvent("management-group", "operate-group-operation", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_INSERT_GROUP_INSTANCES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public InsertGroupInstancesResponse insertGroupInstances(@RequestBody InsertGroupInstancesRequest request) {
        InsertGroupInstancesResponse response = groupService.insertGroupInstances(request);
        MetricLoggerHelper.logResponseEvent("management-group", "insert-group-instances", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_DELETE_GROUP_INSTANCES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public DeleteGroupInstancesResponse deleteGroupInstances(@RequestBody DeleteGroupsInstancesRequest request) {
        DeleteGroupInstancesResponse response = groupService.deleteGroupInstances(request);
        MetricLoggerHelper.logResponseEvent("management-group", "delete-group-instances", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GET_GROUP_INSTANCES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetGroupInstancesResponse getGroupInstances(@RequestBody GetGroupInstancesRequest request) {
        GetGroupInstancesResponse response = groupService.getGroupInstances(request);
        MetricLoggerHelper.logResponseEvent("management-group", "get-group-instances", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_INSERT_SERVICE_INSTANCES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public OperationResponse insertServiceInstances(@RequestBody InsertServiceInstancesRequest request) {
        OperationResponse response = groupService.insertServiceInstances(request);
        MetricLoggerHelper.logResponseEvent("management-group", "insert-service-instances", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_DELETE_SERVICE_INSTANCES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public OperationResponse deleteServiceInstances(@RequestBody DeleteServiceInstancesRequest request) {
        OperationResponse response = groupService.deleteServiceInstances(request);
        MetricLoggerHelper.logResponseEvent("management-group", "delete-service-instances", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GET_SERVICE_INSTANCES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetServiceInstancesResponse getServiceInstances(@RequestBody GetServiceInstancesRequest request) {
        GetServiceInstancesResponse response = groupService.getServiceInstances(request);
        MetricLoggerHelper.logResponseEvent("management-group", "get-service-instances", response);
        return response;
    }
}
