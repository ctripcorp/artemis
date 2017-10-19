package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.management.common.OperationContext;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class InsertServiceInstancesRequest extends OperationContext {
    private List<ServiceInstance> serviceInstances;

    public List<ServiceInstance> getServiceInstances() {
        return serviceInstances;
    }

    public void setServiceInstances(List<ServiceInstance> serviceInstances) {
        this.serviceInstances = serviceInstances;
    }
}
