package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetRouteRuleGroupsResponse implements HasResponseStatus {
    private ResponseStatus responseStatus;
    private List<RouteRuleGroup> routeRuleGroups;

    @Override
    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public List<RouteRuleGroup> getRouteRuleGroups() {
        return routeRuleGroups;
    }

    public void setRouteRuleGroups(List<RouteRuleGroup> routeRuleGroups) {
        this.routeRuleGroups = routeRuleGroups;
    }
}
