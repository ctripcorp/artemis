package com.ctrip.soa.artemis.client.discovery;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ctrip.soa.artemis.InstanceChange;
import com.ctrip.soa.caravan.common.metric.EventMetricManager;
import com.ctrip.soa.caravan.common.metric.MetricConfig;
import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.client.ServiceChangeEvent;
import com.ctrip.soa.artemis.client.ServiceChangeListener;
import com.ctrip.soa.artemis.client.common.ArtemisClientConfig;
import com.ctrip.soa.artemis.discovery.DiscoveryConfig;
import com.ctrip.soa.artemis.util.InstanceChanges;
import com.ctrip.soa.caravan.common.metric.EventMetric;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServiceRepository {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRepository.class);
    private final ConcurrentHashMap<String, ServiceContext> services = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, DiscoveryConfig> discoveryConfigs = new ConcurrentHashMap<>();
    private final ExecutorService serviceChangeCallback = Executors.newSingleThreadExecutor();
    private final String serviceDiscoveryMetricName;
    private final EventMetricManager eventMetricManager;
    protected final ServiceDiscovery serviceDiscovery;

    public ServiceRepository(final ArtemisClientConfig config) {
        this(config, null);
    }

    protected ServiceRepository(final ArtemisClientConfig config, ServiceDiscovery serviceDiscovery) {
        Preconditions.checkArgument(config != null, "ArtemisClientConfig should not be null");
        serviceDiscoveryMetricName = config.key("service-discovery.instance-change.event.distribution");
        eventMetricManager = config.eventMetricManager();
        if (serviceDiscovery == null) {
            this.serviceDiscovery = new ServiceDiscovery(this, config);
        } else {
            this.serviceDiscovery = serviceDiscovery;
        }
    }

    public boolean containsService(final String serviceId) {
        if (StringValues.isNullOrWhitespace(serviceId)) {
            return false;
        }
        return services.containsKey(serviceId.toLowerCase());
    }

    public List<DiscoveryConfig> getDiscoveryConfigs() {
        return Lists.newArrayList(discoveryConfigs.values());
    }

    public DiscoveryConfig getDiscoveryConfig(final String serviceId) {
        if (StringValues.isNullOrWhitespace(serviceId)) {
            return null;
        }
        return discoveryConfigs.get(serviceId.toLowerCase());
    }

    public List<ServiceContext> getServices() {
        return Lists.newArrayList(services.values());
    }

    public Service getService(final DiscoveryConfig discoveryConfig) {
        if (discoveryConfig == null) {
            return new Service();
        }

        final String serviceId = StringValues.toLowerCase(discoveryConfig.getServiceId());
        if (StringValues.isNullOrWhitespace(serviceId))
            return new Service();

        if (!containsService(serviceId))
            registerService(discoveryConfig);

        return services.get(serviceId).newService();
    }

    public void registerServiceChangeListener(final DiscoveryConfig discoveryConfig, final ServiceChangeListener listener) {
        if ((discoveryConfig == null) || (listener == null)) {
            return;
        }

        final String serviceId = StringValues.toLowerCase(discoveryConfig.getServiceId());
        if (StringValues.isNullOrWhitespace(serviceId)) {
            return;
        }
        if (!containsService(serviceId)) {
            registerService(discoveryConfig);
        }
        services.get(serviceId).addListener(listener);
    }

    private synchronized void registerService(final DiscoveryConfig discoveryConfig) {
        if (discoveryConfig == null) {
            return;
        }
        final String serviceId = StringValues.toLowerCase(discoveryConfig.getServiceId());
        if (containsService(serviceId)) {
            return;
        }
        ServiceContext serviceContext = new ServiceContext(discoveryConfig);
        try {
            Service service = serviceDiscovery.getService(discoveryConfig);
            serviceContext.setService(service);
        } catch (Throwable t) {
            logger.error("init service failed", t);
        }
        services.put(serviceId, serviceContext);
        discoveryConfigs.put(serviceId, discoveryConfig);
        try {
            serviceDiscovery.registerDiscoveryConfig(discoveryConfig);
        } catch (final Throwable e) {
            logger.warn(String.format("register the service %s to the %s failed", serviceId, serviceDiscovery), e);
        }
    }

    private void notifyServiceChange(final ServiceChangeListener listener, final ServiceChangeEvent event) {
        try {
            if ((listener == null) || (event == null)) {
                return;
            }
            serviceChangeCallback.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        listener.onChange(event);
                    } catch (final Throwable e) {
                        logger.error("execute service change listener failed", e);
                    }
                }
            });
        } catch (final Throwable e) {
            logger.warn("submit service change notification failed", e);
        }
    }

    protected final synchronized void update(Service service) {
        try {
            if (service == null) {
                return;
            }
            final String serviceId = service.getServiceId();
            if (serviceId == null) {
                return;
            }
            ServiceContext currentContext = services.get(serviceId.toLowerCase());
            if (currentContext == null) {
                return;
            }

            currentContext.setService(service);
            ServiceChangeEvent event = currentContext.newServiceChangeEvent(InstanceChange.ChangeType.RELOAD);
            for (ServiceChangeListener listener : currentContext.getListeners())
                notifyServiceChange(listener, event);

            logger.info("Operation:" + event.changeType() + "\n" + "service: " + JacksonJsonSerializer.INSTANCE.serialize(service));
            metric(event.changeType(), true, service);
        } catch (final Throwable e) {
            logger.warn("update service instance failed", e);
        }
    }

    protected final synchronized void update(InstanceChange instanceChange) {
        try {
            final String changeType = instanceChange.getChangeType();
            final Instance instance = instanceChange.getInstance();
            if (StringValues.isNullOrWhitespace(changeType) || instance == null)
                return;

            final ServiceContext currentContext = services.get(instance.getServiceId());
            if (currentContext == null)
                return;

            boolean updated = false;
            if (InstanceChange.ChangeType.DELETE.equals(changeType)) {
                updated = currentContext.deleteInstance(instance);
            } else if (InstanceChange.ChangeType.NEW.equals(changeType)) {
                updated = currentContext.addInstance(instance);
            } else if (InstanceChange.ChangeType.CHANGE.equals(changeType)) {
                updated = currentContext.updateInstance(instance);
            } else {
                logger.info("unexpected changeType:" + changeType);
            }

            if (updated) {
                ServiceChangeEvent event = currentContext.newServiceChangeEvent(changeType);
                for (ServiceChangeListener listener : currentContext.getListeners()) {
                    notifyServiceChange(listener, event);
                }

                logger.info("Operation:" + changeType + "\nInstance: " + instance);
            }

            metric(changeType, updated, instance);
        } catch (Throwable e) {
            logger.warn("update service instance failed", e);
        }
    }

    private void metric(String changeType, boolean updated, Service service) {
        if (StringValues.isNullOrWhitespace(changeType) || service == null)
            return;

        Instance fakeAllInstance = getFakeAllInstance(service);
        if (fakeAllInstance == null)
            return;

        metric(changeType, updated, fakeAllInstance);
    }

    private Instance getFakeAllInstance(Service service) {
        List<Instance> instances = service.getInstances();
        if (CollectionValues.isNullOrEmpty(instances))
            return null;

        Instance sampleInstance = instances.get(0);
        if (sampleInstance == null)
            return null;

        Instance fakeInstance = sampleInstance.clone();
        fakeInstance.setInstanceId(InstanceChanges.RELOAD_FAKE_INSTANCE_ID);
        return fakeInstance;
    }

    private void metric(String changeType, boolean updated, Instance instance) {
        if (StringValues.isNullOrWhitespace(changeType) || instance == null)
            return;

        String metricId = "service-discovery." + changeType + "." + updated + "." + instance;
        Map<String, String> metadata = Maps.newHashMap();
        metadata.put("metric_name_distribution", serviceDiscoveryMetricName);
        metadata.put("regionId", instance.getRegionId());
        metadata.put("zoneId", instance.getZoneId());
        metadata.put("serviceId", instance.getServiceId());
        metadata.put("updated", updated ? "true" : "false");
        metadata.put("instanceId", instance.getInstanceId());
        EventMetric metric = eventMetricManager.getMetric(metricId, new MetricConfig(metadata));
        metric.addEvent(changeType);
    }

}
