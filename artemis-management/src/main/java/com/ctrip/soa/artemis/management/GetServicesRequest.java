package com.ctrip.soa.artemis.management;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetServicesRequest {

    private String _regionId;

    private String _zoneId;

    public GetServicesRequest() {

    }

    public GetServicesRequest(String regionId, String zoneId) {
        _regionId = regionId;
        _zoneId = zoneId;
    }

    public String getRegionId() {
        return _regionId;
    }

    public void setRegionId(String regionId) {
        _regionId = regionId;
    }

    public String getZoneId() {
        return _zoneId;
    }

    public void setZoneId(String zoneId) {
        _zoneId = zoneId;
    }

}
