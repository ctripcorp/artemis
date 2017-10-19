package com.ctrip.soa.artemis.registry.replication;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.ratelimiter.ArtemisRateLimiterManager;
import com.ctrip.soa.artemis.ErrorCodes;
import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.registry.RegistryTool;
import com.ctrip.soa.artemis.registry.RegistryTool.ExecutionResult;
import com.ctrip.soa.artemis.registry.RegistryTool.Executor;
import com.ctrip.soa.artemis.registry.HeartbeatRequest;
import com.ctrip.soa.artemis.registry.HeartbeatResponse;
import com.ctrip.soa.artemis.registry.RegisterRequest;
import com.ctrip.soa.artemis.registry.RegisterResponse;
import com.ctrip.soa.artemis.registry.RegistryRepository;
import com.ctrip.soa.artemis.registry.UnregisterRequest;
import com.ctrip.soa.artemis.registry.UnregisterResponse;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.ctrip.soa.caravan.util.ratelimiter.RateLimiter;
import com.ctrip.soa.caravan.util.ratelimiter.RateLimiterConfig;
import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;
import com.ctrip.soa.caravan.common.concurrent.collect.circularbuffer.timebucket.TimeBufferConfig;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class RegistryReplicationServiceImpl implements RegistryReplicationService {

    private static final Logger _logger = LoggerFactory.getLogger(RegistryReplicationServiceImpl.class);

    private static RegistryReplicationServiceImpl _instance;

    public static RegistryReplicationServiceImpl getInstance() {
        if (_instance == null) {
            synchronized (RegistryReplicationServiceImpl.class) {
                if (_instance == null)
                    _instance = new RegistryReplicationServiceImpl();
            }
        }

        return _instance;
    }

    private RegistryRepository _repository = RegistryRepository.getInstance();

    private RateLimiter _rateLimiter = ArtemisRateLimiterManager.Instance.getRateLimiter("artemis.service.registry.replication",
            new RateLimiterConfig(true, new RangePropertyConfig<Long>(1000 * 1000L, 1000L, 10 * 1000 * 1000L), new TimeBufferConfig(10 * 1000, 1000)));

    private RegistryReplicationServiceImpl() {

    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (_rateLimiter.isRateLimited("register"))
            return new RegisterResponse(null, ResponseStatusUtil.RATE_LIMITED_STATUS);

        ExecutionResult result = RegistryTool.replicationExecute("artemis.service.registry.replication.register", request, new Executor() {

            @Override
            public String execute(Instance instance) {
                _repository.register(instance);
                return null;
            }

        });

        return new RegisterResponse(result.failedInstances(), result.responseStatus());
    }

    @Override
    public HeartbeatResponse heartbeat(HeartbeatRequest request) {
        if (_rateLimiter.isRateLimited("heartbeat"))
            return new HeartbeatResponse(null, ResponseStatusUtil.RATE_LIMITED_STATUS);

        ExecutionResult result = RegistryTool.replicationExecute("artemis.service.registry.replication.heartbeat", request, new Executor() {

            @Override
            public String execute(Instance instance) {
                boolean success = _repository.heartbeat(instance);
                if (!success)
                    _repository.register(instance);
                return null;
            }

        });

        return new HeartbeatResponse(result.failedInstances(), result.responseStatus());
    }

    @Override
    public UnregisterResponse unregister(UnregisterRequest request) {
        if (_rateLimiter.isRateLimited("unregister"))
            return new UnregisterResponse(null, ResponseStatusUtil.RATE_LIMITED_STATUS);

        ExecutionResult result = RegistryTool.replicationExecute("artemis.service.registry.replication.unregister", request, new Executor() {

            @Override
            public String execute(Instance instance) {
                _repository.unregister(instance);
                return null;
            }

        });

        return new UnregisterResponse(result.failedInstances(), result.responseStatus());
    }

    @Override
    public GetServicesResponse getServices(final GetServicesRequest request) {
        if (_rateLimiter.isRateLimited("get-services"))
            return new GetServicesResponse(null, ResponseStatusUtil.RATE_LIMITED_STATUS);

        try {
            return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.registry.replication.get-applications", new Func<GetServicesResponse>() {
                @Override
                public GetServicesResponse execute() {
                    String errorMessage = RegistryTool.checkRegistryStatus(true);
                    if (!StringValues.isNullOrWhitespace(errorMessage))
                        return new GetServicesResponse(null, ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.SERVICE_UNAVAILABLE));

                    errorMessage = RegistryTool.checkSameZone(request.getRegionId(), request.getZoneId(), true);
                    if (!StringValues.isNullOrWhitespace(errorMessage))
                        return new GetServicesResponse(null, ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.NO_PERMISSION));

                    List<Service> applications = _repository.getServices();
                    return new GetServicesResponse(applications, ResponseStatusUtil.SUCCESS_STATUS);
                }
            });
        } catch (Throwable ex) {
            _logger.error("GetApplications failed. Request: " + JacksonJsonSerializer.INSTANCE.serialize(request), ex);
            return new GetServicesResponse(null, ResponseStatusUtil.newFailStatus(ex.getMessage(), ErrorCodes.INTERNAL_SERVICE_ERROR));
        }
    }

}
