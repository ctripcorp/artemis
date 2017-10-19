package com.ctrip.soa.artemis.management.instance;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetInstanceOperationsResponse implements HasResponseStatus {

    private InstanceOperations operations;
    private ResponseStatus responseStatus;

    @Override
    public ResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    public InstanceOperations getOperations() {
        return operations;
    }

    public void setOperations(InstanceOperations operations) {
        this.operations = operations;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

}
