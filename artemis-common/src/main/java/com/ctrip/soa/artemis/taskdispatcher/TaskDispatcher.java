package com.ctrip.soa.artemis.taskdispatcher;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface TaskDispatcher<T extends Task> {

    void process(T task);

    void shutdown();

}