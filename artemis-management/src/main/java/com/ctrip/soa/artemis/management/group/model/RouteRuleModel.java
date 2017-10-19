package com.ctrip.soa.artemis.management.group.model;

import java.sql.Timestamp;

/**
 * Created by fang_j on 10/07/2016.
 */
public class RouteRuleModel {
    private Long id;
    private String serviceId;
    private String name;
    private String description;
    private String status;
    private String strategy;
    private Timestamp createTime;
    private Timestamp updateTime;

    public RouteRuleModel() {
    }

    public RouteRuleModel(String serviceId, String name, String description, String status, String strategy) {
        this(null, serviceId, name, description, status, strategy);
    }

    public RouteRuleModel(Long id, String serviceId, String name, String status) {
        this(id, serviceId, name, null, status, null);
    }

    public RouteRuleModel(Long id, String serviceId, String name, String description, String status, String strategy) {
        this.id = id;
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.strategy = strategy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
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
