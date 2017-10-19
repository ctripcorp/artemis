package com.ctrip.soa.artemis.management.zone;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetZoneOperationsResponse implements HasResponseStatus {

    private ZoneOperations operations;
    private ResponseStatus responseStatus;

    @Override
    public ResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    public ZoneOperations getOperations() {
        return operations;
    }

    public void setOperations(ZoneOperations operations) {
        this.operations = operations;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

}
