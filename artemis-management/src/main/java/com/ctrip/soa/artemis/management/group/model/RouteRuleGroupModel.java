package com.ctrip.soa.artemis.management.group.model;

import java.sql.Timestamp;

/**
 * Created by fang_j on 10/07/2016.
 */
public class RouteRuleGroupModel {
    private Long id;
    private Long routeRuleId;
    private Long groupId;
    private Integer weight;
    private Integer unreleasedWeight;
    private Timestamp createTime;
    private Timestamp updateTime;

    public RouteRuleGroupModel() {
    }

    public RouteRuleGroupModel(Long routeRuleId, Long groupId){
        this(null, routeRuleId, groupId, null);
    }

    public RouteRuleGroupModel(Long routeRuleId, Long groupId, Integer unreleasedWeight) {
        this(null, routeRuleId, groupId, unreleasedWeight);
    }

    public RouteRuleGroupModel(Long id, Long routeRuleId, Long groupId) {
        this(id, routeRuleId, groupId, null);
    }

    public RouteRuleGroupModel(Long id, Long routeRuleId, Long groupId, Integer unreleasedWeight) {
        this.id = id;
        this.routeRuleId = routeRuleId;
        this.groupId = groupId;
        this.unreleasedWeight = unreleasedWeight;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}
