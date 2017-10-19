package com.ctrip.soa.artemis.management.log;

import java.sql.Timestamp;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServerOperationLog {
    private Long id;
    private String regionId;
    private String serverId;
    private String operation;
    private String operatorId;
    private String token;
    private Timestamp createTime;
    private Timestamp updateTime;
    private boolean complete;
    private String extensions;
    
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }
}
