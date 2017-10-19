package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.management.common.OperationContext;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ReleaseRouteRuleGroupsRequest extends OperationContext {
    private List<RouteRuleGroup> routeRuleGroups;
    public List<RouteRuleGroup> getRouteRuleGroups() {
        return routeRuleGroups;
    }

    public void setRouteRuleGroups(List<RouteRuleGroup> routeRuleGroups) {
        this.routeRuleGroups = routeRuleGroups;
    }

    @Override
    public String toString() {
        return "ReleaseRouteRuleGroupsRequest{" +
                "routeRuleGroups=" + routeRuleGroups +
                "} " + super.toString();
    }
}
