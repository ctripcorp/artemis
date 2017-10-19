package com.ctrip.soa.artemis.management.group.model;

import java.sql.Timestamp;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupOperationModel {
    private Long id;
    private Long groupId;
    private String operation;
    private Timestamp createTime;
    private Timestamp updateTime;

    public GroupOperationModel() {
    }

    public GroupOperationModel(Long groupId, String operation) {
        this.groupId = groupId;
        this.operation = operation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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
