package com.ctrip.soa.artemis.status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.ErrorCodes;
import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.cluster.ClusterManager;
import com.ctrip.soa.artemis.cluster.NodeManager;
import com.ctrip.soa.artemis.cluster.ServiceNode;
import com.ctrip.soa.artemis.cluster.ServiceNodeStatus;
import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.config.DeploymentConfig;
import com.ctrip.soa.artemis.lease.Lease;
import com.ctrip.soa.artemis.lease.LeaseManager;
import com.ctrip.soa.artemis.lease.LeaseUpdateSafeChecker;
import com.ctrip.soa.artemis.ratelimiter.ArtemisRateLimiterManager;
import com.ctrip.soa.artemis.registry.RegistryRepository;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.ctrip.soa.caravan.configuration.ConfigurationSource;
import com.ctrip.soa.caravan.configuration.Property;
import com.ctrip.soa.caravan.configuration.util.PropertyComparator;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;
import com.ctrip.soa.caravan.util.ratelimiter.RateLimiter;
import com.ctrip.soa.caravan.util.ratelimiter.RateLimiterConfig;
import com.ctrip.soa.caravan.common.concurrent.collect.circularbuffer.timebucket.TimeBufferConfig;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class StatusServiceImpl implements StatusService {

    private static final Logger _logger = LoggerFactory.getLogger(StatusServiceImpl.class);

    private static StatusServiceImpl _instance;

    public static StatusServiceImpl getInstance() {
        if (_instance == null) {
            synchronized (StatusServiceImpl.class) {
                if (_instance == null)
                    _instance = new StatusServiceImpl();
            }
        }

        return _instance;
    }

    private RateLimiter _rateLimiter = ArtemisRateLimiterManager.Instance.getRateLimiter("artemis.service.status",
            new RateLimiterConfig(true, new RangePropertyConfig<Long>(30L, 1L, 10 * 1000L), new TimeBufferConfig(10 * 1000, 1000)));

    private RegistryRepository _registryRepository = RegistryRepository.getInstance();

    private StatusServiceImpl() {

    }

    @Override
    public GetClusterNodeStatusResponse getClusterNodeStatus(GetClusterNodeStatusRequest request) {
        try {
            return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.status.get-cluster-node", new Func<GetClusterNodeStatusResponse>() {
                @Override
                public GetClusterNodeStatusResponse execute() {
                    return new GetClusterNodeStatusResponse(NodeManager.INSTANCE.nodeStatus(), ResponseStatusUtil.SUCCESS_STATUS);
                }
            });
        } catch (Throwable ex) {
            _logger.warn("GetClusterNodeStatus failed. request: " + request, ex);
            return new GetClusterNodeStatusResponse(null, ResponseStatusUtil.newFailStatus(ex.getMessage(), ErrorCodes.INTERNAL_SERVICE_ERROR));
        }
    }

    @Override
    public GetClusterStatusResponse getClusterStatus(GetClusterStatusRequest request) {
        if (_rateLimiter.isRateLimited("get-cluster"))
            return new GetClusterStatusResponse(0, null, ResponseStatusUtil.RATE_LIMITED_STATUS);

        try {
            return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.status.get-cluster", new Func<GetClusterStatusResponse>() {
                @Override
                public GetClusterStatusResponse execute() {
                    List<ServiceNodeStatus> nodesStatus = new ArrayList<>();
                    for (ServiceNode node : ClusterManager.INSTANCE.allNodes()) {
                        nodesStatus.add(ClusterManager.INSTANCE.getNodeStatus(node));
                    }

                    return new GetClusterStatusResponse(nodesStatus.size(), nodesStatus, ResponseStatusUtil.SUCCESS_STATUS);
                }
            });
        } catch (Throwable ex) {
            _logger.warn("GetClusterStatus failed. request: " + request, ex);
            return new GetClusterStatusResponse(0, null, ResponseStatusUtil.newFailStatus(ex.getMessage(), ErrorCodes.INTERNAL_SERVICE_ERROR));
        }
    }

    @Override
    public GetLeasesStatusResponse getLeasesStatus(final GetLeasesStatusRequest request) {
        return getLeasesStatus("get-leases", "artemis.service.status.get-leases", request, _registryRepository.getLeaseManager());
    }

    @Override
    public GetLeasesStatusResponse getLegacyLeasesStatus(final GetLeasesStatusRequest request) {
        return getLeasesStatus("get-legacy-leases", "artemis.service.status.get-legacy-leases", request, _registryRepository.getLegacyInstanceLeaseManager());
    }

    @Override
    public GetConfigStatusResponse getConfigStatus(GetConfigStatusRequest request) {
        if (_rateLimiter.isRateLimited("get-config"))
            return new GetConfigStatusResponse(null, null, ResponseStatusUtil.RATE_LIMITED_STATUS);

        try {
            return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.status.get-config", new Func<GetConfigStatusResponse>() {
                @Override
                public GetConfigStatusResponse execute() {
                    List<Property> propertyCache = Lists.<Property>newArrayList(ArtemisConfig.properties().typedProperties());
                    Collections.sort(propertyCache, PropertyComparator.DEFAULT);
                    Map<String, String> properties = new LinkedHashMap<>();
                    for (Property property : propertyCache) {
                        properties.put(property.key(), property.toString());
                    }

                    Map<String, Integer> sources = new LinkedHashMap<>();
                    for (ConfigurationSource source : ArtemisConfig.manager().sources()) {
                        sources.put(source.sourceId(), source.priority());
                    }

                    return new GetConfigStatusResponse(sources, properties, ResponseStatusUtil.SUCCESS_STATUS);
                }
            });
        } catch (Throwable ex) {
            _logger.warn("GetConfigStatus failed. request: " + request, ex);
            return new GetConfigStatusResponse(null, null, ResponseStatusUtil.newFailStatus(ex.getMessage(), ErrorCodes.INTERNAL_SERVICE_ERROR));
        }
    }

    @Override
    public GetDeploymentStatusResponse getDeploymentStatus(GetDeploymentStatusRequest request) {
        if (_rateLimiter.isRateLimited("get-deployment")) {
            GetDeploymentStatusResponse response = new GetDeploymentStatusResponse();
            response.setResponseStatus(ResponseStatusUtil.RATE_LIMITED_STATUS);
            return response;
        }

        try {
            return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.status.get-deployment", new Func<GetDeploymentStatusResponse>() {
                @Override
                public GetDeploymentStatusResponse execute() {
                    List<Property> propertyCache = Lists.<Property>newArrayList(DeploymentConfig.properties().typedProperties());
                    Collections.sort(propertyCache, PropertyComparator.DEFAULT);
                    Map<String, String> properties = new LinkedHashMap<>();
                    for (Property property : propertyCache) {
                        properties.put(property.key(), property.toString());
                    }

                    Map<String, Integer> sources = new LinkedHashMap<>();
                    for (ConfigurationSource source : DeploymentConfig.manager().sources()) {
                        sources.put(source.sourceId(), source.priority());
                    }

                    return new GetDeploymentStatusResponse(DeploymentConfig.regionId(), DeploymentConfig.zoneId(), DeploymentConfig.appId(),
                            DeploymentConfig.machineName(), DeploymentConfig.ip(), DeploymentConfig.port(), DeploymentConfig.protocol(),
                            DeploymentConfig.path(), sources, properties, ResponseStatusUtil.SUCCESS_STATUS);
                }
            });
        } catch (Throwable ex) {
            _logger.warn("GetDeploymentStatus failed. request: " + request, ex);
            GetDeploymentStatusResponse response = new GetDeploymentStatusResponse();
            response.setResponseStatus(ResponseStatusUtil.newFailStatus(ex.getMessage(), ErrorCodes.INTERNAL_SERVICE_ERROR));
            return response;
        }
    }

    private GetLeasesStatusResponse getLeasesStatus(String opName, final String traceKey, final GetLeasesStatusRequest request,
            final LeaseManager<Instance> leaseManager) {
        if (_rateLimiter.isRateLimited(opName))
            return new GetLeasesStatusResponse(0, 0, 0, false, false, 0, null, ResponseStatusUtil.RATE_LIMITED_STATUS);

        try {
            return ArtemisTraceExecutor.INSTANCE.execute(traceKey, new Func<GetLeasesStatusResponse>() {
                @Override
                public GetLeasesStatusResponse execute() {
                    Map<Service, List<LeaseStatus>> leasesStatusMap = new HashMap<>();
                    List<String> serviceIds = request.getServiceIds();
                    ListMultimap<Service, Lease<Instance>> leaseMultiMap = CollectionValues.isNullOrEmpty(serviceIds)
                            ? _registryRepository.getLeases(leaseManager) : _registryRepository.getLeases(serviceIds, leaseManager);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    int leaseCount = 0;
                    for (Service service : leaseMultiMap.keySet()) {
                        List<LeaseStatus> leasesStatus = new ArrayList<>();
                        List<Lease<Instance>> leases = leaseMultiMap.get(service);
                        for (Lease<Instance> lease : leases) {
                            leasesStatus.add(new LeaseStatus(lease.data().toString(), dateFormat.format(lease.creationTime()),
                                    dateFormat.format(lease.renewalTime()), dateFormat.format(lease.evictionTime()), lease.ttl()));
                        }

                        leasesStatusMap.put(service, leasesStatus);
                        leaseCount += leasesStatus.size();
                    }

                    LeaseUpdateSafeChecker leaseUpdateSafeChecker = leaseManager.leaseUpdateSafeChecker();
                    return new GetLeasesStatusResponse(leaseUpdateSafeChecker.maxCount(), leaseUpdateSafeChecker.maxCountLastUpdateTime(),
                            leaseUpdateSafeChecker.countLastTimeWindow(), leaseUpdateSafeChecker.isSafe(), leaseUpdateSafeChecker.isEnabled(),
                            leaseCount, leasesStatusMap, ResponseStatusUtil.SUCCESS_STATUS);
                }
            });
        } catch (Throwable ex) {
            _logger.warn("GetLeasesStatus failed. request: " + request, ex);
            return new GetLeasesStatusResponse(0, 0, 0, false, false, 0, null,
                    ResponseStatusUtil.newFailStatus(ex.getMessage(), ErrorCodes.INTERNAL_SERVICE_ERROR));
        }
    }

}
