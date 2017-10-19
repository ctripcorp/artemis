package com.ctrip.soa.artemis.client.registry;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.client.common.RegisterType;
import com.ctrip.soa.artemis.client.test.utils.ArtemisClientConstants;
import com.ctrip.soa.artemis.client.test.utils.Instances;

/**
 * Created by fang_j on 10/07/2016.
 */
public class InstanceRepositoryTest {
    private final AtomicReference<Set<Instance>> _instances = new AtomicReference<Set<Instance>>(new HashSet<Instance>());

    public final InstanceRepository repository = new InstanceRepository(ArtemisClientConstants.RegistryClientConfig) {
        @Override
        protected void updateInstances(final Set<Instance> instances, final RegisterType type) {
            super.updateInstances(instances, type);
            final Set<Instance> newInstances = new HashSet<Instance>(_instances.get());
            for (final Instance instance : instances) {
                if (type.equals(RegisterType.register)) {
                    newInstances.add(instance);
                } else if (RegisterType.unregister.equals(type)) {
                    newInstances.remove(instance);
                }
            }
            _instances.set(newInstances);
        }

        @Override
        public void registerToRemote(final Set<Instance> instances) {
            super.registerToRemote(instances);
        }
    };

    @Test
    public void testRegister() {
        final Set<Instance> instances = Instances.newInstances(2);
        repository.register(instances);
        Assert.assertTrue(_instances.get().containsAll(instances));
    }

    @Test
    public void testUnregister() {
        final Set<Instance> instances = Instances.newInstances(2);
        repository.register(instances);
        Assert.assertTrue(_instances.get().containsAll(instances));
        repository.unregister(instances);
        for (final Instance instance : instances) {
            Assert.assertFalse(_instances.get().contains(instance));
        }
    }

    @Test
    public void registerToServicesRegistry() throws InterruptedException {
        final Set<Instance> instances = Instances.newInstances(2);
        repository.register(instances);
        Assert.assertTrue(_instances.get().containsAll(instances));
    }
}
