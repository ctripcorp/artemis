package com.ctrip.soa.artemis.taskdispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ctrip.soa.caravan.common.metric.MetricConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.metric.ArtemisMetricManagers;
import com.ctrip.soa.artemis.metric.MetricNames;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.common.value.checker.StringArgumentChecker;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import com.google.common.collect.ImmutableMap;
import com.ctrip.soa.caravan.common.defensive.Loops;
import com.ctrip.soa.caravan.common.delegate.Action;
import com.ctrip.soa.caravan.common.metric.EventMetric;

class TaskExecutor<T extends Task, W> {

    private static final String IDENTITY_FORMAT = ".task-executor";

    private static final Logger _logger = LoggerFactory.getLogger(TaskExecutor.class);

    private String _executorId;

    private TypedProperty<Integer> _threadCountProperty;
    private List<Thread> _workerThreads = new ArrayList<>();

    protected AtomicBoolean _isShutdown = new AtomicBoolean();

    private TaskAcceptor<T, W> _taskAcceptor;
    private TaskProcessor<T, W> _taskProcessor;

    private String _executeTraceKey;
    private String _executeFailedTaskEventType;
    private EventMetric _executeFailedTaskEventMetric;

    public TaskExecutor(String dispatcherId, TaskAcceptor<T, W> taskAcceptor, TaskProcessor<T, W> taskProcessor) {
        StringArgumentChecker.DEFAULT.check(dispatcherId, "dispatcherId");
        NullArgumentChecker.DEFAULT.check(taskAcceptor, "acceptorExecutor");
        NullArgumentChecker.DEFAULT.check(taskProcessor, "taskProcessor");

        _executorId = dispatcherId + IDENTITY_FORMAT;
        _taskAcceptor = taskAcceptor;
        _taskProcessor = taskProcessor;

        _executeTraceKey = _executorId + ".execute";
        _executeFailedTaskEventType = _executeTraceKey + ".failed-task";
        _executeFailedTaskEventMetric = ArtemisMetricManagers.DEFAULT.eventMetricManager().getMetric(_executeFailedTaskEventType,
               new MetricConfig(ImmutableMap.of(MetricNames.METRIC_NAME_KEY_DISTRIBUTION, _executeFailedTaskEventType)));

        _threadCountProperty = ArtemisConfig.properties().getIntProperty(_executorId + ".thread-count", 20, 1, 100);

        Runnable executiontask = new Runnable() {
            @Override
            public void run() {
                loopExecute();
            }
        };

        for (int i = 0; i < _threadCountProperty.typedValue().intValue(); i++) {
            Thread workerThread = new Thread(executiontask);
            _workerThreads.add(workerThread);
            workerThread.setDaemon(true);
            workerThread.start();
        }
    }

    public void shutdown() {
        if (!_isShutdown.compareAndSet(false, true))
            return;

        for (Thread workerThread : _workerThreads)
            workerThread.interrupt();
    }

    private void loopExecute() {
        while (true) {
            if (_isShutdown.get())
                break;

            Loops.executeWithoutTightLoop(new Action() {
                @Override
                public void execute() {
                    try {
                        final W work = _taskAcceptor.pollWork();
                        ArtemisTraceExecutor.INSTANCE.execute(_executeTraceKey, new Action() {
                            @Override
                            public void execute() {
                                TaskExecutor.this.execute(work);
                            }
                        });
                    } catch (Throwable ex) {
                        _logger.error("Execute task error.", ex);
                    }
                }
            });
        }
    }

    private void execute(W work) {
        ProcessingResult<T> result = _taskProcessor.process(work);
        if (CollectionValues.isNullOrEmpty(result.failedTasks()))
            return;

        for (T task : result.failedTasks()) {
            String taskErrorCode = task.errorCode().toString();
            ArtemisTraceExecutor.INSTANCE.markEvent(_executeFailedTaskEventType, taskErrorCode);
            _executeFailedTaskEventMetric.addEvent(taskErrorCode);

            if (TaskErrorCode.RERUNNABLE_ERROR_CODES.contains(task.errorCode())) {
                _taskAcceptor.reaccept(task);
                return;
            }

            _logger.warn("Discarding a task due to non-rerunnable error. Task ErrorCode: {}", task.errorCode());
        }
    }

}
