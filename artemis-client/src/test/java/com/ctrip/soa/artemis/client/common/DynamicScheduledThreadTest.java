package com.ctrip.soa.artemis.client.common;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.soa.artemis.client.test.utils.ArtemisClientConstants;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThread;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThreadConfig;
import com.google.common.util.concurrent.Runnables;

/**
 * Created by fang_j on 10/07/2016.
 */
public class DynamicScheduledThreadTest {
    @Test
    public void testShutdown() throws InterruptedException {
        DynamicScheduledThreadConfig dynamicScheduledThreadConfig = new DynamicScheduledThreadConfig(ArtemisClientConstants.Properties,
                new RangePropertyConfig<Integer>(20, 0, 200), new RangePropertyConfig<Integer>(500, 500, 5 * 1000));
        DynamicScheduledThread t = new DynamicScheduledThread("client", Runnables.doNothing(), dynamicScheduledThreadConfig);
        t.setDaemon(true);
        t.start();
        t.shutdown();
        Thread.sleep(500);
        Assert.assertFalse(t.isAlive());
    }
}