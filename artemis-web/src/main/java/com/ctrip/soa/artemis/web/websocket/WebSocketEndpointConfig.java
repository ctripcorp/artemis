package com.ctrip.soa.artemis.web.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.ctrip.soa.artemis.config.WebSocketPaths;

/**
 * Created by fang_j on 10/07/2016.
 */
@Configuration
@EnableWebSocket
public class WebSocketEndpointConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        registry.addHandler(heartbeatWsHandler(), WebSocketPaths.HEARTBEAT_DESTINATION).setAllowedOrigins("*")
                .addInterceptors(new WsIPBlackList("service-heartbeat"));
        registry.addHandler(serviceChangeWsHandler(), WebSocketPaths.SERVICE_CHANGE_DESTINATION).setAllowedOrigins("*")
                .addInterceptors(new WsIPBlackList("service-discovery"));
        registry.addHandler(allServicesChangeWsHandler(), WebSocketPaths.ALL_SERVICES_CHANGE_DESTINATION).setAllowedOrigins("*")
                .addInterceptors(new WsIPBlackList("service-discoveries"));
    }

    @Bean
    public HeartbeatWsHandler heartbeatWsHandler() {
        HeartbeatWsHandler handler = new HeartbeatWsHandler();
        handler.start();
        return handler;
    }

    @Bean
    public ServiceChangeWsHandler serviceChangeWsHandler() {
        ServiceChangeWsHandler handler = new ServiceChangeWsHandler();
        handler.start();
        return handler;
    }

    @Bean
    public AllServicesChangeWsHandler allServicesChangeWsHandler() {
        AllServicesChangeWsHandler handler = new AllServicesChangeWsHandler();
        handler.start();
        return handler;
    }
}