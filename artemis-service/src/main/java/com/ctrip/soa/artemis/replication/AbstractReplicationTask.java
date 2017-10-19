package com.ctrip.soa.artemis.replication;

import com.ctrip.soa.artemis.replication.ReplicationTask;
import com.ctrip.soa.artemis.taskdispatcher.TaskErrorCode;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public abstract class AbstractReplicationTask implements ReplicationTask {

    private String _serviceUrl;
    private long _submitTime;
    private long _expiryTime;
    private TaskErrorCode _errorCode;

    public AbstractReplicationTask(String serviceUrl, long expiryTime, TaskErrorCode errorCode) {
        _serviceUrl = serviceUrl;
        _submitTime = System.currentTimeMillis();
        _expiryTime = expiryTime;
        _errorCode = errorCode;
    }

    @Override
    public long submitTime() {
        return _submitTime;
    }

    @Override
    public long expiryTime() {
        return _expiryTime;
    }

    @Override
    public String serviceUrl() {
        return _serviceUrl;
    }

    @Override
    public TaskErrorCode errorCode() {
        return _errorCode;
    }

    @Override
    public void resetSubmitTime(long submitTime) {
        if (submitTime <= 0)
            return;

        _submitTime = submitTime;
    }

}
