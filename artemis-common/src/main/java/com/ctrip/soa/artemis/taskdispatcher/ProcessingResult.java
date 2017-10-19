package com.ctrip.soa.artemis.taskdispatcher;

import java.util.List;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class ProcessingResult<T extends Task> {

    private List<T> _failedTasks;

    public ProcessingResult(List<T> failedTasks) {
        _failedTasks = failedTasks;
    }

    public List<T> failedTasks() {
        return _failedTasks;
    }

}
