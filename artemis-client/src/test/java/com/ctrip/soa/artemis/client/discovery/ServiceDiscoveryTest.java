package com.ctrip.soa.artemis.client.discovery;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.InstanceChange;
import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.client.ServiceChangeEvent;
import com.ctrip.soa.artemis.client.ServiceChangeListener;
import com.ctrip.soa.artemis.client.registry.InstanceRepository;
import com.ctrip.soa.artemis.client.test.utils.ArtemisClientConstants;
import com.ctrip.soa.artemis.client.test.utils.Instances;
import com.ctrip.soa.artemis.client.test.utils.Services;
import com.ctrip.soa.artemis.discovery.DiscoveryConfig;
import com.ctrip.soa.caravan.common.concurrent.Threads;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServiceDiscoveryTest {
    @Test
    public void testReload() throws Exception {
        final ServiceRepository serviceRepository = new ServiceRepository(ArtemisClientConstants.DiscoveryClientConfig);
        final List<Service> services = Lists.newArrayList();

        Assert.assertEquals(0, services.size());
        Set<String> serviceKeys = Sets.newHashSet(ArtemisClientConstants.RegistryService.Net.serviceKey,
                ArtemisClientConstants.RegistryService.Java.serviceKey);
        Map<String, ServiceChangeListener> serviceChangeListeners = Maps.newHashMap();
        for (String serviceKey : serviceKeys) {
            DefaultServiceChangeListener listener = new DefaultServiceChangeListener();
            DiscoveryConfig discoveryConfig = new DiscoveryConfig(serviceKey);
            serviceChangeListeners.put(serviceKey, listener);
            serviceRepository.registerServiceChangeListener(discoveryConfig, listener);
            serviceRepository.serviceDiscovery.reload(discoveryConfig);

            Assert.assertTrue(listener.getServiceChangeEvents().size() >= 1);
            for (ServiceChangeEvent event : listener.getServiceChangeEvents()) {
                Assert.assertEquals(InstanceChange.ChangeType.RELOAD, event.changeType());
                Assert.assertEquals(serviceKey, event.changedService().getServiceId());
            }
        }
    }

    @Test
    public void testSubscribe() throws Exception {
        final ServiceRepository serviceRepository = new ServiceRepository(ArtemisClientConstants.DiscoveryClientConfig);
        final String serviceId = Services.newServiceId();
        final DiscoveryConfig discoveryConfig = new DiscoveryConfig(serviceId);
        final Set<Instance> instances = Sets.newHashSet(Instances.newInstance(serviceId), Instances.newInstance(serviceId));
        final CountDownLatch addCount = new CountDownLatch(instances.size() * 2);
        final CountDownLatch deleteCount = new CountDownLatch(instances.size());
        final List<ServiceChangeEvent> serviceChangeEvents = Lists.newArrayList();

        serviceRepository.registerServiceChangeListener(discoveryConfig, new ServiceChangeListener() {
            @Override
            public void onChange(ServiceChangeEvent event) {
                serviceChangeEvents.add(event);
                if (InstanceChange.ChangeType.DELETE.equals(event.changeType())) {
                    deleteCount.countDown();
                }

                if (InstanceChange.ChangeType.NEW.equals(event.changeType())) {
                    addCount.countDown();
                }
            }
        });
        Threads.sleep(2000); // wait server service discovery websocket session created.

        final InstanceRepository instanceRepository = new InstanceRepository(ArtemisClientConstants.RegistryClientConfig);
        instanceRepository.register(instances);
        Assert.assertTrue(addCount.await(2, TimeUnit.SECONDS));
        instanceRepository.unregister(instances);
        Assert.assertTrue(deleteCount.await(2, TimeUnit.SECONDS));
        Assert.assertTrue(3 * instances.size() <= serviceChangeEvents.size());
    }
}
