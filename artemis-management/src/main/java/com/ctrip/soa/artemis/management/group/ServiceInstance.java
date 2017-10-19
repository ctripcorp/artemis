package com.ctrip.soa.artemis.management.group;

import java.util.Map;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServiceInstance {
    private Long id;
    private String serviceId;
    private String instanceId;
    private String groupId;
    private String ip;
    private String machineName;
    private Map<String, String> metadata;
    private int port;
    private String protocol;
    private String regionId;
    private String zoneId;
    private String healthCheckUrl;
    private String url;
    private String description;

    public ServiceInstance() {
    }

    public ServiceInstance(Long id, String serviceId, String instanceId, String ip, String machineName, Map<String, String> metadata, int port, String protocol, String regionId, String zoneId, String healthCheckUrl, String url, String description, String groupId) {
        this.id = id;
        this.serviceId = serviceId;
        this.instanceId = instanceId;
        this.ip = ip;
        this.machineName = machineName;
        this.metadata = metadata;
        this.port = port;
        this.protocol = protocol;
        this.regionId = regionId;
        this.zoneId = zoneId;
        this.healthCheckUrl = healthCheckUrl;
        this.url = url;
        this.description = description;
        this.groupId = groupId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
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

    public String getHealthCheckUrl() {
        return healthCheckUrl;
    }

    public void setHealthCheckUrl(String healthCheckUrl) {
        this.healthCheckUrl = healthCheckUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
