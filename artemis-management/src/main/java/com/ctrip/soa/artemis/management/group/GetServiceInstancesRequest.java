package com.ctrip.soa.artemis.management.group;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetServiceInstancesRequest {
    private String serviceId;
    private String instanceId;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String toString() {
        return "GetServiceInstancesRequest{" +
                "serviceId='" + serviceId + '\'' +
                ", instanceId='" + instanceId + '\'' +
                "} " + super.toString();
    }
}
