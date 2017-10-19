package com.ctrip.soa.artemis.management.zone;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetAllZoneOperationsRequest {

    private String regionId;

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    @Override
    public String toString() {
        return "GetAllZoneOperationsRequest{" +
                "regionId='" + regionId + '\'' +
                '}';
    }
}
