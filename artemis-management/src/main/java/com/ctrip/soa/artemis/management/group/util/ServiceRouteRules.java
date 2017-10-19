package com.ctrip.soa.artemis.management.group.util;

import com.ctrip.soa.artemis.management.common.OperationContext;
import com.ctrip.soa.artemis.management.group.ServiceRouteRule;
import com.ctrip.soa.artemis.management.group.model.RouteRuleLogModel;
import com.ctrip.soa.artemis.management.group.model.RouteRuleModel;
import com.ctrip.soa.caravan.common.delegate.Func1;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServiceRouteRules {
    public static ServiceRouteRule newServiceRouteRule(RouteRuleModel model) {
        return new ServiceRouteRule(model.getId(), model.getServiceId(), model.getName(), model.getDescription(), model.getStatus(), model.getStrategy());
    }

    public static RouteRuleModel newRouteRuleModel(ServiceRouteRule serviceRouteRule) {
        return new RouteRuleModel(serviceRouteRule.getServiceId(), serviceRouteRule.getName(), serviceRouteRule.getDescription(), serviceRouteRule.getStatus(), serviceRouteRule.getStrategy());
    }

    public static List<ServiceRouteRule> newServiceRouteRules(List<RouteRuleModel> models) {
        return Converts.convert(models, new Func1<RouteRuleModel, ServiceRouteRule>() {
            @Override
            public ServiceRouteRule execute(RouteRuleModel model) {
                return newServiceRouteRule(model);
            }
        });
    }

    public static List<RouteRuleModel> newRouteRuleModels(List<ServiceRouteRule> serviceRouteRules) {
        return Converts.convert(serviceRouteRules, new Func1<ServiceRouteRule, RouteRuleModel>() {
            @Override
            public RouteRuleModel execute(ServiceRouteRule serviceRouteRule) {
                return newRouteRuleModel(serviceRouteRule);
            }
        });
    }

    public static List<RouteRuleLogModel> newRouteRuleLogModels(OperationContext operator, List<RouteRuleModel> routeRules) {
        List<RouteRuleLogModel> logs = Lists.newArrayList();
        if (operator == null && CollectionValues.isNullOrEmpty(routeRules)) {
            return logs;
        }
        for (RouteRuleModel routeRule : routeRules) {
            if (routeRule == null) {
                continue;
            }
            logs.add(new RouteRuleLogModel(routeRule, operator));
        }
        return logs;
    }
}
