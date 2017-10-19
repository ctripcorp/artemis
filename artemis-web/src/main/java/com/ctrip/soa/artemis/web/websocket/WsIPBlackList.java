package com.ctrip.soa.artemis.web.websocket;

import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.web.util.InetSocketAddressHelper;
import com.ctrip.soa.caravan.common.value.checker.ValueCheckers;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fang_j on 10/07/2016.
 */
public class WsIPBlackList implements HandshakeInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(WsIPBlackList.class);
    private final TypedProperty<Boolean> ipBlackListEnabled;
    private final TypedProperty<List<String>> ipBlackList;

    public WsIPBlackList(final String ipBlackListId) {
        ValueCheckers.notNullOrWhiteSpace(ipBlackListId, "ipBlackListId");
        ipBlackListEnabled = ArtemisConfig.properties().getBooleanProperty("artemis.service." + ipBlackListId + ".ws-ip.black-list.enabled", true);
        ipBlackList = ArtemisConfig.properties().getListProperty("artemis.service." + ipBlackListId + ".ws-ip.black-list", new ArrayList<String>());
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        try {
            if (ipBlackListEnabled.typedValue()) {
                String ip = InetSocketAddressHelper.getRemoteIP(request);
                for (String blackIp : ipBlackList.typedValue()) {
                    if (ip.equalsIgnoreCase(blackIp)) {
                        return false;
                    }
                }
            }
        } catch (Throwable ex) {
            logger.warn("process WebSocket ip black list failed" + ex.getMessage(), ex);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
