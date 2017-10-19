package com.ctrip.soa.artemis.management.zone;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ZoneOperations {
    private ZoneKey zoneKey;
    private List<String> operations;

    public ZoneOperations() {
    }

    public ZoneOperations(ZoneKey zoneKey, List<String> operations) {
        this.zoneKey = zoneKey;
        this.operations = operations;
    }

    public ZoneKey getZoneKey() {
        return zoneKey;
    }

    public void setZoneKey(ZoneKey zoneKey) {
        this.zoneKey = zoneKey;
    }

    public List<String> getOperations() {
        return operations;
    }

    public void setOperations(List<String> operations) {
        this.operations = operations;
    }
}
