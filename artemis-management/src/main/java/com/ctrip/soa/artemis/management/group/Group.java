package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.util.ServiceGroupKeys;
import java.util.Map;

/**
 * Created by fang_j on 10/07/2016.
 */
public class Group {
    private Long groupId;
    private String serviceId;
    private String regionId;
    private String zoneId;
    private String name;
    private String appId;
    private String description;
    private String status;
    private Map<String, String> metadata;

    public Group() {
    }

    public Group(Long groupId, String serviceId, String regionId, String zoneId, String name, String appId, String description, String status, Map<String, String> metadata) {
        this.groupId = groupId;
        this.serviceId = serviceId;
        this.regionId = regionId;
        this.zoneId = zoneId;
        this.name = name;
        this.appId = appId;
        this.description = description;
        this.status = status;
        this.metadata = metadata;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getGroupKey() {
        return ServiceGroupKeys.of(serviceId, regionId, zoneId, name).getGroupKey();
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", serviceId='" + serviceId + '\'' +
                ", regionId='" + regionId + '\'' +
                ", zoneId='" + zoneId + '\'' +
                ", name='" + name + '\'' +
                ", appId='" + appId + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
