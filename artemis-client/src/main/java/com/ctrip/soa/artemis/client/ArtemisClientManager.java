package com.ctrip.soa.artemis.client;

import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.soa.artemis.client.discovery.DiscoveryClientImpl;
import com.ctrip.soa.artemis.client.registry.RegistryClientImpl;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctrip.soa.caravan.common.value.ConcurrentHashMapValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.google.common.base.Preconditions;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ArtemisClientManager {

    private static final ConcurrentHashMap<String, ArtemisClientManager> _managers = new ConcurrentHashMap<String, ArtemisClientManager>();

    private final String _managerId;
    private final ArtemisClientManagerConfig _managerConfig;
    private final String _clientId;
    private DiscoveryClient _discoveryClient;
    private RegistryClient _registryClient;

    private ArtemisClientManager(final String managerId, final ArtemisClientManagerConfig managerConfig) {
        Preconditions.checkArgument(!StringValues.isNullOrWhitespace(managerId), "managerId");
        Preconditions.checkArgument(managerConfig != null, "manager config");

        _managerId = managerId;
        _managerConfig = managerConfig;
        _clientId = "artemis.client." + managerId;

    }

    public DiscoveryClient getDiscoveryClient() {
        if (_discoveryClient == null) {
            synchronized (this) {
                if (_discoveryClient == null) {
                    _discoveryClient = new DiscoveryClientImpl(_clientId, _managerConfig);
                }
            }
        }

        return _discoveryClient;
    }

    public RegistryClient getRegistryClient() {
        if (_registryClient == null) {
            synchronized (this) {
                if (_registryClient == null) {
                    _registryClient = new RegistryClientImpl(_clientId, _managerConfig);
                }
            }
        }

        return _registryClient;
    }

    public String getManagerId() {
        return _managerId;
    }

    public ArtemisClientManagerConfig getManagerConfig() {
        return _managerConfig;
    }

    public static ArtemisClientManager getManager(final String managerId, final ArtemisClientManagerConfig managerConfig) {
        Preconditions.checkArgument(!StringValues.isNullOrWhitespace(managerId), "managerId");
        Preconditions.checkArgument(managerConfig != null, "manager config");
        return ConcurrentHashMapValues.getOrAddWithLock(_managers, managerId, new Func<ArtemisClientManager>() {
            @Override
            public ArtemisClientManager execute() {
                return new ArtemisClientManager(managerId, managerConfig);
            }
        });
    }
}