package com.ctrip.soa.artemis.web.util;

import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketSession;

import java.net.InetSocketAddress;

/**
 * Created by fang_j on 10/07/2016.
 */
public class InetSocketAddressHelper {
    private static final TypedProperty<Boolean> enabled = ArtemisConfig.properties().getBooleanProperty("artemis.service.inet-socket-address.get-host.enabled", true);
    private static final String defaultHostString = "-";
    private InetSocketAddressHelper() {}

    public static String getRemoteIP(InetSocketAddress address) {
        try {
            String ip = null;
            if (enabled.typedValue()) {
                ip = address.getHostString();
            }

            return ip == null ? defaultHostString : ip;
        } catch (Exception e) {
            return defaultHostString;
        }
    }

    public static String getRemoteIP(WebSocketSession session) {
        return getRemoteIP(session.getRemoteAddress());
    }

    public static String getRemoteIP(ServerHttpRequest request) {
        return getRemoteIP(request.getRemoteAddress());
    }
}
