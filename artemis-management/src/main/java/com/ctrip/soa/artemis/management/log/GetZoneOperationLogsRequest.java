package com.ctrip.soa.artemis.management.log;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetZoneOperationLogsRequest {
    private String regionId;
    private String serviceId;
    private String zoneId;
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

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
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
        return "GetZoneOperationLogsRequest{" +
                "regionId='" + regionId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", zoneId='" + zoneId + '\'' +
                ", operation='" + operation + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", complete=" + complete +
                '}';
    }
}
