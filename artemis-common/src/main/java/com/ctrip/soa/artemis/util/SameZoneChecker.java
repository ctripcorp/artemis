package com.ctrip.soa.artemis.util;

import com.ctrip.soa.artemis.Zone;
import com.ctrip.soa.artemis.config.DeploymentConfig;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.common.value.checker.ValueChecker;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class SameZoneChecker implements ValueChecker<Zone> {

    public static final SameZoneChecker DEFAULT = new SameZoneChecker();

    public void check(String zoneId, String valueName) {
        if (!isSameZone(zoneId))
            throw new IllegalArgumentException(valueName + " is not the current zone." + " expected: " + DeploymentConfig.zoneId() + ", actual: " + zoneId);
    }

    @Override
    public void check(Zone value, String valueName) {
        NullArgumentChecker.DEFAULT.check(value, valueName);
        SameRegionChecker.DEFAULT.check(value.getRegionId(), valueName);
        check(value.getZoneId(), valueName);
    }

    public boolean isSameZone(String zoneId) {
        if (StringValues.isNullOrWhitespace(zoneId))
            return false;

        return zoneId.equalsIgnoreCase(DeploymentConfig.zoneId());
    }

}
