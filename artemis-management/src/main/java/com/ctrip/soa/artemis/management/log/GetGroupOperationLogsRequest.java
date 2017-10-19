package com.ctrip.soa.artemis.management.log;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetGroupOperationLogsRequest {
    private Long groupId;
    private String operation;
    private String operatorId;

    private Boolean complete;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }
}
