package com.ctrip.soa.artemis.registry;

import java.util.List;

import com.ctrip.soa.artemis.HasResponseStatus;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface HasFailedInstances extends HasResponseStatus {

    List<FailedInstance> getFailedInstances();

}
