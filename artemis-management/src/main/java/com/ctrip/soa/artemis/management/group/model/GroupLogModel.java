package com.ctrip.soa.artemis.management.group.model;

import com.ctrip.soa.artemis.management.common.OperationContext;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupLogModel extends GroupModel {
    private String operation;
    private String extensions;
    private String operatorId;
    private String token;
    private String reason;

    public GroupLogModel() {
    }

    public GroupLogModel(String serviceId, String regionId, String zoneId, String name, String appId, String operation, String operatorId) {
        super(serviceId, regionId, zoneId, name, appId, null, null);
        this.operation = operation;
        this.operatorId = operatorId;
    }

    public GroupLogModel(GroupModel group, OperationContext operationContext) {
        super(group.getServiceId(), group.getRegionId(), group.getZoneId(), group.getName(), group.getAppId(), null, group.getStatus());
        this.operation = operationContext.getOperation();
        this.extensions = operationContext.getExtensions();
        this.operatorId = operationContext.getOperatorId();
        this.token = operationContext.getToken();
        this.reason = operationContext.getReason();
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