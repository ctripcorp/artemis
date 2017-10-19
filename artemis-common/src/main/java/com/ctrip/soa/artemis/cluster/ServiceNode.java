package com.ctrip.soa.artemis.cluster;

import com.ctrip.soa.artemis.Zone;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.google.common.base.Objects;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class ServiceNode {

    private Zone _zone;
    private String _url;

    public ServiceNode() {

    }

    public ServiceNode(Zone zone, String url) {
        _zone = zone;
        _url = url;
    }

    public Zone getZone() {
        return _zone;
    }

    public void setZone(Zone zone) {
        _zone = zone;
    }

    public String getUrl() {
        return _url;
    }

    public void setUrl(String url) {
        _url = url;
    }

    @Override
    public String toString() {
        return StringValues.toLowerCase(_zone.getRegionId() + "/" + _zone.getZoneId() + "/" + _url);
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
