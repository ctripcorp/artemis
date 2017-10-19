package com.ctrip.soa.artemis.registry.replication;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.taskdispatcher.TaskErrorCode;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class HeartbeatTask extends RegistryReplicationTask {

    private static TypedProperty<Boolean> _batchingEnabledPropery = ArtemisConfig.properties()
            .getBooleanProperty("artemis.service.registry.heartbeat.replication.batching-enabled", true);

    private static TypedProperty<Integer> _taskTtlProperty = ArtemisConfig.properties()
            .getIntProperty("artemis.service.registry.heartbeat.replication.task-ttl", 5 * 1000, 2000, 10 * 1000);

    public HeartbeatTask(Instance instance) {
        super(instance, System.currentTimeMillis() + _taskTtlProperty.typedValue().intValue());
    }

    public HeartbeatTask(Instance instance, String serviceUrl, long expiryTime, TaskErrorCode errorCode) {
        super(instance, serviceUrl, expiryTime, errorCode);
    }

    @Override
    public boolean batchingEnabled() {
        return _batchingEnabledPropery.typedValue().booleanValue();
    }

}
