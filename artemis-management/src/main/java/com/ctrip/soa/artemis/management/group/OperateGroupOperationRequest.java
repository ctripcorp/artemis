package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.management.common.OperationContext;

/**
 * Created by fang_j on 10/07/2016.
 */
public class OperateGroupOperationRequest extends OperationContext {
    private Long groupId;
    private String serviceId;
    private String regionId;
    private String zoneId;
    private String name;
    private String appId;
    private String description;
    private String status;
    private String operation;
    private boolean operationComplete;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getOperation() {
        return operation;
    }

    @Override
    public void setOperation(String operation) {
        this.operation = operation;
    }

    public boolean isOperationComplete() {
        return operationComplete;
    }

    public void setOperationComplete(boolean operationComplete) {
        this.operationComplete = operationComplete;
    }

    @Override
    public String toString() {
        return "OperateGroupOperationRequest{" +
                "groupId=" + groupId +
                ", serviceId='" + serviceId + '\'' +
                ", regionId='" + regionId + '\'' +
                ", zoneId='" + zoneId + '\'' +
                ", name='" + name + '\'' +
                ", appId='" + appId + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", operation='" + operation + '\'' +
                ", operationComplete=" + operationComplete +
                "} " + super.toString();
    }
}
