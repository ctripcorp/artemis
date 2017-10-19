package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetRouteRulesResponse implements HasResponseStatus {
    private ResponseStatus responseStatus;
    private List<ServiceRouteRule> routeRules;

    @Override
    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public List<ServiceRouteRule> getRouteRules() {
        return routeRules;
    }

    public void setRouteRules(List<ServiceRouteRule> routeRules) {
        this.routeRules = routeRules;
    }
}
