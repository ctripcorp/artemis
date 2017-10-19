package com.ctrip.soa.artemis.management.group.model;

import java.sql.Timestamp;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupInstanceModel {
    private Long id;
    private Long groupId;
    private String instanceId;
    private Timestamp createTime;
    private Timestamp updateTime;

    public GroupInstanceModel() {
    }

    public GroupInstanceModel(Long groupId, String instanceId) {
        this.groupId = groupId;
        this.instanceId = instanceId;
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

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
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
