package com.ctrip.soa.artemis.client.common;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.soa.artemis.client.test.utils.ArtemisClientConstants;
import com.ctrip.soa.caravan.common.value.StringValues;

/**
 * Created by fang_j on 10/07/2016.
 */
public class AddressContextTest {
    private static String webSocketEndpoint = "/ws/endpoint";
    @Test
    public void testAavilable() {
        AddressContext context = newAddressContext(StringValues.EMPTY);
        Assert.assertFalse(context.isAavailable());
        context = newAddressContext("http://localhost:8080");
        Assert.assertTrue(context.isAavailable());
        context.markUnavailable();
        Assert.assertFalse(context.isAavailable());
    }

    @Test
    public void testGetHttpUrl() {
        final AddressContext context = newAddressContext("http://localhost:8080");
        Assert.assertEquals("http://localhost:8080", context.getHttpUrl());
        Assert.assertEquals("http://localhost:8080/path", context.customHttpUrl("path"));
    }

    @Test
    public void testGetWebSocketEndPoint() {
        final AddressContext context = newAddressContext("http://localhost:8080");
        Assert.assertEquals("ws://localhost:8080" + webSocketEndpoint, context.getWebSocketEndPoint());
    }

    @Test
    public void testIsRetired() {
        final AddressContext context = newAddressContext("http://localhost:8080");
        Assert.assertFalse(context.isExpired());
    }

    public AddressContext newAddressContext(final String url) {
        return new AddressContext(ArtemisClientConstants.ClientId, ArtemisClientConstants.ManagerConfig, url, webSocketEndpoint);
    }
}
