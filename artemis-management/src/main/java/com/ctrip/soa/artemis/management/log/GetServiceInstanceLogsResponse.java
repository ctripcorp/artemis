package com.ctrip.soa.artemis.management.log;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;
import com.ctrip.soa.artemis.management.group.log.ServiceInstanceLog;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetServiceInstanceLogsResponse implements HasResponseStatus {
    private ResponseStatus responseStatus;
    private List<ServiceInstanceLog> logs;

    @Override
    public ResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public List<ServiceInstanceLog> getLogs() {
        return logs;
    }

    public void setLogs(List<ServiceInstanceLog> logs) {
        this.logs = logs;
    }
}
