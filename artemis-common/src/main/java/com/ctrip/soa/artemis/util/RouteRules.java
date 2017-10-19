package com.ctrip.soa.artemis.util;

import com.ctrip.soa.artemis.*;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.ValueCheckers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by fang_j on 10/07/2016.
 */
public class RouteRules {
    public static final String DEFAULT_ROUTE_RULE = "default-route-rule";
    public static final String CANARY_ROUTE_RULE = "canary-route-rule";
    public static final String DEFAULT_GROUP_KEY = "default-group-key";
    public static final String DEFAULT_ROUTE_STRATEGY = RouteRule.Strategy.WEIGHTED_ROUND_ROBIN;

    public static final List<RouteRule> newRouteRules(Service service) {
        ValueCheckers.notNull(service, "service");
        List<RouteRule> routeRules = filterRouteRules(service.getRouteRules());
        Map<String, Instance> groupKey2Instance = generateGroupKey2Instance(service.getInstances());
        Map<String, Instance> instanceId2Instance = generateInstanceId2Instance(service.getInstances(), service.getLogicInstances());
        List<RouteRule> newRouteRules = Lists.newArrayList();
        Set<String> routeRuleIds = Sets.newHashSet();
        for (RouteRule routeRule : routeRules) {
            List<ServiceGroup> nonemptyServiceGroups = Lists.newArrayList();
            for (ServiceGroup serviceGroup : routeRule.getGroups()) {
                if (serviceGroup == null || StringValues.isNullOrWhitespace(serviceGroup.getGroupKey())) {
                    continue;
                }
                List<Instance> instances = generateGroupInstances(serviceGroup, instanceId2Instance, groupKey2Instance);
                if (isCanaryRouteRule(routeRule)) {
                    Map<String, Instance> maps = Maps.newHashMap();
                    for (Instance instance : instances) {
                        maps.put(instance.getInstanceId(), instance);
                    }
                    for (Instance instance : filterInstances(serviceGroup.getInstances())) {
                        if (!maps.containsKey(instance.getInstanceId())) {
                            instances.add(instance);
                        }
                    }
                }
                if (CollectionValues.isNullOrEmpty(instances)) {
                    continue;
                }

                ServiceGroup nonemptyServiceGroup = serviceGroup.clone();
                nonemptyServiceGroup.setWeight(ServiceGroups.fixWeight(nonemptyServiceGroup.getWeight()));
                nonemptyServiceGroup.setInstances(instances);
                nonemptyServiceGroups.add(nonemptyServiceGroup);
            }

            routeRuleIds.add(routeRule.getRouteId().toLowerCase());
            newRouteRules.add(new RouteRule(routeRule.getRouteId(), nonemptyServiceGroups, routeRule.getStrategy()));
        }

        if (!routeRuleIds.contains(DEFAULT_ROUTE_RULE)) {
            newRouteRules.add(newDefaultRouteRule(Lists.newArrayList(groupKey2Instance.values())));
        }

        return newRouteRules;
    }

    public static List<Instance> generateGroupInstances(ServiceGroup serviceGroup, Map<String, Instance> instanceId2Instance, Map<String, Instance> groupKey2Instance) {
        String groupKey = StringValues.toLowerCase(serviceGroup.getGroupKey());
        Map<String, Instance> instances = Maps.newHashMap();
        if (!CollectionValues.isNullOrEmpty(serviceGroup.getInstanceIds())) {
            for (String instanceId : serviceGroup.getInstanceIds()) {
                if (StringValues.isNullOrWhitespace(instanceId)) {
                    continue;
                }
                Instance currentInstance = instanceId2Instance.get(instanceId);
                if (currentInstance != null) {
                    instances.put(instanceId, currentInstance);
                }
            }
        }
        if (!StringValues.isNullOrWhitespace(groupKey)) {
            for (String key : groupKey2Instance.keySet()) {
                if (key.startsWith(groupKey)) {
                    Instance currentInstance = groupKey2Instance.get(key);
                    instances.put(currentInstance.getInstanceId(), currentInstance);
                }
            }
        }

        return Lists.newArrayList(instances.values());
    }

    protected static List<RouteRule> filterRouteRules(List<RouteRule> routeRules) {
        List<RouteRule> filterRouteRules = Lists.newArrayList();
        if (CollectionValues.isNullOrEmpty(routeRules)) {
            return filterRouteRules;
        }
        for (RouteRule routeRule : routeRules) {
            if (routeRule == null || StringValues.isNullOrWhitespace(routeRule.getRouteId()) || CollectionValues.isNullOrEmpty(routeRule.getGroups())) {
                continue;
            }
            filterRouteRules.add(routeRule);
        }
        return filterRouteRules;
    }

    public static List<Instance> filterInstances(List<Instance> instances) {
        List<Instance> filterInstances = Lists.newArrayList();
        if (CollectionValues.isNullOrEmpty(instances)) {
            return filterInstances;
        }
        for (Instance instance : instances) {
            if (instance == null || StringValues.isNullOrWhitespace(instance.getInstanceId())) {
                continue;
            }
            filterInstances.add(instance);
        }
        return filterInstances;
    }

    public static Map<String, Instance> generateGroupKey2Instance(List<Instance> instances) {
        Map<String, Instance> groupInstances = Maps.newHashMap();
        for (Instance instance : filterInstances(instances)) {
            groupInstances.put(ServiceGroupKeys.of(instance).getGroupKey(), instance);
        }
        return groupInstances;
    }

    public static Map<String, Instance> generateInstanceId2Instance(List<Instance> instances, List<Instance> logicInstances) {
        Map<String, Instance> groupInstances = Maps.newHashMap();

        for (Instance instance : filterInstances(logicInstances)) {
            groupInstances.put(instance.getInstanceId(), instance);
        }

        for (Instance instance : filterInstances(instances)) {
            groupInstances.put(instance.getInstanceId(), instance);
        }

        return groupInstances;
    }

    public static RouteRule newDefaultRouteRule(List<Instance> instances) {
        RouteRule routeRule = new RouteRule(DEFAULT_ROUTE_RULE,
                Lists.newArrayList(new ServiceGroup(DEFAULT_GROUP_KEY, ServiceGroups.fixWeight(null), instances, null)),
                DEFAULT_ROUTE_STRATEGY);
        return routeRule;
    }

    public static boolean isDefaultRouteRule(RouteRule routeRule) {
        ValueCheckers.notNull(routeRule, "routeRule");
        return Objects.equals(DEFAULT_ROUTE_RULE, routeRule.getRouteId().toLowerCase());
    }

    public static boolean isCanaryRouteRule(RouteRule routeRule) {
        ValueCheckers.notNull(routeRule, "routeRule");
        return Objects.equals(CANARY_ROUTE_RULE, routeRule.getRouteId().toLowerCase());
    }
}
