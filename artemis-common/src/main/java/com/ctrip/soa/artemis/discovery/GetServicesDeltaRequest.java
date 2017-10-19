package com.ctrip.soa.artemis.discovery;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class GetServicesDeltaRequest {

    private String _regionId;

    private String _zoneId;

    private long _version;

    public GetServicesDeltaRequest() {

    }

    public GetServicesDeltaRequest(String regionId, String zoneId, long version) {
        _regionId = regionId;
        _zoneId = zoneId;
        _version = version;
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

    public long getVersion() {
        return _version;
    }

    public void setVersion(long version) {
        _version = version;
    }

}
