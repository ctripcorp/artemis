package com.ctrip.soa.artemis.management.group;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetRouteRuleGroupsRequest {
    private Long routeRuleGroupId;
    private Long routeRuleId;
    private Long groupId;

    public Long getRouteRuleGroupId() {
        return routeRuleGroupId;
    }

    public void setRouteRuleGroupId(Long routeRuleGroupId) {
        this.routeRuleGroupId = routeRuleGroupId;
    }

    public Long getRouteRuleId() {
        return routeRuleId;
    }

    public void setRouteRuleId(Long routeRuleId) {
        this.routeRuleId = routeRuleId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "GetRouteRuleGroupsRequest{" +
                "routeRuleGroupId=" + routeRuleGroupId +
                ", routeRuleId=" + routeRuleId +
                ", groupId=" + groupId +
                '}';
    }
}
