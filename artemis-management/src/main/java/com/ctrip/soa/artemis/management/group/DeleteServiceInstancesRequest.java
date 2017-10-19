package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.management.common.OperationContext;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class DeleteServiceInstancesRequest extends OperationContext {
    private List<Long> serviceInstanceIds;

    public List<Long> getServiceInstanceIds() {
        return serviceInstanceIds;
    }

    public void setServiceInstanceIds(List<Long> serviceInstanceIds) {
        this.serviceInstanceIds = serviceInstanceIds;
    }

    @Override
    public String toString() {
        return "DeleteServiceInstancesRequest{" +
                "serviceInstanceIds=" + serviceInstanceIds +
                "} " + super.toString();
    }
}
