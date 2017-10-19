package com.ctrip.soa.artemis.web.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ctrip.soa.artemis.web.util.InetSocketAddressHelper;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.metric.MetricLoggerHelper;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThread;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThreadConfig;
import com.google.common.collect.Maps;

/**
 * Created by fang_j on 10/07/2016.
 */
public class MetricWsHandler extends TextWebSocketHandler {
    private static final Logger _logger = LoggerFactory.getLogger(MetricWsHandler.class);
    protected final Map<String, WebSocketSession> sessions = Maps.newConcurrentMap();
    protected final DelayQueue<DelayItem<String>> expiredSessions = new DelayQueue<>();
    private final DynamicScheduledThread _healthChecker;
    private final TypedProperty<Integer> sessionTTL;
    private final AtomicBoolean started = new AtomicBoolean(false);

    public MetricWsHandler() {
        final DynamicScheduledThreadConfig dynamicScheduledThreadConfig = new DynamicScheduledThreadConfig(ArtemisConfig.properties(),
                new RangePropertyConfig<Integer>(20, 0, 200), new RangePropertyConfig<Integer>(60 * 1000, 10 * 1000, 60 * 60 * 1000));
        sessionTTL = ArtemisConfig.properties().getIntProperty("artemis.service.websocket.session.ttl", 6 * 60 * 1000, 5 * 60 * 1000, 5 * 60 * 60 * 1000);
        _healthChecker = new DynamicScheduledThread("artemis.service.websocket.session.health-checker", new Runnable() {
            @Override
            public void run() {
                try {
                    closeExpiredSessions();
                    MetricLoggerHelper.logWebSocketSessionCount(sessions.size(), name(), "connected");
                } catch (final Exception e) {
                    _logger.error("check health failed", e);
                }
            }
        }, dynamicScheduledThreadConfig);
        _healthChecker.setDaemon(true);
    }

    public void start() {
        if (started.compareAndSet(false, true)) {
            _healthChecker.start();
        }
    }

    public String name() {
        return "metric";
    }

    public int connections() {
        return sessions.size();
    }

    protected WebSocketSession getSession(final String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    protected void handlePongMessage(final WebSocketSession session, final PongMessage message) throws Exception {
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        final String sessionId = session.getId();
        sessions.put(sessionId, session);
        expiredSessions.put(new DelayItem<>(sessionId, sessionTTL.typedValue(), TimeUnit.MILLISECONDS));
        MetricLoggerHelper.logWebSocketEvent("established", name(), InetSocketAddressHelper.getRemoteIP(session));
    }

    @Override
    public void handleTransportError(final WebSocketSession session, final Throwable exception) throws Exception {
        remove(session);
        _logger.warn("transport error", exception);
        MetricLoggerHelper.logWebSocketEvent("transport-error", name(), InetSocketAddressHelper.getRemoteIP(session));
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus closeStatus) throws Exception {
        remove(session);
        if (closeStatus != CloseStatus.NORMAL) {
            _logger.warn("closed:" + closeStatus);
        }
        MetricLoggerHelper.logWebSocketEvent("closed", name(), InetSocketAddressHelper.getRemoteIP(session));
    }

    private void remove(final WebSocketSession session) throws IOException {
        if (session == null) {
            return;
        }
        if (session.isOpen()) {
            session.close();
        }
        sessions.remove(session.getId());
    }

    private void closeExpiredSessions() {
        int count = 0;
        while (true) {
            try {
                DelayItem<String> delaySessionId = expiredSessions.poll();
                if (delaySessionId == null) {
                    break;
                }
                WebSocketSession session = sessions.get(delaySessionId.item());
                if (session != null) {
                    remove(session);
                    count++;
                }
            } catch (final Throwable t) {
                _logger.error("close expired session failed", t);
            }
        }
        if (count > 0) {
            MetricLoggerHelper.logWebSocketSessionCount(count, name(), "expired");
            _logger.info(String.format("close %d expired sessions", count));
        }
    }
}
