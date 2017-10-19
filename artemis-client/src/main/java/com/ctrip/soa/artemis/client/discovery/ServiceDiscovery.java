package com.ctrip.soa.artemis.client.discovery;

import com.ctrip.soa.artemis.InstanceChange;
import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.client.common.ArtemisClientConfig;
import com.ctrip.soa.artemis.client.websocket.WebSocketSessionContext;
import com.ctrip.soa.artemis.discovery.DiscoveryConfig;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThread;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThreadConfig;
import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);
    private final ServiceRepository serviceRepository;
    private final ArtemisDiscoveryHttpClient discoveryHttpClient;
    private final TypedProperty<Long> ttl;
    private volatile long lastUpdateTime = System.currentTimeMillis();
    private final WebSocketSessionContext sessionContext;
    private final DynamicScheduledThread poller;
    protected final Map<String, DiscoveryConfig> reloadFailedDiscoveryConfigs = Maps.newConcurrentMap();

    public ServiceDiscovery(final ServiceRepository serviceRepository, final ArtemisClientConfig config) {
        Preconditions.checkArgument(serviceRepository != null, "ServiceRepository should not be null");
        this.serviceRepository = serviceRepository;
        this.discoveryHttpClient = new ArtemisDiscoveryHttpClient(config);
        ttl = config.properties().getLongProperty(config.key("service-discovery.ttl"), 15 * 60 * 1000L, 60 * 1000, 24 * 60 * 60 * 1000);
        sessionContext = new WebSocketSessionContext(config) {
            @Override
            protected void afterConnectionEstablished(final WebSocketSession session) {
                subscribe(session);
            }

            @Override
            protected void handleMessage(final WebSocketSession session, final WebSocketMessage<?> message) {
                try {
                    InstanceChange instanceChange = JacksonJsonSerializer.INSTANCE.deserialize((String) message.getPayload(), InstanceChange.class);
                    onInstanceChange(instanceChange);
                } catch (final Throwable e) {
                    logger.warn("convert message failed", e);
                }
            }
        };
        sessionContext.start();

        final DynamicScheduledThreadConfig dynamicScheduledThreadConfig = new DynamicScheduledThreadConfig(config.properties(),
                new RangePropertyConfig<Integer>(0, 0, 200), new RangePropertyConfig<Integer>(60 * 1000, 60 * 1000, 24 * 60 * 60 * 1000));
        poller = new DynamicScheduledThread(config.key("service-discovery"), new Runnable() {
            @Override
            public void run() {
                try {
                    reload(getReloadDiscoveryConfigs());
                } catch (Throwable t) {
                    logger.warn("reload services failed", t);
                }
            }
        }, dynamicScheduledThreadConfig);
        poller.setDaemon(true);
        poller.start();
    }

    public void registerDiscoveryConfig(DiscoveryConfig config) {
        subscribe(sessionContext.get(), config);
    }

    public Service getService(DiscoveryConfig config) {
        return discoveryHttpClient.getService(config);
    }

    protected void onServiceChange(Service service) {
        serviceRepository.update(service);
    }

    protected void onInstanceChange(InstanceChange instanceChange) {
        final String serviceId = instanceChange.getInstance().getServiceId();
        if (InstanceChange.ChangeType.RELOAD.equals(instanceChange.getChangeType())) {
            reload(serviceRepository.getDiscoveryConfig(serviceId));
        } else {
            serviceRepository.update(instanceChange);
        }
    }

    protected List<DiscoveryConfig> getReloadDiscoveryConfigs() {
        if (expired()) {
            return serviceRepository.getDiscoveryConfigs();
        }

        Map<String, DiscoveryConfig> discoveryConfigs = Maps.newHashMap(reloadFailedDiscoveryConfigs);
        List<DiscoveryConfig> configs = Lists.newArrayList(discoveryConfigs.values());
        for (ServiceContext serviceContext : serviceRepository.getServices()) {
            if (discoveryConfigs.containsKey(serviceContext.getDiscoveryConfig().getServiceId()) || serviceContext.isAvailable()) {
                continue;
            }
            configs.add(serviceContext.getDiscoveryConfig());
        }

        return configs;
    }

    protected void reload(DiscoveryConfig... configs) {
        reload(Lists.newArrayList(configs));
    }

    protected void reload(List<DiscoveryConfig> configs) {
        try {
            if (CollectionUtils.isEmpty(configs))
                return;

            logger.info("start reload services.");
            List<Service> services = discoveryHttpClient.getServices(configs);
            for (Service service : services) {
                if (service == null) {
                    continue;
                }
                final String serviceId = service.getServiceId();
                if (StringValues.isNullOrWhitespace(serviceId)) {
                    continue;
                }
                onServiceChange(service);
                reloadFailedDiscoveryConfigs.remove(serviceId.toLowerCase());
            }

            lastUpdateTime = System.currentTimeMillis();
            logger.info("end reload services");
        } catch (Throwable t) {
            for (DiscoveryConfig config : configs) {
                if (config == null) {
                    continue;
                }
                final String serviceId = config.getServiceId();
                if (StringValues.isNullOrWhitespace(serviceId)) {
                    continue;
                }
                reloadFailedDiscoveryConfigs.put(serviceId.toLowerCase(), config);
            }
            throw t;
        }
    }

    protected void subscribe(final WebSocketSession session) {
        try {
            for (final DiscoveryConfig discoveryConfig : serviceRepository.getDiscoveryConfigs()) {
                subscribe(session, discoveryConfig);
            }
        } catch (final Throwable e) {
            logger.warn("subscribe services failed", e);
        }
    }

    protected void subscribe(final WebSocketSession session, final DiscoveryConfig discoveryConfig) {
        try {
            if (discoveryConfig == null) {
                return;
            }
            if (session == null) {
                return;
            }
            final TextMessage message = new TextMessage(JacksonJsonSerializer.INSTANCE.serialize(discoveryConfig));
            session.sendMessage(message);
        } catch (final Throwable e) {
            logger.warn("subscribe service failed", e);
        }
    }

    protected boolean expired() {
        return System.currentTimeMillis() - lastUpdateTime >= ttl.typedValue();
    }
}
