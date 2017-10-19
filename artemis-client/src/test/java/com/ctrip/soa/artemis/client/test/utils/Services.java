package com.ctrip.soa.artemis.client.test.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

import com.ctrip.soa.artemis.discovery.DiscoveryConfig;

/**
 * Created by fang_j on 10/07/2016.
 */
public class Services {
    private static final SecureRandom random = new SecureRandom();
    public static String newServiceId() {
        return new BigInteger(130, random).toString(32);
    }

    public static DiscoveryConfig newDiscoverConfig() {
        return new DiscoveryConfig(new BigInteger(130, random).toString(32));
    }

    public static Set<String> newServiceIds(final int length) {
        final Set<String> serviceIds = new HashSet<String>();
        for (int i = 0; i < length; i++) {
            serviceIds.add(newServiceId());
        }
        return serviceIds;
    }
}
