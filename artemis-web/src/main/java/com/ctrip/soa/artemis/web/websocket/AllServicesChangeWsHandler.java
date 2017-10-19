package com.ctrip.soa.artemis.web.websocket;

import com.ctrip.soa.artemis.InstanceChange;
import com.ctrip.soa.artemis.metric.MetricLoggerHelper;
import com.ctrip.soa.artemis.web.util.Publisher;
import com.ctrip.soa.caravan.common.value.MapValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class AllServicesChangeWsHandler extends MetricWsHandler implements Publisher {
    private static final Logger logger = LoggerFactory.getLogger(AllServicesChangeWsHandler.class);

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

            final String instanceId = instanceChange.getInstance().getInstanceId();
            final String changeType = instanceChange.getChangeType();

            if (MapValues.isNullOrEmpty(sessions)) {
                return true;
            }
            final TextMessage message = new TextMessage(JacksonJsonSerializer.INSTANCE.serialize(instanceChange));
            List<WebSocketSession> allSessions = Lists.newArrayList(sessions.values());
            for (final WebSocketSession session : allSessions) {
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

    @Override
    public String name() {
        return "discoveries";
    }
}
