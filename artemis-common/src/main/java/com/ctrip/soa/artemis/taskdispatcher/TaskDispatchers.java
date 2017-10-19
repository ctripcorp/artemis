package com.ctrip.soa.artemis.taskdispatcher;

import java.util.List;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class TaskDispatchers {

    public static <T extends Task, W> TaskDispatcher<T> newSingleItemTaskDispatcher(String dispatcherId,
            TaskProcessor<T, T> taskProcessor) {
        return new DefaultTaskDispatcher<>(dispatcherId, new SingleItemTaskAcceptor<T>(dispatcherId),
                taskProcessor);
    }

    public static <T extends Task> TaskDispatcher<T> newBatchingTaskDispatcher(String dispatcherId,
            TaskProcessor<T, List<T>> taskProcessor) {
        return new DefaultTaskDispatcher<>(dispatcherId, new BatchingTaskAcceptor<T>(dispatcherId), taskProcessor);
    }

}
