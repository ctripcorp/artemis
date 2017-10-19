package com.ctrip.soa.artemis.management.zone;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

/**
 * Created by fang_j on 10/07/2016.
 */
public class OperateZoneOperationsResponse implements HasResponseStatus {

    private ResponseStatus responseStatus;

    @Override
    public ResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

}
