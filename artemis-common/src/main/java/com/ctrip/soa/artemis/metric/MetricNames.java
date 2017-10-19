package com.ctrip.soa.artemis.metric;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface MetricNames {

    String METRIC_NAME_KEY_DISTRIBUTION = "distribution";

    String RESPONSE_STATUS_CODE = "artemis.service.response.status-code";
    String SERVICE_TAG = "service";
    String OPERATION_TAG = "operation";

    String WS_REGISTRY_HEARTBEAT_STATUS_CODE = "artemis.service.registry.heartbeat.status-code";

    String WS_SERVICE_DISCOVERY_SUBSCRIBE_STATUS_CODE = "artemis.service.discovery.instance-change.subscribe";
    String WS_SERVICE_DISCOVERY_PUBLISH_STATUS_CODE = "artemis.service.discovery.instance-change.publish";
    String SERVICEID_TAG = "service";
    String INSTANCEID_TAG = "instance";
    String CHANGE_TYPE = "change-type";

    String WS_CONNECT_EVENT = "artemis.service.message.connect.event";
    String WS_SERVICE_TAG = "service";
    String WS_REMOTE_HOST_NAME = "remote";
    String WS_CONNECT_CHECK_HEALTH_COST = "artemis.service.message.connect.check-health.cost";
    String WS_CONNECT_SESSION_COUNT = "artemis.service.message.connect.session.count";
    String WS_CONNECT_SESSION_EVENT_TAG = "event";

}
