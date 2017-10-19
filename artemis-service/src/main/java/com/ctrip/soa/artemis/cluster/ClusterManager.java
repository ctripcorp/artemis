package com.ctrip.soa.artemis.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.Zone;
import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.config.DeploymentConfig;
import com.ctrip.soa.artemis.status.GetClusterNodeStatusRequest;
import com.ctrip.soa.artemis.status.GetClusterNodeStatusResponse;
import com.ctrip.soa.artemis.status.StatusServiceClient;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.artemis.util.HttpClientUtil;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.ctrip.soa.artemis.util.ServiceNodeUtil;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import com.ctrip.soa.caravan.common.concurrent.Threads;
import com.ctrip.soa.caravan.common.delegate.Action;
import com.google.common.base.Objects;
import com.google.common.collect.ListMultimap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public final class ClusterManager {

    private static final String LOCAL_NODE_INIT_URL = "http://127.0.0.1";

    public static final ClusterManager INSTANCE = new ClusterManager();

    private static final Logger _logger = LoggerFactory.getLogger(ClusterManager.class);

    private TypedProperty<Integer> _nodesStatusUpdateInterval = ArtemisConfig.properties()
            .getIntProperty("artemis.service.cluster.nodes.status-update.interval", 5 * 1000, 100, 600 * 1000);
    private TypedProperty<Integer> _nodeStatusUpdateFailRetryTimes = ArtemisConfig.properties()
            .getIntProperty("artemis.service.cluster.nodes.status-update.fail-retry-times", 3, 0, 10);

    private ServiceCluster _serviceCluster;

    private Zone _localZone;

    private volatile ServiceNode _localNode;
    private volatile List<ServiceNode> _localZoneNodes = new ArrayList<>();
    private volatile List<ServiceNode> _localZoneOtherNodes = new ArrayList<>();
    private volatile List<ServiceNode> _otherNodes = new ArrayList<>();
    private volatile List<ServiceNode> _otherZoneNodes = new ArrayList<>();
    private volatile List<ServiceNode> _allNodes = new ArrayList<>();

    private HashMap<ServiceNode, ServiceNodeStatus> _nodeStatusMap = new HashMap<>();

    private ScheduledExecutorService _scheduledExecutorService;

    private AtomicBoolean _inited = new AtomicBoolean();
    private String _localHostPortIdentity;

    private ClusterManager() {
    }

    public void init(List<NodeInitializer> nodeInitializers) {
        NullArgumentChecker.DEFAULT.check(DeploymentConfig.regionId(), "DeploymentConfig.regionId()");
        NullArgumentChecker.DEFAULT.check(DeploymentConfig.zoneId(), "DeploymentConfig.zoneId()");
        _localZone = new Zone(DeploymentConfig.regionId(), DeploymentConfig.zoneId());

        if (!_inited.compareAndSet(false, true)) {
            _logger.error("CusterManager init method can be only invoked once!");
            return;
        }

        _logger.info("ClusterManager is initing with regionId: " + DeploymentConfig.regionId());

        _localHostPortIdentity = DeploymentConfig.ip();
        if (DeploymentConfig.port() != 80 && DeploymentConfig.port() > 0)
            _localHostPortIdentity += ":" + DeploymentConfig.port();
        _logger.info("ClusterManager localHostPortIdentity: " + _localHostPortIdentity);

        _serviceCluster = new ServiceCluster(DeploymentConfig.regionId());
        _serviceCluster.addClusterChangeListener(new ClusterChangeListener() {
            @Override
            public void onChange(ClusterChangeEvent event) {
                updateNodesCache();
            }
        });

        _localNode = new ServiceNode(_localZone, LOCAL_NODE_INIT_URL);
        updateNodesCache();
        _logger.info("ClusterManager localNode: " + _localNode);
        _logger.info("ClusterManager localZoneOtherNodes: " + _localZoneOtherNodes);
        _logger.info("ClusterManager otherZoneNodes: " + otherZoneNodes());

        _scheduledExecutorService = Executors
                .newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("serivce-cluster").build());
        _scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    ArtemisTraceExecutor.INSTANCE.execute("artemis.service.cluster.update-node-status", new Action() {
                        @Override
                        public void execute() {
                            updateNodeStatusMap();
                        }
                    });
                } catch (Throwable ex) {
                    _logger.error("ClusterManager updateNodeStatusMap failed.", ex);
                }
            }
        }, 0, _nodesStatusUpdateInterval.typedValue().intValue(), TimeUnit.MILLISECONDS);

        NodeManager.INSTANCE.init(nodeInitializers);

        _logger.info("ClusterManager is inited with regionId: " + DeploymentConfig.regionId());
    }

    public ServiceNode localNode() {
        return _localNode;
    }

    public List<ServiceNode> localZoneNodes() {
        return _localZoneNodes;
    }

    public List<ServiceNode> localZoneOtherNodes() {
        return _localZoneOtherNodes;
    }

    public List<ServiceNode> otherNodes() {
        return _otherNodes;
    }

    public List<ServiceNode> otherZoneNodes() {
        return _otherZoneNodes;
    }

    public List<ServiceNode> allNodes() {
        return _allNodes;
    }

    public ServiceNodeStatus getNodeStatus(ServiceNode node) {
        ServiceNodeStatus status = _nodeStatusMap.get(node);
        return status == null ? ServiceNodeUtil.newUnknownNodeStatus(node) : status;
    }

    private void updateNodesCache() {
        ListMultimap<Zone, ServiceNode> clusterNodes = _serviceCluster.clusterNodes();
        List<ServiceNode> localZoneNodes = new ArrayList<>();
        List<ServiceNode> otherZoneNodes = new ArrayList<>();
        List<ServiceNode> otherNodes = new ArrayList<>();
        List<ServiceNode> localZoneOtherNodes = new ArrayList<>();
        List<ServiceNode> allNodes = new ArrayList<>();
        for (Zone zone : clusterNodes.keySet()) {
            List<ServiceNode> nodes = clusterNodes.get(zone);
            if (zone.equals(_localZone)) {
                localZoneNodes.addAll(nodes);
                for (ServiceNode node : nodes) {
                    if (isLocalNode(node)) {
                        _localNode.setUrl(node.getUrl());
                        continue;
                    }

                    localZoneOtherNodes.add(node);
                }

                continue;
            }

            otherZoneNodes.addAll(nodes);
        }

        otherNodes.addAll(localZoneOtherNodes);
        otherNodes.addAll(otherZoneNodes);

        if (localZoneNodes.size() > 0)
            _localZoneNodes = localZoneNodes;

        if (localZoneOtherNodes.size() > 0)
            _localZoneOtherNodes = localZoneOtherNodes;

        if (otherZoneNodes.size() > 0)
            _otherZoneNodes = otherZoneNodes;

        if (otherNodes.size() > 0)
            _otherNodes = otherNodes;

        allNodes.addAll(_localZoneNodes);
        allNodes.addAll(_otherZoneNodes);
        _allNodes = allNodes;
    }

    private boolean isLocalNode(ServiceNode node) {
        return node.getUrl().indexOf(_localHostPortIdentity) != -1;
    }

    private void updateNodeStatusMap() {
        HashMap<ServiceNode, ServiceNodeStatus> nodeStatusMap = new HashMap<>();
        nodeStatusMap.put(_localNode, NodeManager.INSTANCE.nodeStatus());

        for (ServiceNode node : _otherNodes) {
            ServiceNodeStatus oldStatus = getNodeStatus(node);
            ServiceNodeStatus newStatus = syncNodeStatus(node);
            if (newStatus == null)
                newStatus = ServiceNodeUtil.newUnknownNodeStatus(node);
            nodeStatusMap.put(node, newStatus);

            if (!Objects.equal(oldStatus, newStatus))
                _logger.info("Node {} status changed from {} to {}", node, oldStatus, newStatus);
        }

        _nodeStatusMap = nodeStatusMap;
    }

    private ServiceNodeStatus syncNodeStatus(ServiceNode node) {
        int retryTimes = _nodeStatusUpdateFailRetryTimes.typedValue().intValue();
        for (int i = 0; i < retryTimes; i++) {
            try {
                StatusServiceClient client = new StatusServiceClient(node.getUrl());
                GetClusterNodeStatusResponse response = client.getClusterNodeStatus(new GetClusterNodeStatusRequest());
                if (ResponseStatusUtil.isSuccess(response.getResponseStatus()))
                    return response.getNodeStatus();

                return null;
            } catch (Throwable ex) {
                String errorMessage = String.format("ClusterManager getClusterNodeStatus of %s failed the %s/%s time", node, i + 1, retryTimes);
                if (HttpClientUtil.isRemoteHostUnavailable(ex)) {
                    _logger.warn(errorMessage, ex);
                    break;
                }

                if (i >= retryTimes - 1) {
                    _logger.error(errorMessage, ex);
                    break;
                }

                _logger.warn(errorMessage, ex);
                Threads.sleep(10);
            }
        }

        return null;
    }

}
