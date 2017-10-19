package com.ctrip.soa.artemis.management.log;

import java.util.List;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetInstanceOperationLogsResponse implements HasResponseStatus {
    private ResponseStatus responseStatus;
    private List<InstanceOperationLog> logs;

    @Override
    public ResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public List<InstanceOperationLog> getLogs() {
        return logs;
    }

    public void setLogs(List<InstanceOperationLog> logs) {
        this.logs = logs;
    }
}