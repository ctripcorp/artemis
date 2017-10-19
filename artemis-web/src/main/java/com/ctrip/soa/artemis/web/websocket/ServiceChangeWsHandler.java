package com.ctrip.soa.artemis.web.websocket;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.soa.artemis.web.util.InetSocketAddressHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.ctrip.soa.artemis.InstanceChange;
import com.ctrip.soa.artemis.discovery.DiscoveryConfig;
import com.ctrip.soa.artemis.metric.MetricLoggerHelper;
import com.ctrip.soa.artemis.web.util.Publisher;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServiceChangeWsHandler extends MetricWsHandler implements Publisher {
    private static final Logger logger = LoggerFactory.getLogger(ServiceChangeWsHandler.class);
    private final Map<String, Set<String>> serviceChangeSessions = Maps.newConcurrentMap();

    public ServiceChangeWsHandler() {
        super();
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {
        try {
            final DiscoveryConfig discoveryConfig = JacksonJsonSerializer.INSTANCE.deserialize(message.getPayload(), DiscoveryConfig.class);
            add(discoveryConfig, session);
            MetricLoggerHelper.logSubscribeEvent("success", discoveryConfig.getServiceId());
            logger.info(InetSocketAddressHelper.getRemoteIP(session) + " : " + discoveryConfig);
        } catch (final Exception e) {
            MetricLoggerHelper.logSubscribeEvent("failed", "unknown");
            logger.error("handle service change message failed", e);
        }
    }

    @Override
    public boolean publish(final InstanceChange instanceChange) {
        try {
            if ((instanceChange == null) || (instanceChange.getInstance() == null)) {
                return true;
            }

            final String serviceId = instanceChange.getInstance().getServiceId();
            if (StringValues.isNullOrWhitespace(serviceId)) {
                return true;
            }

            final List<WebSocketSession> sessions = getSessions(serviceId);
            if (CollectionUtils.isEmpty(sessions)) {
                return true;
            }
            final String instanceId = instanceChange.getInstance().getInstanceId();
            final String changeType = instanceChange.getChangeType();

            final TextMessage message = new TextMessage(JacksonJsonSerializer.INSTANCE.serialize(instanceChange));
            for (final WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        synchronized(session){
                            session.sendMessage(message);
                            MetricLoggerHelper.logPublishEvent("success", serviceId, instanceId, changeType);
                        }
                    } catch (final Exception sendException) {
                        MetricLoggerHelper.logPublishEvent("failed", serviceId, instanceId, changeType);
                        logger.error("websocket session send message failed", sendException);
                        try{
                            session.close();
                        } catch (final Exception closeException) {
                            logger.warn("close websocket session failed", closeException);
                        }
                    }
                }
            }
            logger.info(String.format("send instance change message to %d sessions: %s", sessions.size(), instanceChange));
            return true;
        } catch (final Exception e) {
            logger.error("send instance change failed", e);
            return false;
        }
    }

    /**
     * @param discoveryConfig
     * @param session
     */
    private void add(final DiscoveryConfig discoveryConfig, final WebSocketSession session) {
        if ((discoveryConfig == null) || (session == null)) {
            return;
        }

        final String serviceId = discoveryConfig.getServiceId();
        final String sessionId = session.getId();
        if (StringValues.isNullOrWhitespace(serviceId) || StringValues.isNullOrWhitespace(sessionId)) {
            return;
        }

        Set<String> sessionIds = serviceChangeSessions.get(serviceId);
        if (sessionIds == null) {
            synchronized(ServiceChangeWsHandler.class) {
                sessionIds = serviceChangeSessions.get(serviceId);
                if (sessionIds == null) {
                    sessionIds = Sets.newConcurrentHashSet();
                    serviceChangeSessions.put(serviceId, sessionIds);
                }
            }
        }
        sessionIds.add(sessionId);
    }

    private List<WebSocketSession> getSessions(final String serviceId) {
        final List<WebSocketSession> sessions = Lists.newArrayList();
        if (StringValues.isNullOrWhitespace(serviceId)) {
            return sessions;
        }

        final Set<String> sessionIds = serviceChangeSessions.get(serviceId);
        if (CollectionUtils.isEmpty(sessionIds)) {
            return sessions;
        }

        for (final String sessionId : Lists.newArrayList(sessionIds)) {
            final WebSocketSession session = getSession(sessionId);
            if (session != null) {
                sessions.add(session);
            } else {
                sessionIds.remove(sessionId);
            }
        }

        return sessions;
    }

    @Override
    public String name() {
        return "discovery";
    }
}
