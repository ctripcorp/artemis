package com.ctrip.soa.artemis;

import com.google.common.base.Objects;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class InstanceKey {

    public static final InstanceKey EMPTY = new InstanceKey(null, null, null) {
        @Override
        public void setRegionId(String regionId) {
            throw new IllegalStateException("EMPTY InstanceKey cannot be modified.");
        }

        @Override
        public void setServiceId(String serviceId) {
            throw new IllegalStateException("EMPTY InstanceKey cannot be modified.");
        }

        @Override
        public void setInstanceId(String instanceId) {
            throw new IllegalStateException("EMPTY InstanceKey cannot be modified.");
        }
    };

    public static InstanceKey of(Instance instance) {
        if (instance == null)
            return EMPTY;

        return new InstanceKey(instance.getRegionId(), instance.getServiceId(), instance.getInstanceId());
    }

    private String regionId;
    private String serviceId;
    private String instanceId;

    public InstanceKey() {

    }

    public InstanceKey(String regionId, String serviceId, String instanceId) {
        this.regionId = regionId;
        this.serviceId = serviceId;
        this.instanceId = instanceId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public String toString() {
        return String.format("%s.%s.%s", regionId, serviceId, instanceId).toLowerCase();
    };

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other == null)
            return false;

        if (other.getClass() != this.getClass())
            return false;

        return Objects.equal(toString(), other.toString());
    }

}
