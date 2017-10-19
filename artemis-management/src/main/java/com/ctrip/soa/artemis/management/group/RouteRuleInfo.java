package com.ctrip.soa.artemis.management.group;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class RouteRuleInfo {
    private ServiceRouteRule routeRule;
    private List<GroupWeight> groups;

    public RouteRuleInfo() {
    }

    public RouteRuleInfo(ServiceRouteRule routeRule, List<GroupWeight> groups) {
        this.routeRule = routeRule;
        this.groups = groups;
    }

    public ServiceRouteRule getRouteRule() {
        return routeRule;
    }

    public void setRouteRule(ServiceRouteRule routeRule) {
        this.routeRule = routeRule;
    }

    public List<GroupWeight> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupWeight> groups) {
        this.groups = groups;
    }
}
