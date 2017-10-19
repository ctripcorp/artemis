package com.ctrip.soa.artemis.cluster;

import com.ctrip.soa.artemis.cluster.GetServiceNodesRequest;
import com.ctrip.soa.artemis.cluster.GetServiceNodesResponse;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface ClusterService {

    GetServiceNodesResponse getUpRegistryNodes(GetServiceNodesRequest request);

    GetServiceNodesResponse getUpDiscoveryNodes(GetServiceNodesRequest request);

}
