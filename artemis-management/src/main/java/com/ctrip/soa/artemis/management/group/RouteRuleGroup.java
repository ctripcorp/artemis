package com.ctrip.soa.artemis.management.group;

/**
 * Created by fang_j on 10/07/2016.
 */
public class RouteRuleGroup {
    private Long routeRuleGroupId;
    private Long routeRuleId;
    private Long groupId;
    private Integer weight;
    private Integer unreleasedWeight;

    public RouteRuleGroup() {
    }

    public RouteRuleGroup(Long routeRuleGroupId, Long routeRuleId, Long groupId, Integer unreleasedWeight) {
        this(routeRuleGroupId, routeRuleId, groupId, null, unreleasedWeight);
    }

    public RouteRuleGroup(Long routeRuleGroupId, Long routeRuleId, Long groupId, Integer weight, Integer unreleasedWeight) {
        this.routeRuleGroupId = routeRuleGroupId;
        this.routeRuleId = routeRuleId;
        this.groupId = groupId;
        this.weight = weight;
        this.unreleasedWeight = unreleasedWeight;
    }

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

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getUnreleasedWeight() {
        return unreleasedWeight;
    }

    public void setUnreleasedWeight(Integer unreleasedWeight) {
        this.unreleasedWeight = unreleasedWeight;
    }

    @Override
    public String toString() {
        return "RouteRuleGroup{" +
                "routeRuleGroupId=" + routeRuleGroupId +
                ", routeRuleId=" + routeRuleId +
                ", groupId=" + groupId +
                ", weight=" + weight +
                ", unreleasedWeight=" + unreleasedWeight +
                '}';
    }
}
