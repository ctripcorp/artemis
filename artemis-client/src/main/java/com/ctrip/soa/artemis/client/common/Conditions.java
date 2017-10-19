package com.ctrip.soa.artemis.client.common;

import java.util.Collection;

import org.springframework.util.CollectionUtils;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.caravan.common.value.StringValues;

/**
 * Created by fang_j on 10/07/2016.
 */
public class Conditions {
    public static boolean verifyInstance(final Instance instance) {
        return (instance != null)
                && !StringValues.isNullOrWhitespace(instance.getInstanceId())
                && !StringValues.isNullOrWhitespace(instance.getServiceId())
                && !StringValues.isNullOrWhitespace(instance.getUrl());
    }

    public static boolean verifyInstances(final Instance[] instances) {
        if ((instances == null) || (instances.length == 0)) {
            return false;
        }
        for (final Instance instance  : instances) {
            if (!verifyInstance(instance)) {
                return false;
            }
        }
        return true;
    }

    public static boolean verifyInstances(final Collection<Instance> instances) {
        if (CollectionUtils.isEmpty(instances)) {
            return false;
        }
        for (final Instance instance  : instances) {
            if (!verifyInstance(instance)) {
                return false;
            }
        }
        return true;
    }

    public static boolean verifyService(final Service service) {
        return (service != null)
                && !StringValues.isNullOrWhitespace(service.getServiceId());
    }

    public static boolean verifyServices(final Collection<Service> services) {
        if (CollectionUtils.isEmpty(services)) {
            return false;
        }
        for (final Service service  : services) {
            if (!verifyService(service)) {
                return false;
            }
        }
        return true;
    }
}