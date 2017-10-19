package com.ctrip.soa.artemis.discovery;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class GetServiceRequest {

    private DiscoveryConfig _discoveryConfig;

    private String _regionId;

    private String _zoneId;

    public GetServiceRequest() {

    }

    public GetServiceRequest(DiscoveryConfig discoveryConfig, String regionId, String zoneId) {
        _discoveryConfig = discoveryConfig;
        _regionId = regionId;
        _zoneId = zoneId;
    }

    public DiscoveryConfig getDiscoveryConfig() {
        return _discoveryConfig;
    }

    public void setDiscoveryConfig(DiscoveryConfig discoveryConfig) {
        _discoveryConfig = discoveryConfig;
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
