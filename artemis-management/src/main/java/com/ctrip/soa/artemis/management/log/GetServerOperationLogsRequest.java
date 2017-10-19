package com.ctrip.soa.artemis.management.log;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetServerOperationLogsRequest {
    private String regionId;
    private String serverId;
    private String operation;
    private String operatorId;
    private Boolean complete;

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
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

    @Override
    public String toString() {
        return "GetServerOperationLogsRequest{" +
                "regionId='" + regionId + '\'' +
                ", serverId='" + serverId + '\'' +
                ", operation='" + operation + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", complete=" + complete +
                '}';
    }
}
