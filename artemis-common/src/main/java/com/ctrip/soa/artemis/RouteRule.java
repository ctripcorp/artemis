package com.ctrip.soa.artemis;

import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class RouteRule implements Cloneable{
    public interface Strategy {
        String WEIGHTED_ROUND_ROBIN = "weighted-round-robin";
        String CLOSE_BY_VISIT = "close-by-visit";
    }
    private String routeId;
    private String strategy;
    private List<ServiceGroup> groups;

    public RouteRule() {
    }

    public RouteRule(String routeId, String strategy) {
        this(routeId, null, strategy);
    }

    public RouteRule(String routeId, List<ServiceGroup> groups, String strategy) {
        this.routeId = routeId;
        this.groups = groups;
        this.strategy = strategy;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public List<ServiceGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ServiceGroup> groups) {
        this.groups = groups;
    }

    @Override
    public RouteRule clone() {
        return new RouteRule(routeId, strategy);
    }

    @Override
    public String toString() {
        return JacksonJsonSerializer.INSTANCE.serialize(this);
    }
}
