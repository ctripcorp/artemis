package com.ctrip.soa.artemis.management.group;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetRouteRulesRequest {
    private Long routeRuleId;
    private String serviceId;
    private String name;
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GetRouteRulesRequest{" +
                "routeRuleId=" + routeRuleId +
                ", serviceId='" + serviceId + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}