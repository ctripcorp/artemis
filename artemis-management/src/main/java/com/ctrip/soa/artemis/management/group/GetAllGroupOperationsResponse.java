package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetAllGroupOperationsResponse implements HasResponseStatus {
    private ResponseStatus responseStatus;
    private List<GroupOperations> allGroupOperations;

    @Override
    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public List<GroupOperations> getAllGroupOperations() {
        return allGroupOperations;
    }

    public void setAllGroupOperations(List<GroupOperations> allGroupOperations) {
        this.allGroupOperations = allGroupOperations;
    }
}
