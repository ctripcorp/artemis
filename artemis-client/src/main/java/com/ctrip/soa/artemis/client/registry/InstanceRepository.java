package com.ctrip.soa.artemis.client.registry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.TextMessage;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.client.RegistryFilter;
import com.ctrip.soa.artemis.client.common.ArtemisClientConfig;
import com.ctrip.soa.artemis.client.common.RegisterType;
import com.ctrip.soa.artemis.registry.HeartbeatRequest;
import com.ctrip.soa.caravan.common.metric.AuditMetric;
import com.ctrip.soa.caravan.common.metric.AuditMetricManager;
import com.ctrip.soa.caravan.common.metric.MetricConfig;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Created by fang_j on 10/07/2016.
 */
public class InstanceRepository {
    private static final Logger _logger = LoggerFactory.getLogger(InstanceRepository.class);
    private final AtomicReference<Set<Instance>> _instances = new AtomicReference<Set<Instance>>(new HashSet<Instance>());
    private final ArtemisRegistryHttpClient _client;
    private final List<RegistryFilter> _filters;
    private final AuditMetricManager _valueMetricManager;
    private final String _metricNameAudit;
    private final String _metricNameDistribution;

    public InstanceRepository(final ArtemisClientConfig config) {
        Preconditions.checkArgument(config != null, "config");
        _client = new ArtemisRegistryHttpClient(config);
        _filters = config.registryClientConfig().getRegistryFilters();
        _valueMetricManager = config.valueMetricManager();
        _metricNameAudit = config.key("filter-instances.latency");
        _metricNameDistribution = config.key("filter-instances.latency.distribution");
    }
    
    private Set<Instance> filterInstances(final Set<Instance> instances) {
        if (CollectionUtils.isEmpty(instances) || CollectionUtils.isEmpty(_filters)) {
            return instances;
        }
        
        List<Instance> filterInstances = Lists.newArrayList(instances);
        for (RegistryFilter filter : _filters) {
            if (filter == null) {
                continue;
            }
            long start = System.currentTimeMillis();
            try {
                filter.filter(filterInstances);
            } catch (Throwable e) {
                _logger.warn("filter instances failed", e);
            } finally {
                metric(filter.getRegistryFilterId(), System.currentTimeMillis() - start);
            }
        }
        if (filterInstances == null) {
            return Sets.newHashSet();
        } else {
            return Sets.newHashSet(filterInstances);
        }
    }

    public Set<Instance> getAvailableInstances() {
        return filterInstances(_instances.get());
    }

    public TextMessage getHeartbeatMessage() {
        try {
            List<Instance> instances = new ArrayList<Instance>(this.getAvailableInstances());
            if (instances.size() > 0) {
                return new TextMessage(JacksonJsonSerializer.INSTANCE.serialize(new HeartbeatRequest(instances)));
            } else {
                return null;
            }
        } catch (Throwable e) {
            _logger.warn("get heartbeat message failed", e);
            return null;
        }
    }

    public void registerToRemote(final Set<Instance> instances) {
        if (CollectionUtils.isEmpty(instances)) {
            return;
        }

        Set<Instance> filterInstances = filterInstances(instances);
        if (CollectionUtils.isEmpty(filterInstances)) {
            _logger.info("get empty instances after RegistryFilter processed:" + Joiner.on(',').join(instances));
            return;
        }

        _client.register(filterInstances);
    }

    public void register(final Set<Instance> instances) {
        if (CollectionUtils.isEmpty(instances)) {
            return;
        }
        _client.unregister(instances);
        updateInstances(instances, RegisterType.register);
    }

    public void unregister(final Set<Instance> instances) {
        if (CollectionUtils.isEmpty(instances)) {
            return;
        }
        _client.unregister(instances);
        updateInstances(instances, RegisterType.unregister);

    }

    protected synchronized void updateInstances(final Set<Instance> instances, final RegisterType type) {
        try {
            if (CollectionUtils.isEmpty(instances)) {
                return;
            }
            final Set<Instance> newInstances = new HashSet<Instance>(_instances.get());

            for (final Instance instance : instances) {
                if (RegisterType.register.equals(type)) {
                    newInstances.add(instance);
                } else if (RegisterType.unregister.equals(type)) {
                    newInstances.remove(instance);
                }
            }

            _instances.set(newInstances);
        } catch (final Throwable e) {
            _logger.warn("update instances failed", e);
        }
    }
    
    private void metric(final String filterId, final long value) {
        if (StringValues.isNullOrWhitespace(filterId)) {
            return;
        }
        final String metricId = "filter-instances." + filterId;
        AuditMetric metric = _valueMetricManager.getMetric(metricId, new MetricConfig(ImmutableMap.of("metric_name_distribution", _metricNameDistribution,
                "metric_name_audit", _metricNameAudit, "filter", filterId)));
        metric.addValue(value);
    }
}
