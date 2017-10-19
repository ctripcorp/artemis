package com.ctrip.soa.artemis.client.test.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

import com.ctrip.soa.artemis.InstanceChange;
import org.junit.Assert;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.registry.HasFailedInstances;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
 
/**
 * Created by fang_j on 10/07/2016.
 */
public class Instances {
    public static Instance newInstance() {
        final SecureRandom random = new SecureRandom();
        final Instance instance = new Instance();
        instance.setServiceId(new BigInteger(130, random).toString(32));
        instance.setIp(new BigInteger(130, random).toString(32));
        instance.setHealthCheckUrl(new BigInteger(130, random).toString(32));
        instance.setInstanceId(new BigInteger(130, random).toString(32));
        instance.setMachineName(new BigInteger(130, random).toString(32));
        instance.setPort(8090);
        instance.setProtocol("http");
        instance.setRegionId("SHA");
        instance.setStatus(new BigInteger(130, random).toString(32));
        instance.setUrl(new BigInteger(130, random).toString(32));
        instance.setZoneId("SHAJQ");
        return instance;
    }

    public static Instance newInstance(final String serviceId) {
        final Instance instance = newInstance();
        instance.setServiceId(serviceId);
        return instance;
    }

    public static InstanceChange newInstanceChanges(final String changeType, final Instance instance) {
        InstanceChange instanceChange = new InstanceChange();
        instanceChange.setChangeType(changeType);
        instanceChange.setInstance(instance);
        return instanceChange;
    }


    public static Set<Instance> newInstances(final int length, final Set<String> serviceIds) {
        final Set<Instance> instances = new HashSet<Instance>();
        for (int i = 0; i < length; i++) {
            for (final String serviceId : serviceIds) {
                instances.add(newInstance(serviceId));
            }
        }
        return instances;
    }

    public static Set<Instance> newInstances(final int length) {
        final Set<Instance> instances = new HashSet<Instance>();
        for (int i = 0; i < length; i++) {
            instances.add(newInstance());
        }
        return instances;
    }

    public static void assertSucessResponse(final HasResponseStatus response) {
        Assert.assertTrue(ResponseStatusUtil.isSuccess(response.getResponseStatus()));
    }

    public static void assertNoFailedInstances(final HasFailedInstances response) {
        if (response.getFailedInstances() != null) {
            Assert.assertEquals(0, response.getFailedInstances().size());
        }
    }
}
