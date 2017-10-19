package com.ctrip.soa.artemis.util;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.ServiceGroup;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.ValueCheckers;

import java.util.List;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public final class ServiceGroups {

    public static final String DEFAULT_GROUP_ID = "default";
    public static final int MAX_WEIGHT_VALUE = 10000;
    public static final int MIN_WEIGHT_VALUE = 0;
    public static final int DEFAULT_WEIGHT_VALUE = 5;

    private ServiceGroups() {

    }

    public static int fixWeight(Integer weight) {
        if (weight == null || weight < MIN_WEIGHT_VALUE) {
            return DEFAULT_WEIGHT_VALUE;
        }

        if (weight > MAX_WEIGHT_VALUE) {
            return MAX_WEIGHT_VALUE;
        }

        return weight;
    }

    public static boolean isDefaultGroupId(String groupId) {
        if (StringValues.isNullOrWhitespace(groupId))
            return true;
        return DEFAULT_GROUP_ID.equalsIgnoreCase(groupId.trim());
    }

    public static boolean isGroupCanaryInstance(String groupKey, Instance instance) {
        if (StringValues.isNullOrWhitespace(groupKey) || instance == null) {
            return false;
        }
        return RouteRules.DEFAULT_GROUP_KEY.equalsIgnoreCase(groupKey.trim()) ||
                ServiceGroupKeys.of(instance).getGroupKey().startsWith(groupKey);
    }

    public static boolean isLocalZone(ServiceGroup serviceGroup) {
        ValueCheckers.notNull(serviceGroup, "serviceGroup");
        if (StringValues.isNullOrWhitespace(serviceGroup.getGroupKey())) {
            return true;
        }

        List<String> keys = ServiceGroupKeys.toGroupIdList(serviceGroup.getGroupKey());
        if (keys.size() >= 3) {
        	String regionId = keys.get(1);
        	String zoneId = keys.get(2);
        	return SameRegionChecker.DEFAULT.isSameRegion(regionId) && SameZoneChecker.DEFAULT.isSameZone(zoneId);
        }

        return true;
    }
}
