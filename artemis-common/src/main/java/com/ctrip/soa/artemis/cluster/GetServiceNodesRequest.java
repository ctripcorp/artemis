package com.ctrip.soa.artemis.cluster;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class GetServiceNodesRequest {

    private String _regionId;

    private String _zoneId;

    public GetServiceNodesRequest() {

    }

    public GetServiceNodesRequest(String regionId, String zoneId) {
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
