package com.ctrip.soa.artemis.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.ErrorCodes;
import com.ctrip.soa.artemis.InstanceChange;
import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.cache.ServicesDeltaGenerator;
import com.ctrip.soa.artemis.cache.VersionedCacheManager;
import com.ctrip.soa.artemis.cache.VersionedData;
import com.ctrip.soa.artemis.cluster.NodeManager;
import com.ctrip.soa.artemis.cluster.ServiceNodeStatus;
import com.ctrip.soa.artemis.config.DeploymentConfig;
import com.ctrip.soa.artemis.registry.RegistryRepository;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.ctrip.soa.artemis.util.SameRegionChecker;
import com.ctrip.soa.artemis.util.SameZoneChecker;
import com.ctrip.soa.artemis.util.ServiceNodeUtil;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctrip.soa.caravan.common.value.ArrayValues;
import com.ctrip.soa.caravan.common.value.BooleanValues;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class DiscoveryServiceImpl implements DiscoveryService {

    private static final Logger _logger = LoggerFactory.getLogger(DiscoveryServiceImpl.class);

    private static DiscoveryServiceImpl _instance;

    public static DiscoveryServiceImpl getInstance() {
        if (_instance == null) {
            synchronized (DiscoveryServiceImpl.class) {
                if (_instance == null)
                    _instance = new DiscoveryServiceImpl();
            }
        }

        return _instance;
    }

    private VersionedCacheManager<List<Service>, Map<Service, List<InstanceChange>>> _versionedCacheManager = new VersionedCacheManager<>(
            "artemis.service.discovery", new ServicesDataGenerator(), ServicesDeltaGenerator.DEFAULT);

    private RegistryRepository _registryRepository = RegistryRepository.getInstance();

    private List<DiscoveryFilter> _filters = new ArrayList<>();

    private DiscoveryServiceImpl() {

    }

    public synchronized void addFilters(DiscoveryFilter... filters) {
        if (ArrayValues.isNullOrEmpty(filters))
            return;

        for (DiscoveryFilter filter : filters) {
            if (filter == null)
                continue;

            _filters.add(filter);
        }
    }

    @Override
    public LookupResponse lookup(final LookupRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.discovery.lookup", new Func<LookupResponse>() {
            @Override
            public LookupResponse execute() {
                return lookupImpl(request);
            }
        });
    }
    
    @Override
    public GetServiceResponse getService(final GetServiceRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.discovery.service", new Func<GetServiceResponse>() {
            @Override
            public GetServiceResponse execute() {
                return getServiceImpl(request);
            }
        });
    }
    
    @Override
    public GetServicesResponse getServices(final GetServicesRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.discovery.services", new Func<GetServicesResponse>() {
            @Override
            public GetServicesResponse execute() {
                return getServicesImpl(request);
            }
        });
    }
    
    private LookupResponse lookupImpl(final LookupRequest request) {
        try {
            String errorMessage = null;
            if (request == null || CollectionValues.isNullOrEmpty(request.getDiscoveryConfigs())) {
                errorMessage = "Request is null or request.discoveryConfigs is empty.";
                return new LookupResponse(null, ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.BAD_REQUEST));
            }

            errorMessage = checkDiscoveryStatus();
            if (!StringValues.isNullOrWhitespace(errorMessage))
                return new LookupResponse(null, ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.SERVICE_UNAVAILABLE));

            errorMessage = checkSameZone(request.getRegionId(), request.getZoneId());
            if (!StringValues.isNullOrWhitespace(errorMessage))
                return new LookupResponse(null, ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.NO_PERMISSION));

            List<Service> services = new ArrayList<>();
            for (DiscoveryConfig discoveryConfig : request.getDiscoveryConfigs()) {
                if (discoveryConfig == null || StringValues.isNullOrWhitespace(discoveryConfig.getServiceId()))
                    continue;

                Service service = _registryRepository.getService(discoveryConfig.getServiceId());
                if (service == null)
                    service = new Service(discoveryConfig.getServiceId());

                filterService(service, discoveryConfig);
                services.add(service);
            }

            return new LookupResponse(services, ResponseStatusUtil.SUCCESS_STATUS);
        } catch (Throwable ex) {
            _logger.error("Lookup failed. Request: " + JacksonJsonSerializer.INSTANCE.serialize(request), ex);
            return new LookupResponse(null, ResponseStatusUtil.newFailStatus(ex.getMessage(), ErrorCodes.INTERNAL_SERVICE_ERROR));
        }
    }

    private GetServiceResponse getServiceImpl(GetServiceRequest request) {
        try {
            String errorMessage = null;
            if (request == null || request.getDiscoveryConfig() == null || StringValues.isNullOrWhitespace(request.getDiscoveryConfig().getServiceId())) {
                errorMessage = "Request is null or request.discoveryConfig is null.";
                return new GetServiceResponse(null, ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.BAD_REQUEST));
            }

            errorMessage = checkDiscoveryStatus();
            if (!StringValues.isNullOrWhitespace(errorMessage))
                return new GetServiceResponse(null, ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.SERVICE_UNAVAILABLE));

            errorMessage = checkSameZone(request.getRegionId(), request.getZoneId());
            if (!StringValues.isNullOrWhitespace(errorMessage))
                return new GetServiceResponse(null, ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.NO_PERMISSION));

            Service service = _registryRepository.getService(request.getDiscoveryConfig().getServiceId());
            filterService(service, request.getDiscoveryConfig());
            if (service == null)
                service = new Service(request.getDiscoveryConfig().getServiceId());
            return new GetServiceResponse(service, ResponseStatusUtil.SUCCESS_STATUS);
        } catch (Throwable ex) {
            _logger.error("GetService failed. Request: " + JacksonJsonSerializer.INSTANCE.serialize(request), ex);
            return new GetServiceResponse(null, ResponseStatusUtil.newFailStatus(ex.getMessage(), ErrorCodes.INTERNAL_SERVICE_ERROR));
        }
    }

    private GetServicesResponse getServicesImpl(final GetServicesRequest request) {
        try {
            String errorMessage = checkDiscoveryStatus();
            if (!StringValues.isNullOrWhitespace(errorMessage))
                return new GetServicesResponse(null, 0, ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.SERVICE_UNAVAILABLE));

            VersionedData<List<Service>> data = _versionedCacheManager.get();
            return new GetServicesResponse(data.getData(), data.getVersion(), ResponseStatusUtil.SUCCESS_STATUS);
        } catch (Throwable ex) {
            _logger.error("GetServices failed. Request: " + JacksonJsonSerializer.INSTANCE.serialize(request), ex);
            return new GetServicesResponse(null, 0, ResponseStatusUtil.newFailStatus(ex.getMessage(), ErrorCodes.INTERNAL_SERVICE_ERROR));
        }
    }

    @Override
    public GetServicesDeltaResponse getServicesDelta(final GetServicesDeltaRequest request) {
        try {
            String errorMessage = checkDiscoveryStatus();
            if (!StringValues.isNullOrWhitespace(errorMessage))
                return new GetServicesDeltaResponse(null, 0, ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.SERVICE_UNAVAILABLE));

            VersionedData<Map<Service, List<InstanceChange>>> delta = _versionedCacheManager.getDelta(request.getVersion());
            if (delta == null)
                return new GetServicesDeltaResponse(null, 0, ResponseStatusUtil.newFailStatus("Delta is not found.", ErrorCodes.DATA_NOT_FOUND));

            return new GetServicesDeltaResponse(delta.getData(), delta.getVersion(), ResponseStatusUtil.SUCCESS_STATUS);
        } catch (Throwable ex) {
            _logger.error("GetServicesDelta failed. Request: " + JacksonJsonSerializer.INSTANCE.serialize(request), ex);
            return new GetServicesDeltaResponse(null, 0, ResponseStatusUtil.newFailStatus(ex.getMessage(), ErrorCodes.INTERNAL_SERVICE_ERROR));
        }
    }

    private String checkDiscoveryStatus() {
        ServiceNodeStatus nodeStatus = NodeManager.INSTANCE.nodeStatus();
        if (ServiceNodeUtil.canServiceDiscovery(nodeStatus))
            return null;

        return "Serivce discovery is not in up state. Current status: " + nodeStatus;
    }

    private String checkSameZone(String regionId, String zoneId) {
        if (!SameRegionChecker.DEFAULT.isSameRegion(regionId))
            return String.format("regionId is not the same as the registry node. regionId: %s, registry node.regionId: %s", regionId,
                    DeploymentConfig.regionId());

        if (BooleanValues.isTrue(NodeManager.INSTANCE.nodeStatus().isAllowDiscoveryFromOtherZone()))
            return null;

        if (SameZoneChecker.DEFAULT.isSameZone(zoneId))
            return null;

        return String.format("zoneId is not the same as the registry node. zoneId: %s, registry node.zoneId: %s", zoneId,
                DeploymentConfig.zoneId());
    }

    private class ServicesDataGenerator implements Func<List<Service>> {
        @Override
        public List<Service> execute() {
            List<Service> services = new ArrayList<>();
            List<Service> origin = _registryRepository.getServices();
            if (CollectionValues.isNullOrEmpty(origin))
                return services;

            DiscoveryConfig genericConfig = DiscoveryConfig.GENERIC.clone();
            for (Service service : origin) {
                filterService(service, genericConfig);
                if (CollectionValues.isNullOrEmpty(service.getInstances()))
                    continue;
                services.add(service);
            }

            return services;
        }
    }

    private void filterService(Service service, DiscoveryConfig discoveryConfig) {
        if (service == null)
            return;

        for (DiscoveryFilter filter : _filters) {
            try {
                filter.filter(service, discoveryConfig);
            } catch (Throwable ex) {
                _logger.error("Failed to execute filter " + filter, ex);
            }
        }
    }

}
