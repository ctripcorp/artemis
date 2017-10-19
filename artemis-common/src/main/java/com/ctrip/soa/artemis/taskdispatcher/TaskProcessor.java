package com.ctrip.soa.artemis.taskdispatcher;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface TaskProcessor<T extends Task, W> {

    ProcessingResult<T> process(W work);

}
