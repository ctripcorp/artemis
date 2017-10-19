package com.ctrip.soa.artemis.management.group.model;

import com.ctrip.soa.artemis.management.common.OperationContext;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServiceInstanceLogModel extends ServiceInstanceModel {
    private String operation;
    private String operatorId;
    private String token;

    public ServiceInstanceLogModel() {
    }

    public ServiceInstanceLogModel(String serviceId, String instanceId, String operation, String operatorId) {
        super(serviceId, instanceId);
        this.operation = operation;
        this.operatorId = operatorId;
    }

    public ServiceInstanceLogModel(OperationContext operationContext, ServiceInstanceModel serviceInstance) {
        super(serviceInstance.getServiceId(), serviceInstance.getInstanceId(), serviceInstance.getIp(), serviceInstance.getMachineName(),
                serviceInstance.getMetadata(), serviceInstance.getPort(), serviceInstance.getProtocol(), serviceInstance.getRegionId(), serviceInstance.getZoneId(),
                serviceInstance.getHealthCheckUrl(), serviceInstance.getUrl(), serviceInstance.getDescription(), serviceInstance.getGroupId());
        this.operation = operationContext.getOperation();
        this.operatorId = operationContext.getOperatorId();
        this.token = operationContext.getToken();
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
}
