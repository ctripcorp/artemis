package com.ctrip.soa.artemis.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServiceGroupsTest {
    @Test
    public void testFixWeight() {
        Assert.assertEquals(ServiceGroups.DEFAULT_WEIGHT_VALUE, ServiceGroups.fixWeight(null));
        Assert.assertEquals(ServiceGroups.MIN_WEIGHT_VALUE, ServiceGroups.fixWeight(ServiceGroups.MIN_WEIGHT_VALUE));
        Assert.assertEquals(ServiceGroups.MAX_WEIGHT_VALUE, ServiceGroups.fixWeight(ServiceGroups.MAX_WEIGHT_VALUE));

        Assert.assertEquals(ServiceGroups.MIN_WEIGHT_VALUE + 1, ServiceGroups.fixWeight(ServiceGroups.MIN_WEIGHT_VALUE + 1));
        Assert.assertEquals(ServiceGroups.MAX_WEIGHT_VALUE - 1, ServiceGroups.fixWeight(ServiceGroups.MAX_WEIGHT_VALUE - 1));

        Assert.assertEquals(ServiceGroups.DEFAULT_WEIGHT_VALUE, ServiceGroups.fixWeight(ServiceGroups.MIN_WEIGHT_VALUE - 1));
        Assert.assertEquals(ServiceGroups.MAX_WEIGHT_VALUE, ServiceGroups.fixWeight(ServiceGroups.MAX_WEIGHT_VALUE + 1));
    }
}
