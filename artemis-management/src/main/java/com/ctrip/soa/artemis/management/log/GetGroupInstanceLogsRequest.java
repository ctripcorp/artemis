package com.ctrip.soa.artemis.management.log;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetGroupInstanceLogsRequest {
    private Long groupId;
    private String instanceId;
    private String operation;
    private String operatorId;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
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

    @Override
    public String toString() {
        return "GetGroupInstanceLogsRequest{" +
                "groupId=" + groupId +
                ", operation='" + operation + '\'' +
                ", operatorId='" + operatorId + '\'' +
                '}';
    }
}
