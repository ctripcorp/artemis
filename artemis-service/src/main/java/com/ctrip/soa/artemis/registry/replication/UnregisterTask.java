package com.ctrip.soa.artemis.registry.replication;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.taskdispatcher.TaskErrorCode;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class UnregisterTask extends RegistryReplicationTask {

    private static TypedProperty<Boolean> _batchingEnabledPropery = ArtemisConfig.properties()
            .getBooleanProperty("artemis.service.registry.unregister.replication.batching-enabled", false);

    private static TypedProperty<Integer> _taskTtlProperty = ArtemisConfig.properties()
            .getIntProperty("artemis.service.registry.unregister.replication.task-ttl", 5000, 2000, 30 * 1000);

    public UnregisterTask(Instance instance) {
        super(instance, System.currentTimeMillis() + _taskTtlProperty.typedValue().intValue());
    }

    public UnregisterTask(Instance instance, String serviceUrl, long expiryTime, TaskErrorCode errorCode) {
        super(instance, serviceUrl, expiryTime, errorCode);
    }

    @Override
    public boolean batchingEnabled() {
        return _batchingEnabledPropery.typedValue().booleanValue();
    }

}
