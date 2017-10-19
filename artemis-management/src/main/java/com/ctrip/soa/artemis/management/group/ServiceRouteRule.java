package com.ctrip.soa.artemis.management.group;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServiceRouteRule {
    private Long routeRuleId;
    private String serviceId;
    private String name;
    private String description;
    private String status;
    private String strategy;

    public ServiceRouteRule() {
    }

    public ServiceRouteRule(Long routeRuleId, String serviceId, String name, String description, String status, String strategy) {
        this.routeRuleId = routeRuleId;
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.strategy = strategy;
    }

    public Long getRouteRuleId() {
        return routeRuleId;
    }

    public void setRouteRuleId(Long routeRuleId) {
        this.routeRuleId = routeRuleId;
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

    @Override
    public String toString() {
        return "ServiceRouteRule{" +
                "routeRuleId=" + routeRuleId +
                ", serviceId='" + serviceId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", strategy='" + strategy + '\'' +
                '}';
    }
}
