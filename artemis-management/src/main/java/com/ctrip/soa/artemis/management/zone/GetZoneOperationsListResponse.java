package com.ctrip.soa.artemis.management.zone;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetZoneOperationsListResponse implements HasResponseStatus {

    private List<ZoneOperations> zoneOperationsList;
    private ResponseStatus responseStatus;

    @Override
    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public List<ZoneOperations> getZoneOperationsList() {
        return zoneOperationsList;
    }

    public void setZoneOperationsList(List<ZoneOperations> zoneOperationsList) {
        this.zoneOperationsList = zoneOperationsList;
    }
}
