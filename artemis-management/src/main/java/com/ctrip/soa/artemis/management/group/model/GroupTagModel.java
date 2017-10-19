package com.ctrip.soa.artemis.management.group.model;

import java.sql.Timestamp;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupTagModel {
    private Long id;
    private Long groupId;
    private String tag;
    private String value;
    private Timestamp createTime;
    private Timestamp updateTime;

    public GroupTagModel() {
    }

    public GroupTagModel(Long groupId, String tag, String value) {
        this.groupId = groupId;
        this.tag = tag;
        this.value = value;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    @Override
    public String toString() {
        return "GroupTagModel{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", tag='" + tag + '\'' +
                ", value='" + value + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
