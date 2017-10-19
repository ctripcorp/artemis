package com.ctrip.soa.artemis.client.discovery;

import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.client.ArtemisClientManagerConfig;
import com.ctrip.soa.artemis.client.DiscoveryClient;
import com.ctrip.soa.artemis.client.ServiceChangeListener;
import com.ctrip.soa.artemis.client.common.AddressManager;
import com.ctrip.soa.artemis.client.common.ArtemisClientConfig;
import com.ctrip.soa.artemis.discovery.DiscoveryConfig;
import com.ctrip.soa.artemis.util.DiscoveryConfigChecker;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.google.common.base.Preconditions;

/**
 * Created by fang_j on 10/07/2016.
 */
public class DiscoveryClientImpl implements DiscoveryClient {
    private final ServiceRepository serviceRepository;

    public DiscoveryClientImpl(final String clientId, final ArtemisClientManagerConfig managerConfig) {
        Preconditions.checkArgument(!StringValues.isNullOrWhitespace(clientId), "clientId");
        Preconditions.checkArgument(managerConfig != null, "manager config");
        final ArtemisClientConfig config = new ArtemisClientConfig(clientId, managerConfig,
                AddressManager.getDiscoveryAddressManager(clientId, managerConfig));
        serviceRepository = new ServiceRepository(config);

    }

    @Override
    public Service getService(final DiscoveryConfig discoveryConfig) {
        DiscoveryConfigChecker.DEFAULT.check(discoveryConfig, "discoveryConfig");
        return serviceRepository.getService(discoveryConfig);
    }

    @Override
    public void registerServiceChangeListener(final DiscoveryConfig discoveryConfig, final ServiceChangeListener listener) {
        DiscoveryConfigChecker.DEFAULT.check(discoveryConfig, "discoveryConfig");
        NullArgumentChecker.DEFAULT.check(listener, "listener");

        serviceRepository.registerServiceChangeListener(discoveryConfig, listener);
    }
}
