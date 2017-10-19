package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.management.common.OperationContext;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class InsertRouteRulesRequest extends OperationContext {
    private List<ServiceRouteRule> routeRules;

    public List<ServiceRouteRule> getRouteRules() {
        return routeRules;
    }

    public void setRouteRules(List<ServiceRouteRule> routeRules) {
        this.routeRules = routeRules;
    }

    @Override
    public String toString() {
        return "InsertRouteRulesRequest{" +
                "routeRules=" + routeRules +
                "} " + super.toString();
    }
}
