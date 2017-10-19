package com.ctrip.soa.artemis.client.common;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.client.test.utils.Instances;
import com.ctrip.soa.artemis.client.test.utils.Services;
import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ConditionsTest {
    @Test
    public void verifyInstance() {
        {
            Assert.assertFalse(Conditions.verifyInstance(new Instance()));
        }

        {
            Assert.assertTrue(Conditions.verifyInstance(Instances.newInstance()));
        }
    }


    @Test
    public void verifyInstances() {
        {
            final Instance[] instances = new Instance[]{Instances.newInstance(), new Instance()};
            Assert.assertFalse(Conditions.verifyInstances(instances));
            Assert.assertFalse(Conditions.verifyInstances(Lists.newArrayList(instances)));
        }

        {
            final Instance[] instances = new Instance[]{Instances.newInstance()};
            Assert.assertTrue(Conditions.verifyInstances(instances));
            Assert.assertTrue(Conditions.verifyInstances(Lists.newArrayList(instances)));
        }
    }

    @Test
    public void verifyService() {
        {
            Assert.assertFalse(Conditions.verifyService(new Service()));
        }

        {
            Assert.assertTrue(Conditions.verifyService(new Service(Services.newServiceId())));
        }
    }

    @Test
    public void verifyServices() {
        {
            Assert.assertFalse(Conditions.verifyServices(Lists.newArrayList(new Service(), new Service(Services.newServiceId()))));
        }

        {
            Assert.assertTrue(Conditions.verifyServices(Lists.newArrayList(new Service(Services.newServiceId()), new Service(Services.newServiceId()))));
        }
    }
}
