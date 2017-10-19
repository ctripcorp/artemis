package com.ctrip.soa.artemis.taskdispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.metric.ArtemisMetricManagers;
import com.ctrip.soa.artemis.metric.MetricNames;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.caravan.common.metric.AuditMetric;
import com.ctrip.soa.caravan.common.metric.MetricConfig;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import com.google.common.collect.ImmutableMap;

class BatchingTaskAcceptor<T extends Task> extends TaskAcceptor<T, List<T>> {

    private TypedProperty<Integer> _maxBatchingSizeProperty;
    private TypedProperty<Integer> _maxBatchingDelayProperty;
    private final AuditMetric _batchingSizeAuditMetric;
    private AtomicInteger _pendingTaskCount = new AtomicInteger();

    public BatchingTaskAcceptor(String dispatcherId) {
        super(dispatcherId);

        _maxBatchingSizeProperty = ArtemisConfig.properties().getIntProperty(_acceptorId + ".max-batching-size", 250, 10, 10000);
        _maxBatchingDelayProperty = ArtemisConfig.properties().getIntProperty(_acceptorId + ".max-batching-delay", 2 * 1000, 1000, 10 * 1000);

        final String batchingSizeMetricName = _acceptorId + ".batching-size.distribution";
        _batchingSizeAuditMetric = ArtemisMetricManagers.DEFAULT.valueMetricManager().getMetric(batchingSizeMetricName,
                new MetricConfig(ImmutableMap.of(MetricNames.METRIC_NAME_KEY_DISTRIBUTION,  batchingSizeMetricName)));
    }

    @Override
    protected boolean isEmptyWork(List<T> work) {
        return CollectionValues.isNullOrEmpty(work);
    }

    @Override
    protected List<T> filterExpiredTask(List<T> work) {
        List<T> result = new ArrayList<>();
        for (T task : work) {
            if (isExpiredTask(task)) {
                ArtemisTraceExecutor.INSTANCE.markEvent(_taskStatusEventType, "expired");
                _taskStatusEventMetric.addEvent("expired");
                continue;
            }

            result.add(task);
            ArtemisTraceExecutor.INSTANCE.markEvent(_taskStatusEventType, "normal");
            _taskStatusEventMetric.addEvent("normal");
        }

        _batchingSizeAuditMetric.addValue(result.size());

        return result;
    }

    @Override
    protected int getWorkSize(List<T> w) {
        return w.size();
    }

    @Override
    protected void assignWork() {
        while (hasEnoughTasks()) {
            if (isBufferFull()) {
                List<T> tasks = _workQueue.poll();
                _pendingTaskCount.addAndGet(-tasks.size());
                ArtemisTraceExecutor.INSTANCE.markEvent(_workStatusEventType, "buffer-full-dropped");
                _workStatusEventMetric.addEvent("buffer-full-dropped");
            }

            List<T> work = generateWork();
            _workQueue.add(work);
            _pendingTaskCount.addAndGet(work.size());
            ArtemisTraceExecutor.INSTANCE.markEvent(_workStatusEventType, "normal");
            _workStatusEventMetric.addEvent("normal");
        }
    }

    private boolean hasEnoughTasks() {
        if (_processingOrder.size() >= _maxBatchingSizeProperty.typedValue().intValue())
            return true;

        if (_processingOrder.isEmpty())
            return false;

        String taskId = _processingOrder.peek();
        T task = _acceptedTasks.get(taskId);

        long delay = System.currentTimeMillis() - task.submitTime();
        return delay >= _maxBatchingDelayProperty.typedValue().intValue();
    }

    private List<T> generateWork() {
        List<T> tasks = new ArrayList<>();
        while (tasks.size() < _maxBatchingSizeProperty.typedValue().intValue()) {
            if (_processingOrder.isEmpty())
                break;

            String taskId = _processingOrder.poll();
            T task = _acceptedTasks.remove(taskId);
            tasks.add(task);
        }

        return tasks;
    }

}
