package com.ctrip.soa.artemis.registry;

import java.util.List;

import com.ctrip.soa.artemis.Instance;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface HasInstances {

    List<Instance> getInstances();

}
