package com.ctrip.soa.artemis.util;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.InstanceChange;
import com.ctrip.soa.artemis.InstanceChange.ChangeType;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public final class InstanceChanges {

    public static final String RELOAD_FAKE_INSTANCE_ID = ChangeType.RELOAD;
    public static final String RELOAD_FAKE_IP = "0.0.0.0";
    public static final String RELOAD_FAKE_URL = "http://serviceId/reload";

    private InstanceChanges() {

    }

    public static InstanceChange newReloadInstanceChange(String serviceId) {
        NullArgumentChecker.DEFAULT.check(serviceId, "serviceId");
        Instance instance = new Instance();
        instance.setServiceId(serviceId);
        instance.setInstanceId(RELOAD_FAKE_INSTANCE_ID);
        instance.setIp(RELOAD_FAKE_IP);
        instance.setUrl(RELOAD_FAKE_URL);
        return new InstanceChange(instance, ChangeType.RELOAD);
    }

}
