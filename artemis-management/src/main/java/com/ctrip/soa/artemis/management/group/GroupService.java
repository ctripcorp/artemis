package com.ctrip.soa.artemis.management.group;

/**
 * Created by fang_j on 10/07/2016.
 */
public interface GroupService {
    InsertRouteRulesResponse insertRouteRules(InsertRouteRulesRequest request);

    UpdateRouteRulesResponse updateRouteRules(UpdateRouteRulesRequest request);

    DeleteRouteRulesResponse deleteRouteRules(DeleteRouteRulesRequest request);

    GetAllRouteRulesResponse getAllRouteRules(GetAllRouteRulesRequest request);

    GetRouteRulesResponse getRouteRules(GetRouteRulesRequest request);

    InsertRouteRuleGroupsResponse insertRouteRuleGroups(InsertRouteRuleGroupsRequest request);

    UpdateRouteRuleGroupsResponse updateRouteRuleGroups(UpdateRouteRuleGroupsRequest request);

    DeleteRouteRuleGroupsResponse deleteRouteRuleGroups(DeleteRouteRuleGroupsRequest request);

    GetAllRouteRuleGroupsResponse getAllRouteRuleGroups(GetAllRouteRuleGroupsRequest request);

    GetRouteRuleGroupsResponse getRouteRuleGroups(GetRouteRuleGroupsRequest request);

    ReleaseRouteRuleGroupsResponse releaseRouteRuleGroups(ReleaseRouteRuleGroupsRequest request);

    InsertGroupsResponse insertGroups(InsertGroupsRequest request);

    UpdateGroupsResponse updateGroups(UpdateGroupsRequest request);

    DeleteGroupsResponse deleteGroups(DeleteGroupsRequest request);

    GetAllGroupsResponse getAllGroups(GetAllGroupsRequest request);

    GetGroupsResponse getGroups(GetGroupsRequest request);

    InsertGroupTagsResponse insertGroupTags(InsertGroupTagsRequest request);

    UpdateGroupTagsResponse updateGroupTags(UpdateGroupTagsRequest request);

    DeleteGroupTagsResponse deleteGroupTags(DeleteGroupTagsRequest request);

    GetAllGroupTagsResponse getAllGroupTags(GetAllGroupTagsRequest request);

    GetGroupTagsResponse getGroupTags(GetGroupTagsRequest request);

    OperateGroupOperationsResponse operateGroupOperations(OperateGroupOperationsRequest request);

    GetAllGroupOperationsResponse getAllGroupOperations(GetAllGroupOperationsRequest request);

    GetGroupOperationsResponse getGroupOperations(GetGroupOperationsRequest request);

    CreateRouteRuleResponse createRouteRule(CreateRouteRuleRequest request);

    OperateGroupOperationResponse operateGroupOperation(OperateGroupOperationRequest request);

    InsertGroupInstancesResponse insertGroupInstances(InsertGroupInstancesRequest request);

    DeleteGroupInstancesResponse deleteGroupInstances(DeleteGroupsInstancesRequest request);

    GetGroupInstancesResponse getGroupInstances(GetGroupInstancesRequest request);

    OperationResponse insertServiceInstances(InsertServiceInstancesRequest request);

    OperationResponse deleteServiceInstances(DeleteServiceInstancesRequest request);

    GetServiceInstancesResponse getServiceInstances(GetServiceInstancesRequest request);
}
