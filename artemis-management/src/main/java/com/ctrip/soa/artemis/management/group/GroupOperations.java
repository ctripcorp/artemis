package com.ctrip.soa.artemis.management.group;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupOperations {
    private Long groupId;
    private List<String> operations;

    public GroupOperations() {

    }

    public GroupOperations(Long groupId, List<String> operations) {
        this.groupId = groupId;
        this.operations = operations;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<String> getOperations() {
        return operations;
    }

    public void setOperations(List<String> operations) {
        this.operations = operations;
    }
}
