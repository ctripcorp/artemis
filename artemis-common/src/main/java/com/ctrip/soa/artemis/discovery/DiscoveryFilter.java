package com.ctrip.soa.artemis.discovery;

import com.ctrip.soa.artemis.Service;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface DiscoveryFilter {

    void filter(Service service, DiscoveryConfig discoveryConfig);

}
