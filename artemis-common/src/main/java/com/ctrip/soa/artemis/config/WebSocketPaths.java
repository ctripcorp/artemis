package com.ctrip.soa.artemis.config;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface WebSocketPaths {
    String HEARTBEAT_DESTINATION = "/ws/registry/heartbeat";
    String SERVICE_CHANGE_DESTINATION = "/ws/discovery/instance-change";
    String ALL_SERVICES_CHANGE_DESTINATION = "/ws/discovery/all-instance-change";
}
