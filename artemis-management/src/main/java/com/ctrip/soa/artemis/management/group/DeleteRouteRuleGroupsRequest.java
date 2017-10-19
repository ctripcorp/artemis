package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.management.common.OperationContext;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class DeleteRouteRuleGroupsRequest extends OperationContext {
    private List<Long> routeRuleGroupIds;

    public List<Long> getRouteRuleGroupIds() {
        return routeRuleGroupIds;
    }

    public void setRouteRuleGroupIds(List<Long> routeRuleGroupIds) {
        this.routeRuleGroupIds = routeRuleGroupIds;
    }

    @Override
    public String toString() {
        return "DeleteRouteRuleGroupsRequest{" +
                "routeRuleGroupIds=" + routeRuleGroupIds +
                "} " + super.toString();
    }
}
