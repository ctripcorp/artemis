package com.ctrip.soa.artemis.client.registry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.ctrip.soa.artemis.ErrorCodes;
import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.ResponseStatus;
import com.ctrip.soa.artemis.client.common.ArtemisClientConfig;
import com.ctrip.soa.artemis.client.websocket.WebSocketSessionContext;
import com.ctrip.soa.artemis.registry.FailedInstance;
import com.ctrip.soa.artemis.registry.HeartbeatResponse;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.ctrip.soa.caravan.common.metric.AuditMetric;
import com.ctrip.soa.caravan.common.metric.EventMetric;
import com.ctrip.soa.caravan.common.metric.MetricConfig;
import com.ctrip.soa.caravan.configuration.typed.dynamic.TypedDynamicProperty;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThread;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThreadConfig;
import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

/**
 * Created by fang_j on 10/07/2016.
 */
public class InstanceRegistry {

    private static final Logger _logger = LoggerFactory.getLogger(InstanceRegistry.class);

    private final InstanceRepository _instanceRepository;
    private final TypedDynamicProperty<Integer> _ttl;
    private final TypedDynamicProperty<Integer> _interval;
    private volatile long _lastHeartbeatTime = System.currentTimeMillis();
    private volatile long _heartbeatAcceptStartTime = System.currentTimeMillis();
    private final WebSocketSessionContext _sessionContext;
    private final EventMetric _heartbeatStatus;
    private final AuditMetric _heartbeatPrepareLatency;
    private final AuditMetric _heartbeatSendLatency;
    private final AuditMetric _heartbeatAcceptLatency;
    private final DynamicScheduledThread _heartbeatChecker;

    public InstanceRegistry(final InstanceRepository instanceRepository, final ArtemisClientConfig config) {
        Preconditions.checkArgument(instanceRepository != null, "instance repository");
        Preconditions.checkArgument(config != null, "config");
        _instanceRepository = instanceRepository;
        _ttl = config.properties().getIntProperty(config.key("instance-registry.instance-ttl"), 20 * 1000, 5 * 1000, 24 * 60 * 60 * 1000);
        _interval = config.properties().getIntProperty(config.key("instance-registry.heartbeat-interval"), 5 * 1000, 500, 5 * 60 * 1000);
        _sessionContext = new WebSocketSessionContext(config) {
            @Override
            protected void afterConnectionEstablished(final WebSocketSession session) {

            }

            @Override
            protected void handleMessage(final WebSocketSession session, final WebSocketMessage<?> message) {
                acceptHeartbeat(message);
            }
        };
        _sessionContext.start();

        _heartbeatStatus = config.eventMetricManager().getMetric(config.key("heartbeat.event"),
                new MetricConfig(ImmutableMap.of("metric_name_distribution", config.key("heartbeat.event.distribution"))));
        _heartbeatPrepareLatency = config.valueMetricManager().getMetric(config.key("heartbeat.prepare-latency"),
                new MetricConfig(ImmutableMap.of("metric_name_distribution", config.key("heartbeat.prepare-latency.distribution"), "metric_name_audit",
                        config.key("heartbeat.prepare-latency"))));
        _heartbeatSendLatency = config.valueMetricManager().getMetric(config.key("heartbeat.send-latency"), new MetricConfig(ImmutableMap
                .of("metric_name_distribution", config.key("heartbeat.send-latency.distribution"), "metric_name_audit", config.key("heartbeat.send-latency"))));
        _heartbeatAcceptLatency = config.valueMetricManager().getMetric(config.key("heartbeat.accept-latency"),
                new MetricConfig(ImmutableMap.of("metric_name_distribution", config.key("heartbeat.accept-latency.distribution"), "metric_name_audit",
                        config.key("heartbeat.accept-latency"))));
        final DynamicScheduledThreadConfig dynamicScheduledThreadConfig = new DynamicScheduledThreadConfig(config.properties(),
                new RangePropertyConfig<Integer>(5 * 1000, 1 * 1000, 60 * 1000), new RangePropertyConfig<Integer>(1000, 500, 90 * 1000));
        _heartbeatChecker = new DynamicScheduledThread(config.key("instance-registry.heartbeat-checker"), new Runnable() {
            @Override
            public void run() {
                checkHeartbeat();
            }
        }, dynamicScheduledThreadConfig);
        _heartbeatChecker.setDaemon(true);
        _heartbeatChecker.start();
    }

    protected void acceptHeartbeat(final WebSocketMessage<?> message) {
        try {
            final HeartbeatResponse response = JacksonJsonSerializer.INSTANCE.deserialize((String) message.getPayload(), HeartbeatResponse.class);
            final ResponseStatus status = response.getResponseStatus();
            if (status == null) {
                _heartbeatStatus.addEvent("null");
            } else {
                _heartbeatStatus.addEvent(status.getStatus());
            }

            long heartbeatTime = System.currentTimeMillis() - _heartbeatAcceptStartTime;
            _heartbeatAcceptLatency.addValue(heartbeatTime);

            if (ResponseStatusUtil.isServiceDown(status)) {
                _sessionContext.markdown();
            }
            if (ResponseStatusUtil.isFail(status)) {
                _logger.warn("heartbeat failed: " + status.getMessage());
            } else if (ResponseStatusUtil.isPartialFail(status)) {
                _logger.info("heartbeat partial failed: " + status.getMessage());
            }

            registerToServicesRegistry(response.getFailedInstances());
        } catch (final Throwable e) {
            _logger.error("handle heartbeat message failed", e);
        }
    }

    protected void sendHeartbeat() {
        try {
            if (_sessionContext.get() == null) {
                return;
            }

            long heartbeatPrepareStartTime = System.currentTimeMillis();
            final TextMessage message = _instanceRepository.getHeartbeatMessage();
            _heartbeatPrepareLatency.addValue(System.currentTimeMillis() - heartbeatPrepareStartTime);
            if (message == null) {
                _logger.info("heartbeat message is null");
                _lastHeartbeatTime = System.currentTimeMillis();
                return;
            }

            long heartbeatSendStartTime = System.currentTimeMillis();
            _sessionContext.get().sendMessage(message);
            _heartbeatSendLatency.addValue(System.currentTimeMillis() - heartbeatSendStartTime);
            _lastHeartbeatTime = System.currentTimeMillis();
            _heartbeatAcceptStartTime = System.currentTimeMillis();
        } catch (Throwable e) {
            _logger.warn("send heartbeat failed.", e);
        }
    }

    protected void checkHeartbeat() {
        long heartbeatInterval = System.currentTimeMillis() - _lastHeartbeatTime;
        if (heartbeatInterval >= _ttl.typedValue()) {
            _logger.warn(String.format("heartbeat interval time is more than %d", _ttl.typedValue()));
            _sessionContext.markdown();
        }

        if (heartbeatInterval >= _interval.typedValue()) {
            sendHeartbeat();
        }
    }

    protected void registerToServicesRegistry(final List<FailedInstance> failedInstances) {
        try {
            if (CollectionUtils.isEmpty(failedInstances)) {
                return;
            }

            final Set<Instance> instances = new HashSet<Instance>();
            for (final FailedInstance fs : failedInstances) {
                if (ErrorCodes.DATA_NOT_FOUND.equals(fs.getErrorCode()) || ErrorCodes.UNKNOWN.equals(fs.getErrorCode())) {
                    instances.add(fs.getInstance());
                }
            }
            _instanceRepository.registerToRemote(instances);
        } catch (final Throwable e) {
            _logger.warn("register failed instances failed", e);
        }
    }
}