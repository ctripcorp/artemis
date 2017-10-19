package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.management.common.OperationContext;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class InsertGroupInstancesRequest extends OperationContext {
    private List<GroupInstance> groupInstances;

    public List<GroupInstance> getGroupInstances() {
        return groupInstances;
    }

    public void setGroupInstances(List<GroupInstance> groupInstances) {
        this.groupInstances = groupInstances;
    }
}
