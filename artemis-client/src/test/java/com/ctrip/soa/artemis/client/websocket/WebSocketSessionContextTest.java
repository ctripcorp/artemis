package com.ctrip.soa.artemis.client.websocket;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.ctrip.soa.artemis.client.test.utils.ArtemisClientConstants;
import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
public class WebSocketSessionContextTest {
    private final CountDownLatch registrySessionConnected = new CountDownLatch(1);
    private final AtomicBoolean registrySessionIsExpired = new AtomicBoolean(false);
    private final WebSocketSessionContext registrySessionContext = new WebSocketSessionContext(ArtemisClientConstants.RegistryClientConfig) {
        @Override
        protected void afterConnectionEstablished(final WebSocketSession session) {
            registrySessionConnected.countDown();
        }

        @Override
        protected void handleMessage(final WebSocketSession session,
                final WebSocketMessage<?> message) {
        }

        @Override
        protected boolean isExpired() {
            return super.isExpired() || registrySessionIsExpired.get();
        }
    };
    private final CountDownLatch discoverySessionConnected = new CountDownLatch(1);

    private final List<WebSocketSessionContext> sessionContexts = Lists.newArrayList(
            registrySessionContext,
            new WebSocketSessionContext(ArtemisClientConstants.DiscoveryClientConfig) {
                @Override
                protected void afterConnectionEstablished(final WebSocketSession session) {
                    discoverySessionConnected.countDown();
                }

                @Override
                protected void handleMessage(final WebSocketSession session,
                        final WebSocketMessage<?> message) {
                }
            });

    @Before
    public void before() throws InterruptedException {
        for (WebSocketSessionContext sessionContext : sessionContexts) {
            sessionContext.start();
        }
        if (!registrySessionConnected.await(10000, TimeUnit.MILLISECONDS)) {
            Assert.fail("session manager start failed");
        }
        if (!discoverySessionConnected.await(1000, TimeUnit.MILLISECONDS)) {
            Assert.fail("session manager start failed");
        }
        for (final WebSocketSessionContext sessionContext : sessionContexts) {
            Assert.assertNotNull(sessionContext.get());
            Assert.assertTrue(sessionContext.isAvailable());
        }
    }

    @Test(timeout=5000)
    public void testConnect() {
        final WebSocketSession session = registrySessionContext.get();
        if (registrySessionContext.isAvailable()) {
            WebSocketSessionContext.disconnect(session);
            Assert.assertFalse(session.isOpen());
        }
        Assert.assertTrue(registrySessionContext.isAvailable());

        registrySessionContext.connect();
        Assert.assertTrue(registrySessionContext.isAvailable());
    }

    @Test
    public void testIsExpired() {
        Assert.assertFalse(registrySessionContext.isExpired());
    }

    @Test
    public void testCheckHealth() {
        registrySessionIsExpired.set(true);
        registrySessionContext.checkHealth();
        registrySessionIsExpired.set(false);
    }

    @Test
    public void testGet() {
        Assert.assertNotNull(registrySessionContext.get());
    }
}