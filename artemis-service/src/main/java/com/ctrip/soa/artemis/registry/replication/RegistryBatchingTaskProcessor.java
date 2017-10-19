package com.ctrip.soa.artemis.registry.replication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.taskdispatcher.ProcessingResult;
import com.ctrip.soa.artemis.taskdispatcher.TaskErrorCode;
import com.ctrip.soa.artemis.taskdispatcher.TaskProcessor;
import com.ctrip.soa.caravan.common.value.StringValues;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class RegistryBatchingTaskProcessor implements TaskProcessor<RegistryReplicationTask, List<RegistryReplicationTask>> {

    private static final Logger _logger = LoggerFactory.getLogger(RegistryBatchingTaskProcessor.class);

    @Override
    public ProcessingResult<RegistryReplicationTask> process(List<RegistryReplicationTask> work) {
        try {
            Map<Class<?>, Map<Instance, Long>> subTaskTypeInstanceExpiryTimeMap = new HashMap<>();
            Map<Class<?>, Map<String, List<Instance>>> subTaskTypeServiceUrlInstancesMap = new HashMap<>();
            for (RegistryReplicationTask task : work) {
                for (Class<?> clazz : RegistryReplicationTool.supportedSubTaskTypes()) {
                    Map<Instance, Long> instanceExpiryTimeMap = subTaskTypeInstanceExpiryTimeMap.get(clazz);
                    if (instanceExpiryTimeMap == null) {
                        instanceExpiryTimeMap = new HashMap<>();
                        subTaskTypeInstanceExpiryTimeMap.put(clazz, instanceExpiryTimeMap);
                    }

                    instanceExpiryTimeMap.put(task.instance(), task.expiryTime());

                    Map<String, List<Instance>> urlInstancesMap = subTaskTypeServiceUrlInstancesMap.get(clazz);
                    if (urlInstancesMap == null) {
                        urlInstancesMap = new HashMap<>();
                        subTaskTypeServiceUrlInstancesMap.put(clazz, urlInstancesMap);
                    }

                    if (task.getClass() == clazz) {
                        String serviceUrl = task.serviceUrl() == null ? StringValues.EMPTY : task.serviceUrl();
                        List<Instance> instances = urlInstancesMap.get(serviceUrl);
                        if (instances == null) {
                            instances = new ArrayList<>();
                            urlInstancesMap.put(serviceUrl, instances);
                        }

                        instances.add(task.instance());
                    }
                }
            }

            List<RegistryReplicationTask> totalFailedTasks = new ArrayList<>();
            for (Class<?> clazz : RegistryReplicationTool.supportedSubTaskTypes()) {
                Map<String, List<Instance>> serviceUrlInstancesMap = subTaskTypeServiceUrlInstancesMap.get(clazz);
                if (serviceUrlInstancesMap == null)
                    continue;

                for (String serviceUrl : serviceUrlInstancesMap.keySet()) {
                    List<Instance> instances = serviceUrlInstancesMap.get(serviceUrl);
                    Map<Instance, Long> instanceExpiryTimeMap = subTaskTypeInstanceExpiryTimeMap.get(clazz);
                    List<RegistryReplicationTask> failedTasks = RegistryReplicationTool.replicate(clazz, serviceUrl, instances, instanceExpiryTimeMap);
                    totalFailedTasks.addAll(failedTasks);
                }
            }

            return new ProcessingResult<>(totalFailedTasks);

        } catch (Throwable ex) {
            _logger.error("Batch Replication failed. Maybe a bug.", ex);
            return new ProcessingResult<>(RegistryReplicationTool.toFailedTasks(work, TaskErrorCode.PermanentFail));
        }
    }

}