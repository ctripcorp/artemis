package com.ctrip.soa.artemis.web.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ctrip.soa.artemis.cluster.ClusterService;
import com.ctrip.soa.artemis.cluster.ClusterServiceImpl;
import com.ctrip.soa.artemis.cluster.GetServiceNodesRequest;
import com.ctrip.soa.artemis.cluster.GetServiceNodesResponse;
import com.ctrip.soa.artemis.config.RestPaths;
import com.ctrip.soa.artemis.metric.MetricLoggerHelper;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
@RestController
@RequestMapping(path = RestPaths.CLUSTER_PATH)
public class ClusterController {

    private ClusterService _clusterService = ClusterServiceImpl.getInstance();

    @RequestMapping(path = RestPaths.CLUSTER_UP_REGISTRY_NODES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetServiceNodesResponse getUpRegistryNodes(@RequestBody GetServiceNodesRequest request) {
        GetServiceNodesResponse response = _clusterService.getUpRegistryNodes(request);
        MetricLoggerHelper.logResponseEvent("cluster", "get-up-registry-nodes", response);
        return response;
    }

    @RequestMapping(path = RestPaths.CLUSTER_UP_REGISTRY_NODES_RELATIVE_PATH, method = RequestMethod.GET, produces = "application/json")
    public GetServiceNodesResponse getUpRegistryNodes(@RequestParam(required = false) String regionId, @RequestParam(required = false) String zoneId) {
        GetServiceNodesResponse response = _clusterService.getUpRegistryNodes(new GetServiceNodesRequest(regionId, zoneId));
        MetricLoggerHelper.logResponseEvent("cluster", "get-up-registry-nodes", response);
        return response;
    }

    @RequestMapping(path = RestPaths.CLUSTER_UP_DISCOVERY_NODES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetServiceNodesResponse getUpDiscoveryNodes(@RequestBody GetServiceNodesRequest request) {
        GetServiceNodesResponse response = _clusterService.getUpDiscoveryNodes(request);
        MetricLoggerHelper.logResponseEvent("cluster", "get-up-discovery-nodes", response);
        return response;
    }

    @RequestMapping(path = RestPaths.CLUSTER_UP_DISCOVERY_NODES_RELATIVE_PATH, method = RequestMethod.GET, produces = "application/json")
    public GetServiceNodesResponse getUpDiscoveryNodes(@RequestParam(required = false) String regionId, @RequestParam(required = false) String zoneId) {
        GetServiceNodesResponse response = _clusterService.getUpDiscoveryNodes(new GetServiceNodesRequest(regionId, zoneId));
        MetricLoggerHelper.logResponseEvent("cluster", "get-up-discovery-nodes", response);
        return response;
    }

}
