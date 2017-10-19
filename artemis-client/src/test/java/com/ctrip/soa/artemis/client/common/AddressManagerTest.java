package com.ctrip.soa.artemis.client.common;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ctrip.soa.artemis.client.test.utils.ArtemisClientConstants;

/**
 * Created by fang_j on 10/07/2016.
 */
public class AddressManagerTest {
    private final AddressManager addr = ArtemisClientConstants.DiscoveryClientConfig.addressManager();

    @Before
    public void before() throws InterruptedException, ExecutionException, TimeoutException {
        final AddressContext cxt = addr.getContext();
        Assert.assertNotNull(cxt.getHttpUrl());
    }

    @Test
    public void testMarkUnavailable() throws InterruptedException, ExecutionException, TimeoutException{
        final AddressContext context = addr.getContext();
        Assert.assertTrue(context.isAavailable());
        context.markUnavailable();
        Assert.assertFalse(context.isAavailable());
        final AddressContext newContext = addr.getContext();
        Assert.assertFalse(context == newContext);
        Assert.assertNotNull(newContext.getHttpUrl());
    }
}
