package com.ctrip.soa.artemis.management.zone;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetZoneOperationsListRequest {
    private String regionId;
    private String serviceId;
    private String zoneId;

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
}
