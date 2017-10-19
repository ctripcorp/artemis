package com.ctrip.soa.artemis.client.test.utils;

import java.util.ArrayList;
import java.util.List;
import com.ctrip.soa.artemis.client.ArtemisClientManagerConfig;
import com.ctrip.soa.artemis.client.common.AddressManager;
import com.ctrip.soa.artemis.client.common.ArtemisClientConfig;
import com.ctrip.soa.artemis.client.discovery.ArtemisDiscoveryHttpClient;
import com.ctrip.soa.artemis.config.DeploymentConfig;
import com.ctrip.soa.caravan.configuration.ConfigurationSource;
import com.ctrip.soa.caravan.configuration.facade.ConfigurationManagers;
import com.ctrip.soa.caravan.configuration.facade.ConfigurationSources;
import com.ctrip.soa.caravan.configuration.facade.TypedDynamicCachedCorrectedProperties;
import com.ctrip.soa.caravan.configuration.source.memory.MemoryConfigurationSource;
import com.ctrip.soa.caravan.configuration.typed.dynamic.cached.corrected.TypedDynamicCachedCorrectedConfigurationManager;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ArtemisClientConstants {

    public static String Name = "client";
    public static MemoryConfigurationSource MemSource = ConfigurationSources.newMemorySource(1, "artemis.client.test.config.source");
    public static TypedDynamicCachedCorrectedProperties Properties;
    public static ArtemisClientManagerConfig ManagerConfig;
    public static ArtemisClientConfig DiscoveryClientConfig;
    public static ArtemisClientConfig RegistryClientConfig;
    public static ArtemisDiscoveryHttpClient DiscoveryHttpClient;
    public static String ClientId;
    
    public static final String DOMAIN_ARTEMIS_SERVICE_URL = "{need to replace}";

    static {
        DeploymentConfig.init("SHA", "jqsha", "0", "http", 8080, "artemis");
        final List<ConfigurationSource> sources = new ArrayList<>();
        sources.add(MemSource);
        final TypedDynamicCachedCorrectedConfigurationManager manager = ConfigurationManagers
                .newTypedDynamicCachedCorrectedManager(sources.toArray(new ConfigurationSource[0]));
        Properties = new TypedDynamicCachedCorrectedProperties(manager);
        ClientId = "artemis.client" + "." + Name.toLowerCase();
        ArtemisClientConstants.setDomain(DOMAIN_ARTEMIS_SERVICE_URL);
        MemSource.configuration().setPropertyValue(ClientId + ".websocket-session.reconnect-times.rate-limiter.enabled", "false");
        ManagerConfig = new ArtemisClientManagerConfig(Properties);
        DiscoveryClientConfig = new ArtemisClientConfig(ClientId, ManagerConfig, AddressManager.getDiscoveryAddressManager(ClientId, ManagerConfig));
        RegistryClientConfig = new ArtemisClientConfig(ClientId, ManagerConfig, AddressManager.getRegistryAddressManager(ClientId, ManagerConfig));
        DiscoveryHttpClient = new ArtemisDiscoveryHttpClient(DiscoveryClientConfig);
    }

    public static void setDomain(final String domain) {
        MemSource.configuration().setPropertyValue(ClientId + ".service.domain.url", domain);
    }

    public static void setSocketTimeout() {
        MemSource.configuration().setPropertyValue(ClientId + ".registry.http-client.client.socket-timout", "10000");
        MemSource.configuration().setPropertyValue(ClientId + ".discovery.http-client.client.socket-timout", "10000");
    }

    public interface RegistryService {
        public interface Net {
            String serviceKey = "framework.soa.v1.registryservice";
            String serviceCode = "10002";
        }

        public interface Java {
            String serviceKey = "framework.soa4j.registryservice.v1.registryservice";
            String serviceCode = "10586";
        }
    }
}
