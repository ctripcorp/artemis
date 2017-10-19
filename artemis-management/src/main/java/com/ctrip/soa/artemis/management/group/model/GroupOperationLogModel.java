package com.ctrip.soa.artemis.management.group.model;

import com.ctrip.soa.artemis.management.common.OperationContext;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupOperationLogModel extends GroupOperationModel{
    private boolean complete;
    private String extensions;
    private String operatorId;
    private String token;
    private String reason;

    public GroupOperationLogModel() {
    }

    public GroupOperationLogModel(Long groupId, String operation, String operatorId) {
        super(groupId, operation);
        this.operatorId = operatorId;
    }

    public GroupOperationLogModel(GroupOperationModel groupOperation, OperationContext operationContext, boolean complete) {
        super(groupOperation.getGroupId(), groupOperation.getOperation());
        this.complete = complete;
        this.extensions = operationContext.getExtensions();
        this.operatorId = operationContext.getOperatorId();
        this.token = operationContext.getToken();
        this.reason = operationContext.getReason();
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
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
