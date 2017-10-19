package com.ctrip.soa.artemis.registry;

import java.util.List;

import com.ctrip.soa.artemis.Instance;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class RegisterRequest implements HasInstances {

    private List<Instance> _instances;

    public RegisterRequest() {

    }

    public RegisterRequest(List<Instance> instances) {
        _instances = instances;
    }

    @Override
    public List<Instance> getInstances() {
        return _instances;
    }

    public void setInstances(List<Instance> instances) {
        _instances = instances;
    }

}
