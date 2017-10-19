package com.ctrip.soa.artemis.util;

import java.util.List;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.Region;
import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.ServiceGroupKey;
import com.ctrip.soa.artemis.Zone;
import com.ctrip.soa.caravan.common.value.ArrayValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.google.common.base.Splitter;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public final class ServiceGroupKeys {

    private ServiceGroupKeys() {

    }

    public static ServiceGroupKey of(Service service) {
        NullArgumentChecker.DEFAULT.check(service, "service");

        return of(service.getServiceId());
    }

    public static ServiceGroupKey of(Service service, Region region) {
        NullArgumentChecker.DEFAULT.check(service, "service");
        NullArgumentChecker.DEFAULT.check(region, "region");

        return of(service.getServiceId(), region.getRegionId());
    }

    public static ServiceGroupKey of(Service service, Zone zone) {
        NullArgumentChecker.DEFAULT.check(service, "service");
        NullArgumentChecker.DEFAULT.check(zone, "zone");

        return of(service.getServiceId(), zone.getRegionId(), zone.getZoneId());
    }

    public static ServiceGroupKey of(Instance instance) {
        NullArgumentChecker.DEFAULT.check(instance, "instance");
        String groupId = instance.getGroupId();
        groupId = ServiceGroups.isDefaultGroupId(groupId) ? ServiceGroups.DEFAULT_GROUP_ID : groupId;
        return of(instance.getServiceId(), instance.getRegionId(), instance.getZoneId(), groupId, instance.getInstanceId());
    }

    public static ServiceGroupKey of(String... groupIds) {
        ArrayValues.checkNullOrEmpty(groupIds, "groupIds");

        String groupKey = StringValues.toLowerCase(StringValues.concatPathParts(groupIds));
        return new ServiceGroupKey(groupKey);
    }

    public static List<String> toGroupIdList(ServiceGroupKey serviceGroupKey) {
        NullArgumentChecker.DEFAULT.check(serviceGroupKey, "serviceGroupKey");
        return toGroupIdList(serviceGroupKey.getGroupKey());
    }

    public static List<String> toGroupIdList(String serviceGroupKey) {
        NullArgumentChecker.DEFAULT.check(serviceGroupKey, "serviceGroupKey");
        return Splitter.on('/').omitEmptyStrings().splitToList(serviceGroupKey.toLowerCase());
    }

}
