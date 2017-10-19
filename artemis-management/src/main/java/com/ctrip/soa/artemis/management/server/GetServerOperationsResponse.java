package com.ctrip.soa.artemis.management.server;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetServerOperationsResponse implements HasResponseStatus {

    private ServerOperations operations;
    private ResponseStatus responseStatus;

    @Override
    public ResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    public ServerOperations getOperations() {
        return operations;
    }

    public void setOperations(ServerOperations operations) {
        this.operations = operations;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

}
