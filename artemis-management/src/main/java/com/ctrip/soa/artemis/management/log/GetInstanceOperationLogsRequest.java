package com.ctrip.soa.artemis.management.log;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetInstanceOperationLogsRequest {
    private String regionId;
    private String serviceId;
    private String instanceId;
    private String operation;
    private String operatorId;
    private Boolean complete;

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    @Override
    public String toString() {
        return "GetInstanceOperationLogsRequest{" +
                "regionId='" + regionId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", operation='" + operation + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", complete=" + complete +
                '}';
    }
}
