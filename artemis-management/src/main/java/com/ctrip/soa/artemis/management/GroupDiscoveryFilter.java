package com.ctrip.soa.artemis.management;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.RouteRule;
import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.ServiceGroup;
import com.ctrip.soa.artemis.discovery.DiscoveryConfig;
import com.ctrip.soa.artemis.discovery.DiscoveryFilter;
import com.ctrip.soa.artemis.util.RouteRules;
import com.ctrip.soa.caravan.common.value.CollectionValues;

import java.util.List;
import java.util.Map;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupDiscoveryFilter implements DiscoveryFilter {

    private static volatile GroupDiscoveryFilter instance;

    public static GroupDiscoveryFilter getInstance() {
        if (instance == null) {
            synchronized (GroupDiscoveryFilter.class) {
                if (instance == null)
                    instance = new GroupDiscoveryFilter();
            }
        }

        return instance;
    }

    private final GroupRepository groupRepository = GroupRepository.getInstance();

    @Override
    public void filter(Service service, DiscoveryConfig discoveryConfig) {
        if (service== null || discoveryConfig == null) {
            return;
        }
        service.setLogicInstances(groupRepository.getServiceInstances(service.getServiceId()));

        List<RouteRule> routeRules = groupRepository.getServiceRouteRules(service.getServiceId(), discoveryConfig.getRegionId());
        if (!CollectionValues.isNullOrEmpty(routeRules)) {
            for (RouteRule routeRule : routeRules) {
                if (routeRule == null) {
                    continue;
                }
                if (RouteRules.isCanaryRouteRule(routeRule)) {
                    Map<String, Instance> groupKey2Instance = RouteRules.generateGroupKey2Instance(service.getInstances());
                    Map<String, Instance> instanceId2Instance = RouteRules.generateInstanceId2Instance(service.getInstances(), service.getLogicInstances());
                    for (ServiceGroup serviceGroup : routeRule.getGroups()) {
                        serviceGroup.setInstances(RouteRules.generateGroupInstances(serviceGroup, instanceId2Instance, groupKey2Instance));
                    }
                    break;
                }
            }
        }
        service.setRouteRules(routeRules);

    }
}