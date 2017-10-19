package com.ctrip.soa.artemis.management.instance;

import com.ctrip.soa.artemis.Instance;

/**
 * Created by fang_j on 10/07/2016.
 */
public class IsInstanceDownRequest {

    private Instance instance;

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

}
