package com.ctrip.soa.artemis.util;

import com.ctrip.soa.artemis.Region;
import com.ctrip.soa.artemis.config.DeploymentConfig;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.common.value.checker.ValueChecker;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class SameRegionChecker implements ValueChecker<Region> {

    public static final SameRegionChecker DEFAULT = new SameRegionChecker();

    public void check(String regionId, String valueName) {
        if (!isSameRegion(regionId))
            throw new IllegalArgumentException(
                    valueName + " is not the current region." + " expected: " + DeploymentConfig.regionId() + ", actual: " + regionId);
    }

    @Override
    public void check(Region value, String valueName) {
        NullArgumentChecker.DEFAULT.check(value, valueName);
        check(value.getRegionId(), valueName);
    }

    public boolean isSameRegion(String regionId) {
        if (StringValues.isNullOrWhitespace(regionId))
            return false;

        return regionId.equalsIgnoreCase(DeploymentConfig.regionId());
    }

}
