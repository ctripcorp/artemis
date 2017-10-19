package com.ctrip.soa.artemis.cluster;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.config.DeploymentConfig;
import com.ctrip.soa.artemis.registry.RegisterRequest;
import com.ctrip.soa.artemis.registry.RegisterResponse;
import com.ctrip.soa.artemis.registry.replication.GetServicesRequest;
import com.ctrip.soa.artemis.registry.replication.GetServicesResponse;
import com.ctrip.soa.artemis.registry.replication.RegistryReplicationService;
import com.ctrip.soa.artemis.registry.replication.RegistryReplicationServiceClient;
import com.ctrip.soa.artemis.registry.replication.RegistryReplicationServiceImpl;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.ctrip.soa.artemis.util.ServiceNodeUtil;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class RegistryReplicationInitializer implements NodeInitializer {

    private static final Logger _logger = LoggerFactory.getLogger(RegistryReplicationInitializer.class);

    public static final RegistryReplicationInitializer INSTANCE = new RegistryReplicationInitializer();

    private RegistryReplicationService _registryReplicationService = RegistryReplicationServiceImpl.getInstance();

    private RegistryReplicationInitializer() {

    }

    @Override
    public TargetType target() {
        return TargetType.REGISTRY;
    }

    @Override
    public boolean initialize() {
        List<ServiceNode> peerNodes = ClusterManager.INSTANCE.localZoneOtherNodes();
        boolean success = initRegistryDataFromPeerNodes(peerNodes);
        if (success)
            return true;

        peerNodes = ClusterManager.INSTANCE.otherZoneNodes();
        return initRegistryDataFromPeerNodes(peerNodes);
    }

    private boolean initRegistryDataFromPeerNodes(List<ServiceNode> peerNodes) {
        for (ServiceNode peerNode : peerNodes) {
            ServiceNodeStatus nodeStatus = ClusterManager.INSTANCE.getNodeStatus(peerNode);
            if (!ServiceNodeUtil.isUp(nodeStatus))
                continue;

            boolean success = initRegistryDataFromPeerNode(peerNode);
            _logger.info("initRegistryData from peerNode: [" + peerNode.getUrl() + "] success? " + success);
            if (success)
                return true;
        }

        return false;
    }

    private boolean initRegistryDataFromPeerNode(ServiceNode peerNode) {
        RegistryReplicationServiceClient client = new RegistryReplicationServiceClient(peerNode.getUrl());
        GetServicesResponse response;
        try {
            GetServicesRequest request = new GetServicesRequest();
            request.setRegionId(DeploymentConfig.regionId());
            request.setZoneId(DeploymentConfig.zoneId());
            response = client.getServices(request);
            if (!ResponseStatusUtil.isSuccess(response.getResponseStatus())) {
                _logger.info("Response: " + JacksonJsonSerializer.INSTANCE.serialize(response));
                return false;
            }
        } catch (Throwable ex) {
            _logger.error("NodeManager initRegistryDataFromPeerNode failed.", ex);
            return false;
        }

        List<Service> services = response.getServices();
        if (services == null) {
            _logger.info(String.format("The services list of peerNode: [%s] is null, return false!", peerNode.getUrl()));
            return false;
        }

        int replicationInstanceCount = 0;
        for (Service service : services) {
            if (service == null)
                continue;

            List<Instance> instances = service.getInstances();
            if (CollectionValues.isNullOrEmpty(instances))
                continue;

            RegisterRequest request = new RegisterRequest(instances);
            RegisterResponse response2 = _registryReplicationService.register(request);
            if (!ResponseStatusUtil.isSuccess(response2.getResponseStatus())) {
                _logger.info("Response2: " + JacksonJsonSerializer.INSTANCE.serialize(response2));
                return false;
            }

            replicationInstanceCount++;
        }

        _logger.info("replicationInstanceCount: " + replicationInstanceCount);
        return replicationInstanceCount > 0;
    }

}
