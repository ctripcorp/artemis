package com.ctrip.soa.artemis.management.group.model;

import com.ctrip.soa.artemis.management.common.OperationContext;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupInstanceLogModel extends GroupInstanceModel{
    private String operation;
    private String operatorId;
    private String token;
    private String reason;

    public GroupInstanceLogModel() {
    }

    public GroupInstanceLogModel(Long groupId, String instanceId, String operatorId, String operation) {
        super(groupId, instanceId);
        this.operatorId = operatorId;
        this.operation = operation;
    }

    public GroupInstanceLogModel(OperationContext operationContext, GroupInstanceModel groupInstanceModel) {
        super(groupInstanceModel.getGroupId(), groupInstanceModel.getInstanceId());
        this.operation = operationContext.getOperation();
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
