package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.management.common.OperationContext;
import com.ctrip.soa.artemis.management.group.RouteRuleGroup;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class UpdateRouteRuleGroupsRequest  extends OperationContext {
    private List<RouteRuleGroup> routeRuleGroups;

    public List<RouteRuleGroup> getRouteRuleGroups() {
        return routeRuleGroups;
    }

    public void setRouteRuleGroups(List<RouteRuleGroup> routeRuleGroups) {
        this.routeRuleGroups = routeRuleGroups;
    }

    @Override
    public String toString() {
        return "UpdateRouteRuleGroupsRequest{" +
                "routeRuleGroups=" + routeRuleGroups +
                "} " + super.toString();
    }
}
