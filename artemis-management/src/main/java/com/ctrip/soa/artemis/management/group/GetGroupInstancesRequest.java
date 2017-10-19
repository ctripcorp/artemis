package com.ctrip.soa.artemis.management.group;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetGroupInstancesRequest {
    private Long groupId;
    private String instanceId;

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

    @Override
    public String toString() {
        return "GetGroupInstancesRequest{" +
                "groupId=" + groupId +
                ", instanceId='" + instanceId + '\'' +
                '}';
    }
}
