package com.ctrip.soa.artemis.client.discovery;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.client.test.utils.ArtemisClientConstants;
import com.ctrip.soa.artemis.client.test.utils.Services;
import com.ctrip.soa.artemis.discovery.DiscoveryConfig;
import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ArtemisDiscoveryHttpClientTest {
    private final ArtemisDiscoveryHttpClient _client = new ArtemisDiscoveryHttpClient(ArtemisClientConstants.DiscoveryClientConfig);

    @Test
    public void testGetService_ShouldReturnEmptyInstances() {
        Assert.assertTrue(CollectionUtils.isEmpty(_client.getService(Services.newDiscoverConfig()).getInstances()));
    }

    @Test
    public void testGetService_ShouldReturnIntances() throws Exception {
        final Service service = _client.getService(new DiscoveryConfig(ArtemisClientConstants.RegistryService.Net.serviceKey));
        Assert.assertNotNull(service);
        Assert.assertTrue(service.getInstances().size() > 0);
    }

    @Test
    public void testGetServices() {
        final String serviceId = Services.newServiceId();
        final List<String> serviceIds = Lists.newArrayList(serviceId, ArtemisClientConstants.RegistryService.Net.serviceKey);
        final List<DiscoveryConfig> discoveryConfigs = Lists.newArrayList(new DiscoveryConfig(serviceId),
                new DiscoveryConfig(ArtemisClientConstants.RegistryService.Net.serviceKey));
        final List<Service> services = _client.getServices(discoveryConfigs);
        Assert.assertEquals(discoveryConfigs.size(), services.size());
        for (final Service service : services) {
            Assert.assertTrue(serviceIds.contains(service.getServiceId()));
            if (ArtemisClientConstants.RegistryService.Net.serviceKey
                    .equals(service.getServiceId())) {
                Assert.assertTrue(service.getInstances().size() > 0);
            } else {
                Assert.assertTrue((service.getInstances() == null) || (service.getInstances().size() == 0));
            }
        }
    }
}