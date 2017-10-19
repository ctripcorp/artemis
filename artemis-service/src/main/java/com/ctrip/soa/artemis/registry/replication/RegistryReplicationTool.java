package com.ctrip.soa.artemis.registry.replication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.soa.caravan.common.metric.MetricConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.ErrorCodes;
import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.cluster.ClusterManager;
import com.ctrip.soa.artemis.cluster.ServiceNode;
import com.ctrip.soa.artemis.cluster.ServiceNodeStatus;
import com.ctrip.soa.artemis.metric.ArtemisMetricManagers;
import com.ctrip.soa.artemis.metric.MetricNames;
import com.ctrip.soa.artemis.registry.FailedInstance;
import com.ctrip.soa.artemis.registry.HasFailedInstances;
import com.ctrip.soa.artemis.registry.HeartbeatRequest;
import com.ctrip.soa.artemis.registry.RegisterRequest;
import com.ctrip.soa.artemis.registry.UnregisterRequest;
import com.ctrip.soa.artemis.replication.ReplicationTool;
import com.ctrip.soa.artemis.taskdispatcher.TaskErrorCode;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.artemis.util.HttpClientUtil;
import com.ctrip.soa.artemis.util.RequestExecutor;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.ctrip.soa.artemis.util.ServiceNodeUtil;
import com.ctrip.soa.caravan.common.metric.EventMetric;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.google.common.collect.ImmutableMap;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class RegistryReplicationTool extends ReplicationTool {

    private static final String FAILED_INSTANCE_STATUS_CODE_EVENT_TYPE = "artemis.service.registry.replication.failed-instance.error-code";

    private static final Logger _logger = LoggerFactory.getLogger(RegistryReplicationTool.class);

    private interface RegistryReplicationRequestExecutor extends RequestExecutor<RegistryReplicationServiceClient, List<Instance>, HasFailedInstances> {

    }

    private interface RegistryReplicationTaskGenerator {
        RegistryReplicationTask create(Instance instance, String serviceUrl, long expiryTime, TaskErrorCode errorCode);
    }

    private static Set<Class<?>> _supportedSubTaskTypes = new HashSet<>();
    private static Map<Class<?>, RegistryReplicationRequestExecutor> _requestExecutors = new HashMap<>();
    private static Map<Class<?>, RegistryReplicationTaskGenerator> _taskGenerators = new HashMap<>();

    static {
        _supportedSubTaskTypes.add(RegisterTask.class);
        _supportedSubTaskTypes.add(UnregisterTask.class);
        _supportedSubTaskTypes.add(HeartbeatTask.class);
        _supportedSubTaskTypes = Collections.unmodifiableSet(_supportedSubTaskTypes);

        _requestExecutors.put(RegisterTask.class, new RegistryReplicationRequestExecutor() {
            @Override
            public HasFailedInstances execute(RegistryReplicationServiceClient client, List<Instance> instances) {
                return client.register(new RegisterRequest(instances));
            }
        });
        _requestExecutors.put(UnregisterTask.class, new RegistryReplicationRequestExecutor() {
            @Override
            public HasFailedInstances execute(RegistryReplicationServiceClient client, List<Instance> instances) {
                return client.unregister(new UnregisterRequest(instances));
            }
        });
        _requestExecutors.put(HeartbeatTask.class, new RegistryReplicationRequestExecutor() {
            @Override
            public HasFailedInstances execute(RegistryReplicationServiceClient client, List<Instance> instances) {
                return client.heartbeat(new HeartbeatRequest(instances));
            }
        });

        _taskGenerators.put(RegisterTask.class, new RegistryReplicationTaskGenerator() {
            @Override
            public RegistryReplicationTask create(Instance instance, String serviceUrl, long expiryTime, TaskErrorCode errorCode) {
                return new RegisterTask(instance, serviceUrl, expiryTime, errorCode);
            }
        });
        _taskGenerators.put(UnregisterTask.class, new RegistryReplicationTaskGenerator() {
            @Override
            public RegistryReplicationTask create(Instance instance, String serviceUrl, long expiryTime, TaskErrorCode errorCode) {
                return new UnregisterTask(instance, serviceUrl, expiryTime, errorCode);
            }
        });
        _taskGenerators.put(HeartbeatTask.class, new RegistryReplicationTaskGenerator() {
            @Override
            public RegistryReplicationTask create(Instance instance, String serviceUrl, long expiryTime, TaskErrorCode errorCode) {
                return new HeartbeatTask(instance, serviceUrl, expiryTime, errorCode);
            }
        });
    };

    public static Set<Class<?>> supportedSubTaskTypes() {
        return _supportedSubTaskTypes;
    }

    public static RegistryReplicationTask toFailedTask(RegistryReplicationTask task, TaskErrorCode errorCode) {
        return _taskGenerators.get(task.getClass()).create(task.instance(), task.serviceUrl(), task.expiryTime(), errorCode);
    }

    public static List<RegistryReplicationTask> toFailedTasks(List<RegistryReplicationTask> tasks, TaskErrorCode errorCode) {
        List<RegistryReplicationTask> failedTasks = new ArrayList<>();
        for (RegistryReplicationTask task : tasks) {
            failedTasks.add(toFailedTask(task, errorCode));
        }

        return failedTasks;
    }

    public static List<RegistryReplicationTask> replicate(Class<?> clazz, String serviceUrl, List<Instance> instances,
            Map<Instance, Long> instanceExpiryTimeMap) {
        List<RegistryReplicationTask> failedTasks = new ArrayList<>();
        if (CollectionValues.isNullOrEmpty(instances))
            return failedTasks;

        RegistryReplicationRequestExecutor requestExecutor = _requestExecutors.get(clazz);
        RegistryReplicationTaskGenerator taskGenerator = _taskGenerators.get(clazz);

        if (shouldReplicateToAllPeerNodes(serviceUrl)) {
            Map<String, List<FailedInstance>> urlFailedInstancesMap = replicate(instances, requestExecutor);
            for (String serviceUrl2 : urlFailedInstancesMap.keySet()) {
                List<FailedInstance> failedInstances = urlFailedInstancesMap.get(serviceUrl2);
                failedTasks.addAll(toReplicationTasks(serviceUrl2, failedInstances, taskGenerator, instanceExpiryTimeMap));
            }

            return failedTasks;
        }

        List<FailedInstance> failedInstances = replicate(instances, requestExecutor, serviceUrl);
        if (CollectionValues.isNullOrEmpty(failedInstances))
            return failedTasks;

        failedTasks.addAll(toReplicationTasks(serviceUrl, failedInstances, taskGenerator, instanceExpiryTimeMap));
        return failedTasks;
    }

    public static Map<String, List<FailedInstance>> replicate(List<Instance> instances, RegistryReplicationRequestExecutor executor) {
        Map<String, List<FailedInstance>> urlFailedInstancesMap = new HashMap<>();
        for (ServiceNode node : ClusterManager.INSTANCE.otherNodes()) {
            ServiceNodeStatus nodeStatus = ClusterManager.INSTANCE.getNodeStatus(node);
            if (!ServiceNodeUtil.canServiceRegistry(nodeStatus)) {
                _logger.warn("Node {} is not available for replication. Status: {}", node, nodeStatus);
                continue;
            }

            List<FailedInstance> failedInstances = replicate(instances, executor, node.getUrl());
            urlFailedInstancesMap.put(node.getUrl(), failedInstances);
        }

        return urlFailedInstancesMap;
    }

    public static List<FailedInstance> replicate(List<Instance> instances, RegistryReplicationRequestExecutor executor, String serviceUrl) {
        try {
            RegistryReplicationServiceClient client = new RegistryReplicationServiceClient(serviceUrl);
            HasFailedInstances response = executor.execute(client, instances);
            if (ResponseStatusUtil.isSuccess(response.getResponseStatus()))
                return new ArrayList<>();

            if (ResponseStatusUtil.isFail(response.getResponseStatus()))
                return toFailedInstances(instances, response.getResponseStatus().getErrorCode(), null);

            List<FailedInstance> failedInstances = response.getFailedInstances();
            if (failedInstances == null)
                failedInstances = new ArrayList<>();
            return failedInstances;
        } catch (Throwable ex) {
            String errorMessage = "Replication to service " + serviceUrl + " failed.";
            if (HttpClientUtil.isRemoteHostUnavailable(ex) || HttpClientUtil.isSocketTimeout(ex))
                _logger.warn(errorMessage, ex);
            else
                _logger.error(errorMessage, ex);

            return toFailedInstances(instances, ex.getMessage());
        }
    }

    private static List<FailedInstance> toFailedInstances(List<Instance> instances, String errorMessage) {
        return toFailedInstances(instances, ErrorCodes.UNKNOWN, errorMessage);
    }

    private static List<FailedInstance> toFailedInstances(List<Instance> instances, String errorCode, String errorMessage) {
        List<FailedInstance> failedInstances = new ArrayList<>();
        for (Instance instance : instances) {
            failedInstances.add(new FailedInstance(instance, errorCode, errorMessage));
        }

        return failedInstances;
    }

    private static List<RegistryReplicationTask> toReplicationTasks(String serviceUrl, List<FailedInstance> failedInstances,
            RegistryReplicationTaskGenerator taskGenerator, Map<Instance, Long> instanceExpiryTimeMap) {
        List<RegistryReplicationTask> failedTasks = new ArrayList<>();
        EventMetric metric = ArtemisMetricManagers.DEFAULT.eventMetricManager().getMetric(FAILED_INSTANCE_STATUS_CODE_EVENT_TYPE + serviceUrl,
                new MetricConfig(ImmutableMap.of(MetricNames.METRIC_NAME_KEY_DISTRIBUTION, FAILED_INSTANCE_STATUS_CODE_EVENT_TYPE, "service_url", serviceUrl)));
        for (FailedInstance failedInstance : failedInstances) {
            ArtemisTraceExecutor.INSTANCE.markEvent(FAILED_INSTANCE_STATUS_CODE_EVENT_TYPE, failedInstance.getErrorCode());
            metric.addEvent(failedInstance.getErrorCode());
            TaskErrorCode errorCode = responseErrorCodeToTaskErrorCode(failedInstance.getErrorCode());
            Instance instance = failedInstance.getInstance();
            RegistryReplicationTask task = taskGenerator.create(instance, serviceUrl, instanceExpiryTimeMap.get(instance), errorCode);
            failedTasks.add(task);
        }

        return failedTasks;
    }

}
