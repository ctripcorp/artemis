package com.ctrip.soa.artemis.management.dao;

import java.sql.Date;

/**
 * Created by fang_j on 10/07/2016.
 */
public class InstanceModel {

    private Long _id;
    private String _regionId;
    private String _serviceId;
    private String _instanceId;
    private String _operation;
    private String _operatorId;
    private String _token;
    private Date _createTime;
    private Date _updateTime;

    public InstanceModel() {

    }

    public InstanceModel(final String regionId, final String serviceId, final String instanceId, final String operation, final String operatorId, final String token) {
        _regionId = regionId;
        _serviceId = serviceId;
        _instanceId = instanceId;
        _operation = operation;
        _operatorId = operatorId;
        _token = token;
    }

    public Long getId() {
        return _id;
    }

    public void setId(final Long id) {
        _id = id;
    }

    public String getRegionId() {
        return _regionId;
    }

    public void setRegionId(final String regionId) {
        _regionId = regionId;
    }

    public String getServiceId() {
        return _serviceId;
    }

    public void setServiceId(final String serviceId) {
        _serviceId = serviceId;
    }

    public String getInstanceId() {
        return _instanceId;
    }

    public void setInstanceId(final String instanceId) {
        _instanceId = instanceId;
    }

    public String getOperation() {
        return _operation;
    }

    public void setOperation(final String operation) {
        _operation = operation;
    }

    public String getOperatorId() {
        return _operatorId;
    }

    public void setOperatorId(final String operatorId) {
        _operatorId = operatorId;
    }

    public String getToken() {
        return _token;
    }

    public void setToken(final String token) {
        _token = token;
    }

    public Date getCreateTime() {
        return _createTime;
    }

    public void setCreateTime(final Date createTime) {
        _createTime = createTime;
    }

    public Date getUpdateTime() {
        return _updateTime;
    }

    public void setUpdateTime(final Date updateTime) {
        _updateTime = updateTime;
    }
}