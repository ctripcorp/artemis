package com.ctrip.soa.artemis;

import java.util.Map;

import com.ctrip.soa.caravan.common.value.StringValues;
import com.google.common.base.Objects;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class Zone {

    private String _regionId;
    private String _zoneId;
    private Map<String, String> _metadata;

    public Zone() {

    }

    public Zone(String regionId, String zoneId) {
        this(regionId, zoneId, null);
    }

    public Zone(String regionId, String zoneId, Map<String, String> metadata) {
        _regionId = regionId;
        _zoneId = zoneId;
        _metadata = metadata;
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

    public Map<String, String> getMetadata() {
        return _metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        _metadata = metadata;
    }

    @Override
    public String toString() {
        return StringValues.toLowerCase(_regionId + "/" + _zoneId);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (other.getClass() != this.getClass())
            return false;

        return Objects.equal(toString(), other.toString());
    }

}
