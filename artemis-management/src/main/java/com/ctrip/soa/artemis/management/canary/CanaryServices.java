package com.ctrip.soa.artemis.management.canary;

import com.ctrip.soa.artemis.RouteRule;
import com.ctrip.soa.artemis.config.DeploymentConfig;
import com.ctrip.soa.artemis.management.GroupRepository;
import com.ctrip.soa.artemis.management.group.model.GroupModel;
import com.ctrip.soa.artemis.management.group.model.RouteRuleModel;
import com.ctrip.soa.artemis.util.RouteRules;

/**
 * Created by fang_j on 10/07/2016.
 */
public class CanaryServices {
    public static final String CANARY_ZONE_ID = "canary";

    public static RouteRuleModel generateCanaryRouteRule(String serviceId) {
        return new RouteRuleModel(serviceId, RouteRules.CANARY_ROUTE_RULE, null, GroupRepository.RouteRuleStatus.ACTIVE, RouteRule.Strategy.WEIGHTED_ROUND_ROBIN);
    }

    public static GroupModel generateCanaryGroup(String serviceId, String appId) {
        return new GroupModel(serviceId, DeploymentConfig.regionId(), CANARY_ZONE_ID, appId, appId, null, GroupRepository.GroupStatus.ACTIVE);
    }
}
