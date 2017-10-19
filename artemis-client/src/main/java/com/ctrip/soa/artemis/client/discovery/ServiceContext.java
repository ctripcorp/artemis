package com.ctrip.soa.artemis.client.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.client.ServiceChangeEvent;
import com.ctrip.soa.artemis.client.ServiceChangeListener;
import com.ctrip.soa.artemis.discovery.DiscoveryConfig;
import com.ctrip.soa.artemis.util.RouteRules;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.common.value.checker.StringArgumentChecker;
import com.ctrip.soa.caravan.common.value.checker.ValueCheckers;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServiceContext {
    private final DiscoveryConfig discoveryConfig;
    private final String serviceId;
    private final Set<ServiceChangeListener> listeners;
    private volatile Service service;

    public ServiceContext(DiscoveryConfig discoveryConfig) {
        NullArgumentChecker.DEFAULT.check(discoveryConfig, "discoveryConfig");
        StringArgumentChecker.DEFAULT.check(discoveryConfig.getServiceId(), "discoveryConfig.serviceId");
        this.discoveryConfig = discoveryConfig;
        serviceId = StringValues.toLowerCase(discoveryConfig.getServiceId());
        listeners = Sets.newConcurrentHashSet();
        service = new Service(this.discoveryConfig.getServiceId());
    }

    public DiscoveryConfig getDiscoveryConfig() {
        return discoveryConfig;
    }

    public synchronized Service newService() {
        Service newService = service.clone();
        newService.setRouteRules(RouteRules.newRouteRules(service));
        return newService;
    }

    public synchronized void setService(Service service) {
        ValueCheckers.notNull(service, "service");
        final String newServiceId = service.getServiceId();
        ValueCheckers.notNullOrWhiteSpace(newServiceId, "serviceId");
        Preconditions.checkArgument(serviceId.equals(newServiceId.toLowerCase()),
                "service's serviceId is not this same as discoveryConfig. expected: " + serviceId + ", actual: " + newServiceId);
        this.service = service;
    }

    public synchronized boolean deleteInstance(Instance instance) {
        if (instance == null)
            return false;

        List<Instance> instances = service.getInstances();
        if (instances == null)
            return false;

        return instances.remove(instance);
    }

    public synchronized boolean updateInstance(Instance instance) {
        return addInstance(instance);
    }

    public synchronized boolean addInstance(Instance instance) {
        if (instance == null) {
            return false;
        }
        String instanceServiceId = instance.getServiceId();
        if (instanceServiceId == null || !serviceId.equals(instanceServiceId.toLowerCase())) {
            return false;
        }

        List<Instance> instances = service.getInstances();
        if (instances == null) {
            instances = new ArrayList<>();
        }

        deleteInstance(instance);
        instances.add(instance);
        service.setInstances(instances);
        return true;
    }

    public synchronized boolean isAvailable() {
        Service service = this.service;
        return service != null && !CollectionValues.isNullOrEmpty(service.getInstances());
    }

    public synchronized Set<ServiceChangeListener> getListeners() {
        return listeners;
    }

    public synchronized void addListener(final ServiceChangeListener listener) {
        if (listener == null) {
            return;
        }
        listeners.add(listener);
    }

    public ServiceChangeEvent newServiceChangeEvent(final String changeType) {
        Preconditions.checkArgument(!StringValues.isNullOrWhitespace(changeType), "changeType");
        final Service service = newService();
        return new ServiceChangeEvent() {
            @Override
            public Service changedService() {
                return service;
            }

            @Override
            public String changeType() {
                return changeType;
            }
        };
    }

    @Override
    public String toString() {
        return serviceId;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }

        if (other.getClass() != this.getClass()) {
            return false;
        }

        return Objects.equal(toString(), other.toString());
    }

}
