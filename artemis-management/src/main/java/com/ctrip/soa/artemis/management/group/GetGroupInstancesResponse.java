package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetGroupInstancesResponse implements HasResponseStatus {
    private List<GroupInstance> groupInstances;
    private ResponseStatus responseStatus;

    public List<GroupInstance> getGroupInstances() {
        return groupInstances;
    }

    public void setGroupInstances(List<GroupInstance> groupInstances) {
        this.groupInstances = groupInstances;
    }

    @Override
    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }
}
