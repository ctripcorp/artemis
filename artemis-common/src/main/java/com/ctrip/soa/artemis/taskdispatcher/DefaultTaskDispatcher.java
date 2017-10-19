package com.ctrip.soa.artemis.taskdispatcher;

class DefaultTaskDispatcher<T extends Task, W> implements TaskDispatcher<T> {

    private TaskAcceptor<T, W> _acceptorExecutor;
    private TaskExecutor<T, W> _taskExecutor;

    public DefaultTaskDispatcher(String dispatcherId, TaskAcceptor<T, W> taskAccetor,
            TaskProcessor<T, W> taskProcessor) {
        _acceptorExecutor = taskAccetor;
        _taskExecutor = new TaskExecutor<T, W>(dispatcherId, taskAccetor, taskProcessor);
    }

    @Override
    public void process(T task) {
        _acceptorExecutor.accept(task);
    }

    @Override
    public void shutdown() {
        _acceptorExecutor.shutdown();
        _taskExecutor.shutdown();
    }

}
