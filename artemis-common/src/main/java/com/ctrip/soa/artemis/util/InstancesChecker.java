package com.ctrip.soa.artemis.util;

import java.util.Collection;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.caravan.common.value.checker.CollectionArgumentChecker;
import com.ctrip.soa.caravan.common.value.checker.ValueChecker;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class InstancesChecker implements ValueChecker<Collection<Instance>> {

    public static final InstancesChecker DEFAULT = new InstancesChecker();

    @Override
    public void check(final Collection<Instance> value, final String valueName) {
        CollectionArgumentChecker.DEFAULT.check(value, "instances");
        for (final Instance instance : value) {
            InstanceChecker.DEFAULT.check(instance, "instance");
        }
    }
}
