package com.ctrip.soa.artemis.management.group;

/**
 * Created by fang_j 10/07/2016.
 */
public class GetGroupsRequest {
    private Long groupId;
    private String serviceId;
    private String regionId;
    private String zoneId;
    private String name;
    private String appId;
    private String status;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GetGroupsRequest{" +
                "groupId=" + groupId +
                ", serviceId='" + serviceId + '\'' +
                ", regionId='" + regionId + '\'' +
                ", zoneId='" + zoneId + '\'' +
                ", name='" + name + '\'' +
                ", appId='" + appId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
