package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.management.common.OperationContext;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class DeleteRouteRulesRequest extends OperationContext{
    private List<Long> routeRuleIds;

    public List<Long> getRouteRuleIds() {
        return routeRuleIds;
    }

    public void setRouteRuleIds(List<Long> routeRuleIds) {
        this.routeRuleIds = routeRuleIds;
    }

    @Override
    public String toString() {
        return "DeleteRouteRulesRequest{" +
                "routeRuleIds=" + routeRuleIds +
                "} " + super.toString();
    }
}
