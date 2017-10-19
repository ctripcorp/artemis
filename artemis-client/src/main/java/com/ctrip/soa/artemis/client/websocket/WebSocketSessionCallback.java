package com.ctrip.soa.artemis.client.websocket;

import com.ctrip.soa.artemis.client.common.AddressContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.socket.WebSocketSession;

/**
 * Created by fang_j on 10/07/2016.
 */
public class WebSocketSessionCallback implements ListenableFutureCallback<WebSocketSession> {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketSessionCallback.class);
    private final AddressContext context;
    private final WebSocketSessionContext webSocketSessionContext;

    public WebSocketSessionCallback(final WebSocketSessionContext webSocketSessionContext,
                                    final AddressContext context) {
        this.webSocketSessionContext = webSocketSessionContext;
        this.context = context;
    }
    @Override
    public void onFailure(Throwable ex) {
        logger.warn("connect to websocket endpoint failed", ex);
    }

    @Override
    public void onSuccess(WebSocketSession session) {
       webSocketSessionContext.reset(session, context);
    }
}
