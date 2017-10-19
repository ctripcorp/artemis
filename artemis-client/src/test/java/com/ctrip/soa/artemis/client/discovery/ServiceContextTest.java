package com.ctrip.soa.artemis.client.discovery;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.InstanceChange;
import com.ctrip.soa.artemis.client.test.utils.Instances;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.ctrip.soa.artemis.client.ServiceChangeEvent;
import com.ctrip.soa.artemis.client.ServiceChangeListener;
import com.ctrip.soa.artemis.client.test.utils.Services;
import com.ctrip.soa.artemis.discovery.DiscoveryConfig;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServiceContextTest {
    private final String _serviceId = Services.newServiceId();
    private final DiscoveryConfig _discoveryConfig = new DiscoveryConfig(_serviceId);
    public ServiceContext context;

    @Before
    public void before() {
        context = new ServiceContext(_discoveryConfig);
        Assert.assertNotNull(context.getDiscoveryConfig());
        Assert.assertNotNull(context.newService());
        Assert.assertNotNull(context.getListeners());
        Assert.assertNotNull(context.newServiceChangeEvent(InstanceChange.ChangeType.RELOAD));
    }

    @Test
    public void testAddListener() {
        final ServiceChangeListener listener1 = emptyListener();
        context.addListener(listener1);
        Assert.assertEquals(1, context.getListeners().size());
        Assert.assertTrue(context.getListeners().contains(listener1));
        final ServiceChangeListener listener2 = emptyListener();
        context.addListener(listener2);
        Assert.assertEquals(2, context.getListeners().size());
        Assert.assertTrue(context.getListeners().contains(listener2));
    }

    public ServiceChangeListener emptyListener() {
        return new ServiceChangeListener() {
            @Override
            public void onChange(final ServiceChangeEvent event) {
            }
        };
    }

    @Test
    public void testDeleteInstance() {
        ServiceContext context = new ServiceContext(_discoveryConfig);
        Instance instance1 = Instances.newInstance(_serviceId);
        Assert.assertTrue(context.addInstance(instance1));
        Assert.assertEquals(1, context.newService().getInstances().size());
        Assert.assertTrue(context.deleteInstance(instance1));
        Assert.assertEquals(0, context.newService().getInstances().size());

        Assert.assertFalse(context.deleteInstance(instance1));
    }

    @Test
    public void testAddInstance() {
        ServiceContext context = new ServiceContext(_discoveryConfig);
        Instance instance1 = Instances.newInstance(_serviceId);
        Assert.assertTrue(context.addInstance(instance1));
        Assert.assertEquals(1, context.newService().getInstances().size());

        Instance instance2 = instance1.clone();
        instance2.setServiceId(instance1.getServiceId().toUpperCase());
        Assert.assertTrue(context.addInstance(instance2));
        Assert.assertEquals(1, context.newService().getInstances().size());

        Assert.assertFalse(context.addInstance(Instances.newInstance()));
        Assert.assertEquals(1, context.newService().getInstances().size());

        Assert.assertTrue(context.addInstance(Instances.newInstance(_serviceId)));
        Assert.assertEquals(2, context.newService().getInstances().size());
    }

    @Test
    public void testIsAvailable() {
        ServiceContext context = new ServiceContext(_discoveryConfig);
        Assert.assertFalse(context.isAvailable());
        Instance instance = Instances.newInstance(_serviceId);
        Assert.assertTrue(context.addInstance(instance));
        Assert.assertEquals(1, context.newService().getInstances().size());
        Assert.assertTrue(context.isAvailable());

        Assert.assertTrue(context.deleteInstance(instance));
        Assert.assertFalse(context.isAvailable());
    }
}
