package com.ctrip.soa.artemis.management.group.log;

import java.sql.Timestamp;

/**
 * Created by fang_j on 10/07/2016.
 */
public class RouteRuleGroupLog {
    private Long id;
    private Long routeRuleId;
    private Long groupId;
    private Integer weight;
    private String operation;
    private String operatorId;
    private String token;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String extensions;
    private String reason;

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

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
