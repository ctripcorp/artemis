package com.ctrip.soa.artemis.registry.replication;

import com.ctrip.soa.artemis.replication.ReplicationManager;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class RegistryReplicationManager extends ReplicationManager<RegistryReplicationTask> {

    public static final RegistryReplicationManager INSTANCE = new RegistryReplicationManager();

    private RegistryReplicationManager() {
        super("artemis.service.registry.replication", new RegistrySingleItemTaskProcessor(),
                new RegistryBatchingTaskProcessor());
    }

}
