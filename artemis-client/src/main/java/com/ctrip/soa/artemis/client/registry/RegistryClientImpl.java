package com.ctrip.soa.artemis.client.registry;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.client.ArtemisClientManagerConfig;
import com.ctrip.soa.artemis.client.RegistryClient;
import com.ctrip.soa.artemis.client.common.AddressManager;
import com.ctrip.soa.artemis.client.common.ArtemisClientConfig;
import com.ctrip.soa.artemis.client.common.Conditions;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * Created by fang_j on 10/07/2016.
 */
public class RegistryClientImpl implements RegistryClient {
    protected final InstanceRepository _instanceRepository;
    protected final InstanceRegistry _instanceRegistry;

    public RegistryClientImpl(final String clientId, final ArtemisClientManagerConfig managerConfig) {
        Preconditions.checkArgument(!StringValues.isNullOrWhitespace(clientId), "clientId");
        Preconditions.checkArgument(managerConfig != null, "manager config");

        final ArtemisClientConfig config = new ArtemisClientConfig(clientId, managerConfig,
                AddressManager.getRegistryAddressManager(clientId, managerConfig));
        _instanceRepository = new InstanceRepository(config);
        _instanceRegistry = new InstanceRegistry(_instanceRepository, config);
    }

    @Override
    public void register(final Instance... instances) {
        Preconditions.checkArgument(Conditions.verifyInstances(instances), "instances");
        _instanceRepository.register(Sets.newHashSet(instances));
    }

    @Override
    public void unregister(final Instance... instances) {
        Preconditions.checkArgument(Conditions.verifyInstances(instances), "instances");
        Conditions.verifyInstances(instances);
        _instanceRepository.unregister(Sets.newHashSet(instances));
    }
}
