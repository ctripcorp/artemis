package com.ctrip.soa.artemis.management.instance;

import java.util.List;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetAllInstanceOperationsResponse implements HasResponseStatus {

    private List<InstanceOperations> allInstanceOperations;
    private ResponseStatus responseStatus;

    @Override
    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public List<InstanceOperations> getAllInstanceOperations() {
        return allInstanceOperations;
    }

    public void setAllInstanceOperations(List<InstanceOperations> allInstanceOperations) {
        this.allInstanceOperations = allInstanceOperations;
    }

}
