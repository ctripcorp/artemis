package com.ctrip.soa.artemis.management.log;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetServiceInstanceLogsRequest {
    private String serviceId;
    private String instanceId;
    private String operation;
    private String operatorId;

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

    @Override
    public String toString() {
        return "GetServiceInstanceLogsRequest{" +
                "operatorId='" + operatorId + '\'' +
                ", operation='" + operation + '\'' +
                ", serviceId='" + serviceId + '\'' +
                '}';
    }
}
