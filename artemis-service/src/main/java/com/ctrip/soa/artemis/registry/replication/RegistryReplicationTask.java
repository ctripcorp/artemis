package com.ctrip.soa.artemis.registry.replication;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.InstanceKey;
import com.ctrip.soa.artemis.replication.AbstractReplicationTask;
import com.ctrip.soa.artemis.taskdispatcher.TaskErrorCode;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public abstract class RegistryReplicationTask extends AbstractReplicationTask {

    private String _taskId;
    private Instance _instance;

    public RegistryReplicationTask(Instance instance, long expiryTime) {
        this(instance, null, expiryTime, null);
    }

    public RegistryReplicationTask(Instance instance, String serviceUrl, long expiryTime, TaskErrorCode errorCode) {
        super(serviceUrl, expiryTime, errorCode);
        NullArgumentChecker.DEFAULT.check(instance, "instance");
        _instance = instance;
        _taskId = getClass().getSimpleName() + ":" + InstanceKey.of(instance) + ":" + serviceUrl;
    }

    public Instance instance() {
        return _instance;
    }

    @Override
    public String taskId() {
        return _taskId;
    }

}
