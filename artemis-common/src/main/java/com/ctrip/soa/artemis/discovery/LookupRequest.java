package com.ctrip.soa.artemis.discovery;

import java.util.List;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class LookupRequest {

    private String _regionId;

    private String _zoneId;

    private List<DiscoveryConfig> _discoveryConfigs;

    public LookupRequest() {

    }

    public LookupRequest(List<DiscoveryConfig> discoveryConfigs, String regionId, String zoneId) {
        _discoveryConfigs = discoveryConfigs;
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

    public List<DiscoveryConfig> getDiscoveryConfigs() {
        return _discoveryConfigs;
    }

    public void setDiscoveryConfigs(List<DiscoveryConfig> discoveryConfigs) {
        _discoveryConfigs = discoveryConfigs;
    }

}
