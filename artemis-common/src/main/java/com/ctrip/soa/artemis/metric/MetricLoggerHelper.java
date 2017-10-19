package com.ctrip.soa.artemis.metric;

import com.ctrip.soa.artemis.HasResponseStatus;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class MetricLoggerHelper {

    private MetricLoggerHelper() {

    }

    public static void logResponseEvent(final String service, final String operation, final HasResponseStatus response) {
        logResponseEvent(service, operation, response.getResponseStatus().getErrorCode());
    }

    public static void logResponseEvent(final String service, final String operation, final String errorCode) {

    }

    public static void logRegistryEvent(final String errorCode) {

    }

    public static void logSubscribeEvent(final String errorCode, final String serviceId) {

    }

    public static void logPublishEvent(final String errorCode, final String serviceId, final String instanceId, final String changeType) {

    }

    public static void logWebSocketEvent(final String event, final String wsHandlerName, final String remoteHostName) {
    }

    public static void logWebSocketCheckHealthCost(final long cost, final String wsHandlerName) {
    }

    public static void logWebSocketSessionCount(final long count, final String wsHandlerName, final String event) {
    }
}
