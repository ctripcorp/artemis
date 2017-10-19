package com.ctrip.soa.artemis.cluster;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.soa.artemis.ErrorCodes;
import com.ctrip.soa.artemis.ratelimiter.ArtemisRateLimiterManager;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.ctrip.soa.artemis.util.ServiceNodeUtil;
import com.ctrip.soa.caravan.common.concurrent.collect.circularbuffer.timebucket.TimeBufferConfig;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctrip.soa.caravan.common.value.BooleanValues;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;
import com.ctrip.soa.caravan.util.ratelimiter.RateLimiter;
import com.ctrip.soa.caravan.util.ratelimiter.RateLimiterConfig;
import com.google.common.base.Objects;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class ClusterServiceImpl implements ClusterService {

    private static ClusterServiceImpl _instance;

    public static ClusterService getInstance() {
        if (_instance == null) {
            synchronized (ClusterServiceImpl.class) {
                if (_instance == null)
                    _instance = new ClusterServiceImpl();
            }
        }

        return _instance;
    }

    private RateLimiter _rateLimiter = ArtemisRateLimiterManager.Instance.getRateLimiter("artemis.service.cluster",
            new RateLimiterConfig(true, new RangePropertyConfig<Long>(10 * 1000L, 100L, 100 * 1000L), new TimeBufferConfig(10 * 1000, 1000)));

    private ClusterServiceImpl() {

    }

    @Override
    public GetServiceNodesResponse getUpRegistryNodes(final GetServiceNodesRequest request) {
        if (_rateLimiter.isRateLimited("up-registry-nodes"))
            return new GetServiceNodesResponse(null, ResponseStatusUtil.RATE_LIMITED_STATUS);

        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.cluster.up-registry-nodes", new Func<GetServiceNodesResponse>() {
            @Override
            public GetServiceNodesResponse execute() {
                List<ServiceNode> serviceNodes = new ArrayList<>();
                for (ServiceNode node : ClusterManager.INSTANCE.allNodes()) {
                    ServiceNodeStatus nodeStatus = ClusterManager.INSTANCE.getNodeStatus(node);
                    if (!ServiceNodeUtil.canServiceRegistry(nodeStatus))
                        continue;

                    if (BooleanValues.isTrue(nodeStatus.isAllowRegistryFromOtherZone()) || fromSameZone(node, request))
                        serviceNodes.add(node);
                }

                return createResponse(serviceNodes);
            }
        });
    }

    @Override
    public GetServiceNodesResponse getUpDiscoveryNodes(final GetServiceNodesRequest request) {
        if (_rateLimiter.isRateLimited("up-discovery-nodes"))
            return new GetServiceNodesResponse(null, ResponseStatusUtil.RATE_LIMITED_STATUS);

        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.cluster.up-discovery-nodes", new Func<GetServiceNodesResponse>() {
            @Override
            public GetServiceNodesResponse execute() {
                List<ServiceNode> serviceNodes = new ArrayList<>();
                for (ServiceNode node : ClusterManager.INSTANCE.allNodes()) {
                    ServiceNodeStatus nodeStatus = ClusterManager.INSTANCE.getNodeStatus(node);
                    if (!ServiceNodeUtil.canServiceDiscovery(nodeStatus))
                        continue;

                    if (BooleanValues.isTrue(nodeStatus.isAllowDiscoveryFromOtherZone()) || fromSameZone(node, request))
                        serviceNodes.add(node);
                }

                return createResponse(serviceNodes);
            }
        });
    }

    private GetServiceNodesResponse createResponse(List<ServiceNode> serviceNodes) {
        if (CollectionValues.isNullOrEmpty(serviceNodes))
            return new GetServiceNodesResponse(null, ResponseStatusUtil.newFailStatus("No available service nodes.", ErrorCodes.DATA_NOT_FOUND));

        return new GetServiceNodesResponse(serviceNodes, ResponseStatusUtil.SUCCESS_STATUS);
    }

    private boolean fromSameZone(ServiceNode node, GetServiceNodesRequest request) {
        if (node.getZone() == null)
            return false;

        if (!Objects.equal(node.getZone().getZoneId(), request.getZoneId()))
            return false;

        if (!Objects.equal(node.getZone().getRegionId(), request.getRegionId()))
            return false;

        return true;
    }

}
