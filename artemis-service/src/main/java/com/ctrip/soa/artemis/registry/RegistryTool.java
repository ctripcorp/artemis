package com.ctrip.soa.artemis.registry;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.ErrorCodes;
import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.ResponseStatus;
import com.ctrip.soa.artemis.cluster.NodeManager;
import com.ctrip.soa.artemis.cluster.ServiceNodeStatus;
import com.ctrip.soa.artemis.config.DeploymentConfig;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.ctrip.soa.artemis.util.SameRegionChecker;
import com.ctrip.soa.artemis.util.SameZoneChecker;
import com.ctrip.soa.artemis.util.ServiceNodeUtil;
import com.ctrip.soa.caravan.common.value.BooleanValues;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;
import com.ctrip.soa.caravan.common.delegate.Func;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public final class RegistryTool {

    private static final Logger _logger = LoggerFactory.getLogger(RegistryTool.class);

    private RegistryTool() {

    }

    public static ExecutionResult replicationExecute(String traceKey, final HasInstances request, final Executor executor) {
        return execute(traceKey, request, executor, true);
    }

    public static ExecutionResult execute(String traceKey, final HasInstances request, final Executor executor) {
        return execute(traceKey, request, executor, false);
    }

    private static ExecutionResult execute(String traceKey, final HasInstances request, final Executor executor, final boolean isReplication) {
        try {
            return ArtemisTraceExecutor.INSTANCE.execute(traceKey, new Func<ExecutionResult>() {
                @Override
                public ExecutionResult execute() {
                    return RegistryTool.execute(request, executor, isReplication);
                }
            });
        } catch (Throwable ex) {
            _logger.error("Request failed. Request: " + JacksonJsonSerializer.INSTANCE.serialize(request), ex);
            return new ExecutionResult(null, ResponseStatusUtil.newFailStatus(ex.getMessage(), ErrorCodes.INTERNAL_SERVICE_ERROR));
        }
    }

    private static ExecutionResult execute(HasInstances request, Executor executor, boolean isReplication) {
        String errorMessage = null;
        List<FailedInstance> failedInstances = null;

        errorMessage = checkRequest(request);
        if (!StringValues.isNullOrWhitespace(errorMessage))
            return new ExecutionResult(failedInstances, ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.BAD_REQUEST));

        errorMessage = checkRegistryStatus(isReplication);
        if (!StringValues.isNullOrWhitespace(errorMessage))
            return new ExecutionResult(failedInstances, ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.SERVICE_UNAVAILABLE));

        errorMessage = StringValues.EMPTY;
        for (Instance instance : request.getInstances()) {
            String errorMessage2 = null;
            String errorCode = null;
            try {
                if (instance == null)
                    continue;

                errorMessage2 = checkSameZone(instance.getRegionId(), instance.getZoneId(), isReplication);
                if (!StringValues.isNullOrWhitespace(errorMessage2)) {
                    errorCode = ErrorCodes.NO_PERMISSION;
                    continue;
                }

                errorMessage2 = executor.execute(instance);
                if (!StringValues.isNullOrWhitespace(errorMessage2))
                    errorCode = ErrorCodes.DATA_NOT_FOUND;
            } catch (Throwable ex) {
                _logger.warn("Execute failed. Instance: " + JacksonJsonSerializer.INSTANCE.serialize(instance), ex);
                errorMessage2 = "Exception: " + ex.getMessage();
                errorCode = ErrorCodes.INTERNAL_SERVICE_ERROR;
            } finally {
                if (errorCode != null) {
                    if (failedInstances == null)
                        failedInstances = new ArrayList<>();
                    StringBuffer tmp = new StringBuffer(errorMessage);
                    if (!StringValues.isNullOrWhitespace(errorMessage))
                        tmp.append(',');
                    tmp.append(instance);
                    tmp.append(": ");
                    tmp.append(errorMessage2);
                    failedInstances.add(new FailedInstance(instance, errorCode, tmp.toString()));
                }
            }
        }

        ResponseStatus responseStatus = null;
        if (CollectionValues.isNullOrEmpty(failedInstances))
            responseStatus = ResponseStatusUtil.SUCCESS_STATUS;
        else
            responseStatus = ResponseStatusUtil.newPartialFailStatus(errorMessage);

        return new ExecutionResult(failedInstances, responseStatus);
    }

    private static String checkRequest(HasInstances request) {
        if (request == null)
            return "request is null";

        if (CollectionValues.isNullOrEmpty(request.getInstances()))
            return "request.instances is null or empty.";

        return null;
    }

    public static String checkRegistryStatus(boolean isReplication) {
        if (isReplication)
            return null;

        ServiceNodeStatus nodeStatus = NodeManager.INSTANCE.nodeStatus();
        if (ServiceNodeUtil.canServiceRegistry(nodeStatus))
            return null;

        return "Serivce registry is not in up state. Current status: " + nodeStatus;
    }

    public static String checkSameZone(String regionId, String zoneId, boolean isReplication) {
        if (!SameRegionChecker.DEFAULT.isSameRegion(regionId))
            return String.format("regionId is not the same as the registry node. regionId: %s, registry node.regionId: %s", regionId,
                    DeploymentConfig.regionId());

        if (isReplication)
            return null;

        if (SameZoneChecker.DEFAULT.isSameZone(zoneId))
            return null;

        if (BooleanValues.isTrue(NodeManager.INSTANCE.nodeStatus().isAllowRegistryFromOtherZone()))
            return null;

        return String.format("zoneId is not the same as the registry node. zoneId: %s, registry node.zoneId: %s", zoneId,
                DeploymentConfig.zoneId());
    }

    public static class ExecutionResult {

        private List<FailedInstance> _failedInstances;
        private ResponseStatus _responseStatus;

        public ExecutionResult(List<FailedInstance> failedInstances, ResponseStatus responseStatus) {
            _failedInstances = failedInstances;
            _responseStatus = responseStatus;
        }

        public List<FailedInstance> failedInstances() {
            return _failedInstances;
        }

        public ResponseStatus responseStatus() {
            return _responseStatus;
        }

    }

    public interface Executor {

        String execute(Instance instance);

    }

}
