package com.ctrip.soa.artemis.management.zone.model;

import java.sql.Timestamp;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ZoneOperationModel {
    private Long id;
    private String regionId;
    private String serviceId;
    private String zoneId;
    private String operation;
    private Timestamp createTime;
    private Timestamp updateTime;

    public ZoneOperationModel() {
    }

    public ZoneOperationModel(String regionId, String serviceId, String zoneId) {
        this.regionId = regionId;
        this.serviceId = serviceId;
        this.zoneId = zoneId;
    }

    public ZoneOperationModel(String operation, String zoneId, String serviceId, String regionId) {
        this.operation = operation;
        this.zoneId = zoneId;
        this.serviceId = serviceId;
        this.regionId = regionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}
