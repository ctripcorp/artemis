package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.management.common.OperationContext;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class DeleteGroupsInstancesRequest extends OperationContext {
    private List<Long> groupInstanceIds;

    public List<Long> getGroupInstanceIds() {
        return groupInstanceIds;
    }

    public void setGroupInstanceIds(List<Long> groupInstanceIds) {
        this.groupInstanceIds = groupInstanceIds;
    }

    @Override
    public String toString() {
        return "DeleteGroupsInstancesRequest{" +
                "groupInstanceIds=" + groupInstanceIds +
                '}';
    }
}
