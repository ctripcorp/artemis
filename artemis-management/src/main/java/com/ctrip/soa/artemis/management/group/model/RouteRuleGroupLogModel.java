package com.ctrip.soa.artemis.management.group.model;

import com.ctrip.soa.artemis.management.common.OperationContext;

/**
 * Created by fang_j on 10/07/2016.
 */
public class RouteRuleGroupLogModel extends RouteRuleGroupModel {
    private String operation;
    private String extensions;
    private String operatorId;
    private String token;
    private String reason;

    public RouteRuleGroupLogModel() {
    }

    public RouteRuleGroupLogModel(Long routeRuleId, Long groupId, String operatorId, String operation) {
        super(routeRuleId, groupId);
        this.operatorId = operatorId;
        this.operation = operation;
    }

    public RouteRuleGroupLogModel(RouteRuleGroupModel routeRuleGroup, OperationContext operationContext) {
        super(routeRuleGroup.getRouteRuleId(), routeRuleGroup.getGroupId(), routeRuleGroup.getUnreleasedWeight());
        this.operatorId = operationContext.getOperatorId();
        this.token = operationContext.getToken();
        this.reason = operationContext.getReason();
        this.operation = operationContext.getOperation();
        this.extensions = operationContext.getExtensions();
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
