package com.ctrip.soa.artemis.management.log;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetZoneOperationLogsResponse implements HasResponseStatus {
    private ResponseStatus responseStatus;
    private List<ZoneOperationLog> logs;

    @Override
    public ResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public List<ZoneOperationLog> getLogs() {
        return logs;
    }

    public void setLogs(List<ZoneOperationLog> logs) {
        this.logs = logs;
    }
}
