package com.ctrip.soa.artemis.replication;

import com.ctrip.soa.artemis.taskdispatcher.Task;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface ReplicationTask extends Task {

    boolean batchingEnabled();

    String serviceUrl();

}
