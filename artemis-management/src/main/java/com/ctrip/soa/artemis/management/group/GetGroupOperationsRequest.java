package com.ctrip.soa.artemis.management.group;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetGroupOperationsRequest {
    private Long groupId;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "GetGroupOperationsRequest{" +
                "groupId=" + groupId +
                '}';
    }
}
