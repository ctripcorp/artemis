package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetGroupOperationsResponse implements HasResponseStatus {
    private ResponseStatus responseStatus;
    private GroupOperations groupOperations;

    @Override
    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public GroupOperations getGroupOperations() {
        return groupOperations;
    }

    public void setGroupOperations(GroupOperations groupOperations) {
        this.groupOperations = groupOperations;
    }
}
