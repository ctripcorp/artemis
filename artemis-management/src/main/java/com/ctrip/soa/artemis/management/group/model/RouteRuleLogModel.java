package com.ctrip.soa.artemis.management.group.model;

import com.ctrip.soa.artemis.management.common.OperationContext;

/**
 * Created by fang_j on 10/07/2016.
 */
public class RouteRuleLogModel extends RouteRuleModel {
    private String operation;
    private String extensions;
    private String operatorId;
    private String token;
    private String reason;

    public RouteRuleLogModel(){

    }

    public RouteRuleLogModel(String serviceId, String name, String operation, String operatorId) {
        super(serviceId, name, null, null, null);
        this.operation = operation;
        this.operatorId = operatorId;
    }

    public RouteRuleLogModel(RouteRuleModel routeRule, OperationContext operationContext) {
        super(routeRule.getServiceId(), routeRule.getName(), null, routeRule.getStatus(), routeRule.getStrategy());
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
