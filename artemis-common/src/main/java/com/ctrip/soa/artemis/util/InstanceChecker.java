package com.ctrip.soa.artemis.util;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.common.value.checker.StringArgumentChecker;
import com.ctrip.soa.caravan.common.value.checker.ValueChecker;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class InstanceChecker implements ValueChecker<Instance> {

    public static final InstanceChecker DEFAULT = new InstanceChecker();

    @Override
    public void check(Instance value, String valueName) {
        NullArgumentChecker.DEFAULT.check(value, valueName);
        StringArgumentChecker.DEFAULT.check(value.getServiceId(), valueName + ".serviceId");
        StringArgumentChecker.DEFAULT.check(value.getInstanceId(), valueName + ".instanceId");
        StringArgumentChecker.DEFAULT.check(value.getUrl(), valueName + ".url");
    }

}
