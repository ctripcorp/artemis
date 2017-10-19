package com.ctrip.soa.artemis.management.log;

/**
 * Created by fang_j on 10/07/2016.
 */
public interface ManagementLogService {
    public GetInstanceOperationLogsResponse getInstanceOperationLogs(GetInstanceOperationLogsRequest request);
    
    public GetServerOperationLogsResponse getServerOperationLogs(GetServerOperationLogsRequest request);

    public GetGroupOperationLogsResponse getGroupOperationLogs(GetGroupOperationLogsRequest request);

    public GetGroupLogsResponse getGroupLogs(GetGroupLogsRequest request);

    public GetRouteRuleLogsResponse getRouteRuleLogs(GetRouteRuleLogsRequest request);

    public GetRouteRuleGroupLogsResponse getRouteRuleGroupLogs(GetRouteRuleGroupLogsRequest request);

    public GetZoneOperationLogsResponse getZoneOperationLogs(GetZoneOperationLogsRequest request);

    public GetGroupInstanceLogsResponse getGroupInstanceLogs(GetGroupInstanceLogsRequest request);

    public GetServiceInstanceLogsResponse getServiceInstanceLogs(GetServiceInstanceLogsRequest request);
}