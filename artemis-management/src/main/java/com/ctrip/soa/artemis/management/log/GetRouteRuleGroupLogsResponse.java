package com.ctrip.soa.artemis.management.log;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;
import com.ctrip.soa.artemis.management.group.log.RouteRuleGroupLog;
import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetRouteRuleGroupLogsResponse implements HasResponseStatus {
    private ResponseStatus responseStatus;
    private List<RouteRuleGroupLog> logs;

    @Override
    public ResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public List<RouteRuleGroupLog> getLogs() {
        return logs;
    }

    public void setLogs(List<RouteRuleGroupLog> logs) {
        this.logs = logs;
    }
}
