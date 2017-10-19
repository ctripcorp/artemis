package com.ctrip.soa.artemis.management.group;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupInstance {
    public Long id;
    public Long groupId;
    public String instanceId;

    public GroupInstance() {
    }

    public GroupInstance(Long id, Long groupId, String instanceId) {
        this.id = id;
        this.groupId = groupId;
        this.instanceId = instanceId;
    }

    public GroupInstance(Long groupId, String instanceId) {
        this(null, groupId, instanceId);
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
}
