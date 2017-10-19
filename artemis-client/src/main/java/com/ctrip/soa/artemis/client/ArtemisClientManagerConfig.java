package com.ctrip.soa.artemis.client;

import com.ctrip.soa.caravan.common.metric.AuditMetricManager;
import com.ctrip.soa.caravan.common.metric.EventMetricManager;
import com.ctrip.soa.caravan.common.metric.NullAuditMetricManager;
import com.ctrip.soa.caravan.common.metric.NullEventMetricManager;
import com.ctrip.soa.caravan.configuration.facade.TypedDynamicCachedCorrectedProperties;
import com.google.common.base.Preconditions;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ArtemisClientManagerConfig {
    private final TypedDynamicCachedCorrectedProperties _properties;
    private final EventMetricManager _eventMetricManager;
    private final AuditMetricManager _auditMetricManager;
    private final RegistryClientConfig _registryClientConfig;
    private final DiscoveryClientConfig _discoveryClientConfig;

    public ArtemisClientManagerConfig(final TypedDynamicCachedCorrectedProperties properties) {
        this(properties, NullEventMetricManager.INSTANCE, NullAuditMetricManager.INSTANCE);
    }

    public ArtemisClientManagerConfig(final TypedDynamicCachedCorrectedProperties properties,
            final EventMetricManager eventMetricManager, final AuditMetricManager valueMetricManager) {
        this(properties, eventMetricManager, valueMetricManager, new RegistryClientConfig(), new DiscoveryClientConfig());
    }
    
    public ArtemisClientManagerConfig(final TypedDynamicCachedCorrectedProperties properties,
            final EventMetricManager eventMetricManager, final AuditMetricManager valueMetricManager, RegistryClientConfig registryClientConfig) {
        this(properties, eventMetricManager, valueMetricManager, registryClientConfig, new DiscoveryClientConfig());
    }
    
    public ArtemisClientManagerConfig(final TypedDynamicCachedCorrectedProperties properties,
            final EventMetricManager eventMetricManager, final AuditMetricManager valueMetricManager, DiscoveryClientConfig discoveryClientConfig) {
        this(properties, eventMetricManager, valueMetricManager, new RegistryClientConfig(), new DiscoveryClientConfig());
    }
    
    public ArtemisClientManagerConfig(final TypedDynamicCachedCorrectedProperties properties,
            final EventMetricManager eventMetricManager, final AuditMetricManager valueMetricManager,
            final RegistryClientConfig registryClientConfig, final DiscoveryClientConfig discoveryClientConfig) {
        Preconditions.checkArgument(properties != null, "properties");
        Preconditions.checkArgument(eventMetricManager != null, "event metric manager");
        Preconditions.checkArgument(valueMetricManager != null, "value metric manager");
        Preconditions.checkArgument(registryClientConfig != null, "registry client config");
        Preconditions.checkArgument(discoveryClientConfig != null, "discovery client config");
        _properties = properties;
        _eventMetricManager = eventMetricManager;
        _auditMetricManager = valueMetricManager;
        _registryClientConfig = registryClientConfig;
        _discoveryClientConfig = discoveryClientConfig;
    }

    public TypedDynamicCachedCorrectedProperties properties() {
        return _properties;
    }

    public EventMetricManager eventMetricManager() {
        return _eventMetricManager;
    }

    public AuditMetricManager valueMetricManager() {
        return _auditMetricManager;
    }
    
    public RegistryClientConfig registryClientConfig() {
        return _registryClientConfig;
    }
    
    public DiscoveryClientConfig discoveryClientConfig() {
        return _discoveryClientConfig;
    }
}
