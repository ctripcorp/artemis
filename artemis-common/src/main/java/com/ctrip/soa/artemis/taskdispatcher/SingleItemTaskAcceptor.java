package com.ctrip.soa.artemis.taskdispatcher;

import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;

class SingleItemTaskAcceptor<T extends Task> extends TaskAcceptor<T, T> {

    public SingleItemTaskAcceptor(String dispatcherId) {
        super(dispatcherId);
    }

    @Override
    protected int getWorkSize(T w) {
        return 1;
    }

    @Override
    protected boolean isEmptyWork(T work) {
        return work == null;
    }

    @Override
    protected T filterExpiredTask(T work) {
        if (isExpiredTask(work)) {
            ArtemisTraceExecutor.INSTANCE.markEvent(_taskStatusEventType, "expired");
            _taskStatusEventMetric.addEvent("expired");
            return null;
        }

        ArtemisTraceExecutor.INSTANCE.markEvent(_taskStatusEventType, "normal");
        _taskStatusEventMetric.addEvent("normal");
        return work;
    }

    @Override
    protected void assignWork() {
        while (!_processingOrder.isEmpty()) {
            String taskId = _processingOrder.poll();
            T task = _acceptedTasks.remove(taskId);

            if (isBufferFull()) {
                _workQueue.poll();
                _pendingTaskCount.decrementAndGet();
                ArtemisTraceExecutor.INSTANCE.markEvent(_workStatusEventType, "buffer-full-dropped");
                _workStatusEventMetric.addEvent("buffer-full-dropped");
            }

            _workQueue.add(task);
            _pendingTaskCount.incrementAndGet();
            ArtemisTraceExecutor.INSTANCE.markEvent(_workStatusEventType, "normal");
            _workStatusEventMetric.addEvent("normal");
        }
    }

}