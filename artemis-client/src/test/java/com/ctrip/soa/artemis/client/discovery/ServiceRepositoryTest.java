package com.ctrip.soa.artemis.client.discovery;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.InstanceChange;
import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.client.ServiceChangeEvent;
import com.ctrip.soa.artemis.client.test.utils.ArtemisClientConstants;
import com.ctrip.soa.artemis.client.test.utils.Instances;
import com.ctrip.soa.artemis.client.test.utils.Services;
import com.ctrip.soa.artemis.discovery.DiscoveryConfig;
import com.ctrip.soa.caravan.common.concurrent.Threads;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServiceRepositoryTest {
    private final ServiceRepository _serviceRepository = new ServiceRepository(ArtemisClientConstants.DiscoveryClientConfig);

    @Test
    public void testGetService() {
        final DiscoveryConfig discoveryConfig = Services.newDiscoverConfig();
        final String serviceId = discoveryConfig.getServiceId();
        final Service service = _serviceRepository.getService(discoveryConfig);
        Assert.assertEquals(serviceId, service.getServiceId());
        Assert.assertTrue(_serviceRepository.containsService(serviceId));
    }

    @Test
    public void testRegisterServiceChangeListener() {
        final DiscoveryConfig discoveryConfig = Services.newDiscoverConfig();
        _serviceRepository.registerServiceChangeListener(discoveryConfig, new DefaultServiceChangeListener());
        Assert.assertTrue(_serviceRepository.containsService(discoveryConfig.getServiceId()));
    }

    @Test
    public void testUpdate_Instance() {
        for (int i = 0; i < 5; i++)
        {
            final String serviceId = Services.newServiceId();
            final DiscoveryConfig discoveryConfig = new DiscoveryConfig(serviceId);
            final List<DefaultServiceChangeListener> listeners = Lists.newArrayList();
            for (int j = 0; j < 4; j++)
            {
                listeners.add(new DefaultServiceChangeListener());
            }
            for (final DefaultServiceChangeListener listener : listeners)
            {
                _serviceRepository.registerServiceChangeListener(discoveryConfig, listener);
            }

            Assert.assertTrue(_serviceRepository.containsService(serviceId));
            final String[] changeTypes = new String[] {
                    InstanceChange.ChangeType.DELETE, InstanceChange.ChangeType.NEW, InstanceChange.ChangeType.NEW,
                    InstanceChange.ChangeType.NEW, InstanceChange.ChangeType.DELETE, InstanceChange.ChangeType.DELETE,
                    InstanceChange.ChangeType.CHANGE, InstanceChange.ChangeType.CHANGE
            };

            final Instance context = Instances.newInstance(serviceId);
            final Instance changeContext = Instances.newInstance(serviceId);
            for (final String changeType : changeTypes) {
                if (InstanceChange.ChangeType.CHANGE.equals(changeType)) {
                    _serviceRepository.update(Instances.newInstanceChanges(changeType, changeContext));
                } else {
                    _serviceRepository.update(Instances.newInstanceChanges(changeType, context));
                }
            }
            Threads.sleep(100);
            for (final DefaultServiceChangeListener listener : listeners) {
                Assert.assertEquals(6, listener.getServiceChangeEvents().size());
                final ServiceChangeEvent add1 = listener.getServiceChangeEvents().get(0);
                final ServiceChangeEvent add2 = listener.getServiceChangeEvents().get(1);
                final ServiceChangeEvent add3 = listener.getServiceChangeEvents().get(2);
                final ServiceChangeEvent delete = listener.getServiceChangeEvents().get(3);
                final ServiceChangeEvent change1 = listener.getServiceChangeEvents().get(4);
                final ServiceChangeEvent change2 = listener.getServiceChangeEvents().get(5);

                Assert.assertEquals(add1.changeType(), InstanceChange.ChangeType.NEW);
                Assert.assertEquals(add2.changeType(), InstanceChange.ChangeType.NEW);
                Assert.assertEquals(add3.changeType(), InstanceChange.ChangeType.NEW);
                Assert.assertEquals(delete.changeType(), InstanceChange.ChangeType.DELETE);
                Assert.assertEquals(change1.changeType(), InstanceChange.ChangeType.CHANGE);
                Assert.assertEquals(change2.changeType(), InstanceChange.ChangeType.CHANGE);

                Assert.assertTrue(new HashSet<>(add1.changedService().getInstances()).contains(context));
                Assert.assertTrue(new HashSet<>(add2.changedService().getInstances()).contains(context));
                Assert.assertTrue(new HashSet<>(add2.changedService().getInstances()).contains(context));
                Assert.assertTrue(delete.changedService().getInstances().size() == 0);
                Assert.assertTrue(new HashSet<>(change1.changedService().getInstances()).contains(changeContext));
                Assert.assertTrue(new HashSet<>(change2.changedService().getInstances()).contains(changeContext));
            }
        }
    }

    @Test
    public void TestUpdate_Service()
    {
        for (int i = 0; i < 5; i++)
        {
            DiscoveryConfig discoveryConfig = Services.newDiscoverConfig();
            String serviceId = discoveryConfig.getServiceId();
            DefaultServiceChangeListener listener = new DefaultServiceChangeListener();
            _serviceRepository.registerServiceChangeListener(discoveryConfig, listener);
            Assert.assertTrue(_serviceRepository.containsService(serviceId));

            Service service1 = new Service(serviceId, Lists.newArrayList(Instances.newInstance(serviceId)), null);
            Service service2 = new Service(serviceId, Lists.newArrayList(Instances.newInstance(serviceId), Instances.newInstance(serviceId)),null);

            _serviceRepository.update(service1);
            _serviceRepository.update(service2);
            _serviceRepository.update(new Service(serviceId + "1"));

            Threads.sleep(100);
            Assert.assertEquals(2, listener.getServiceChangeEvents().size());
            for (ServiceChangeEvent event : listener.getServiceChangeEvents()) {
                Assert.assertEquals(InstanceChange.ChangeType.RELOAD, event.changeType());
            }
        }
    }
}
