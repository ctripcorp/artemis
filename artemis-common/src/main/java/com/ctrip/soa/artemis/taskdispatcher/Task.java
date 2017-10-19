package com.ctrip.soa.artemis.taskdispatcher;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface Task {

    String taskId();

    long submitTime();

    long expiryTime();

    TaskErrorCode errorCode();

    void resetSubmitTime(long submitTime);

}
