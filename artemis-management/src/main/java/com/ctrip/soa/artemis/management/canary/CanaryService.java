package com.ctrip.soa.artemis.management.canary;

/**
 * Created by fang_j on 10/07/2016.
 */
public interface CanaryService {
    UpdateCanaryIPsResponse updateCanaryIPs(UpdateCanaryIPsRequest request);
}
