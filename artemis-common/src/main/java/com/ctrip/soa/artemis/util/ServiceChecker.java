package com.ctrip.soa.artemis.util;

import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.common.value.checker.StringArgumentChecker;
import com.ctrip.soa.caravan.common.value.checker.ValueChecker;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class ServiceChecker implements ValueChecker<Service> {

    public static final ServiceChecker DEFAULT = new ServiceChecker();

    @Override
    public void check(Service value, String valueName) {
        NullArgumentChecker.DEFAULT.check(value, valueName);
        StringArgumentChecker.DEFAULT.check(value.getServiceId(), valueName + ".serviceId");
    }

}
