package com.ctrip.soa.artemis.util;

import com.ctrip.soa.artemis.discovery.DiscoveryConfig;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.common.value.checker.StringArgumentChecker;
import com.ctrip.soa.caravan.common.value.checker.ValueChecker;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class DiscoveryConfigChecker implements ValueChecker<DiscoveryConfig> {

    public static final DiscoveryConfigChecker DEFAULT = new DiscoveryConfigChecker();

    @Override
    public void check(DiscoveryConfig value, String valueName) {
        NullArgumentChecker.DEFAULT.check(value, valueName);
        StringArgumentChecker.DEFAULT.check(value.getServiceId(), "value.serviceId");
    }

}
