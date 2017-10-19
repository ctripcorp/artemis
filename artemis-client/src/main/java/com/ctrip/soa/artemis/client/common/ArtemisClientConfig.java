package com.ctrip.soa.artemis.client.common;

import com.ctrip.soa.artemis.client.ArtemisClientManagerConfig;
import com.ctrip.soa.artemis.client.DiscoveryClientConfig;
import com.ctrip.soa.artemis.client.RegistryClientConfig;
import com.ctrip.soa.caravan.common.metric.AuditMetricManager;
import com.ctrip.soa.caravan.common.metric.EventMetricManager;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.configuration.facade.TypedDynamicCachedCorrectedProperties;
import com.ctrip.soa.caravan.util.ratelimiter.RateLimiterManager;
import com.ctrip.soa.caravan.util.ratelimiter.RateLimiterManagerConfig;
import com.google.common.base.Preconditions;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ArtemisClientConfig {
    private final String _clientId;
    private final ArtemisClientManagerConfig _managerConfig;
    private final AddressManager _addressManager;
    private final RateLimiterManager _rateLimiterManager;

    public ArtemisClientConfig(final String clientId, final ArtemisClientManagerConfig managerConfig, final AddressManager addressManager) {
        Preconditions.checkArgument(!StringValues.isNullOrWhitespace(clientId), "clientId");
        Preconditions.checkArgument(managerConfig != null, "manager config");
        Preconditions.checkArgument(addressManager != null, "addressManager");
        _clientId = clientId;
        _managerConfig = managerConfig;
        _addressManager = addressManager;
        _rateLimiterManager = new RateLimiterManager(clientId, new RateLimiterManagerConfig(properties()));
    }

    public String key(final String suffix) {
        Preconditions.checkArgument(!StringValues.isNullOrWhitespace(suffix), "suffix");
        return _clientId + "." + suffix;
    }

    public TypedDynamicCachedCorrectedProperties properties() {
        return _managerConfig.properties();
    }

    public AddressManager addressManager() {
        return _addressManager;
    }

    public EventMetricManager eventMetricManager() {
        return _managerConfig.eventMetricManager();
    }

    public AuditMetricManager valueMetricManager() {
        return _managerConfig.valueMetricManager();
    }
    
    public RegistryClientConfig registryClientConfig() {
        return _managerConfig.registryClientConfig();
    }
    
    public DiscoveryClientConfig discoveryClientConfig() {
        return _managerConfig.discoveryClientConfig();
    }

    public RateLimiterManager getRateLimiterManager() {
        return _rateLimiterManager;
    }
}
