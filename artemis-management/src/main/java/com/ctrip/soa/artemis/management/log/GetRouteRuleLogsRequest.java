package com.ctrip.soa.artemis.management.log;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetRouteRuleLogsRequest {
    private String serviceId;
    private String name;
    private String operation;
    private String operatorId;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
