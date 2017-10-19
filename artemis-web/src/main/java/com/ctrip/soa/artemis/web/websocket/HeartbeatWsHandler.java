package com.ctrip.soa.artemis.web.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.ctrip.soa.artemis.metric.MetricLoggerHelper;
import com.ctrip.soa.artemis.registry.HeartbeatRequest;
import com.ctrip.soa.artemis.registry.HeartbeatResponse;
import com.ctrip.soa.artemis.registry.RegistryService;
import com.ctrip.soa.artemis.registry.RegistryServiceImpl;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;

/**
 * Created by fang_j on 10/07/2016.
 */
public class HeartbeatWsHandler extends MetricWsHandler {
    private static final Logger logger = LoggerFactory.getLogger(HeartbeatWsHandler.class);
    private static final TextMessage defaultMessage;
    private final RegistryService registryService = RegistryServiceImpl.getInstance();

    public HeartbeatWsHandler() {
        super();
    }

    static {
        final HeartbeatResponse response = new HeartbeatResponse();
        response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
        defaultMessage = new TextMessage(JacksonJsonSerializer.INSTANCE.serialize(response));
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {
        TextMessage returnMessage = null;
        try {
            final HeartbeatResponse response = registryService.heartbeat(JacksonJsonSerializer.INSTANCE.deserialize(message.getPayload(), HeartbeatRequest.class));
            returnMessage = new TextMessage(JacksonJsonSerializer.INSTANCE.serialize(response));
            MetricLoggerHelper.logRegistryEvent(response.getResponseStatus().getErrorCode());
        } catch (final Exception e){
            logger.error("convert heartbeat message failed", e);
        }
        if (returnMessage != null) {
            session.sendMessage(returnMessage);
        } else {
            session.sendMessage(defaultMessage);
        }
    }

    @Override
    public String name() {
        return "registry";
    }
}
