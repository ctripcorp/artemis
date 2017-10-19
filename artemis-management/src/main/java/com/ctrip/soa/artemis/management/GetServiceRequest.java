package com.ctrip.soa.artemis.management;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetServiceRequest {

    private String serviceId;

    private String zoneId;

    private String regionId;

    public GetServiceRequest() {

    }

    public GetServiceRequest(String serviceId, String regionId, String zoneId) {
        this.serviceId = serviceId;
        this.zoneId = regionId;
        this.regionId = zoneId;
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

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
}
