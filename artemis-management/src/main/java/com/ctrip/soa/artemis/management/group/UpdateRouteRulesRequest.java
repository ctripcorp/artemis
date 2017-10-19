package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.management.common.OperationContext;
import com.ctrip.soa.artemis.management.group.ServiceRouteRule;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class UpdateRouteRulesRequest  extends OperationContext {
    private List<ServiceRouteRule> routeRules;

    public List<ServiceRouteRule> getRouteRules() {
        return routeRules;
    }

    public void setRouteRules(List<ServiceRouteRule> routeRules) {
        this.routeRules = routeRules;
    }

    @Override
    public String toString() {
        return "UpdateRouteRulesRequest{" +
                "routeRules=" + routeRules +
                "} " + super.toString();
    }
}
