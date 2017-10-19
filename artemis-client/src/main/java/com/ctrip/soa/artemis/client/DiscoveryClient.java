package com.ctrip.soa.artemis.client;

import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.discovery.DiscoveryConfig;

/**
 * Created by fang_j on 10/07/2016.
 */
public interface DiscoveryClient {

    Service getService(DiscoveryConfig discoveryConfig);

    void registerServiceChangeListener(DiscoveryConfig discoveryConfig, ServiceChangeListener listener);

}