package com.ctrip.soa.artemis.management.zone;

import com.ctrip.soa.artemis.Instance;
import com.google.common.base.Objects;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ZoneKey {
    public static final ZoneKey EMPTY = new ZoneKey(null, null, null) {
        @Override
        public void setRegionId(String regionId) {
            throw new IllegalStateException("EMPTY ZoneKey cannot be modified.");
        }

        @Override
        public void setServiceId(String serviceId) {
            throw new IllegalStateException("EMPTY ZoneKey cannot be modified.");
        }

        @Override
        public void setZoneId(String serviceId) {
            throw new IllegalStateException("EMPTY ZoneKey cannot be modified.");
        }
    };

    public static ZoneKey of(Instance instance) {
        if (instance == null)
            return EMPTY;

        return new ZoneKey(instance.getRegionId(), instance.getServiceId(), instance.getZoneId());
    }

    private String regionId;
    private String serviceId;
    private String zoneId;

    public ZoneKey() {
    }

    public ZoneKey(String regionId, String serviceId, String zoneId) {
        this.regionId = regionId;
        this.serviceId = serviceId;
        this.zoneId = zoneId;
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

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public String toString() {
        return String.format("%s.%s.%s", regionId, serviceId, zoneId).toLowerCase();
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
