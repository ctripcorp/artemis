package com.ctrip.soa.artemis;

import java.util.HashMap;
import java.util.Map;

import com.ctrip.soa.caravan.common.value.StringValues;
import com.google.common.base.Objects;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class Instance implements Cloneable {

    public interface Status {

        String STARTING = "starting";
        String UP = "up";
        String DOWN = "down";
        String UNHEALTHY = "unhealthy";
        String UNKNOWN = "unknown";

    }

    private String _regionId;
    private String _zoneId;
    private String _groupId;
    private String _serviceId;
    private String _instanceId;
    private String _machineName;
    private String _ip;
    private int _port;
    private String _protocol;
    private String _url;
    private String _healthCheckUrl;
    private String _status;
    private Map<String, String> _metadata;

    public Instance() {

    }

    public Instance(String regionId, String zoneId, String groupId, String serviceId, String instanceId, String machineName, String ip, int port,
            String protocol, String url, String healthCheckUrl, String status, Map<String, String> metadata) {
        _regionId = regionId;
        _zoneId = zoneId;
        _groupId = groupId;
        _serviceId = serviceId;
        _instanceId = instanceId;
        _machineName = machineName;
        _ip = ip;
        _port = port;
        _protocol = protocol;
        _url = url;
        _healthCheckUrl = healthCheckUrl;
        _status = status;
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

    public String getServiceId() {
        return _serviceId;
    }

    public String getGroupId() {
        return _groupId;
    }

    public void setGroupId(String groupId) {
        _groupId = groupId;
    }

    public void setServiceId(String serviceId) {
        _serviceId = serviceId;
    }

    public String getInstanceId() {
        return _instanceId;
    }

    public void setInstanceId(String instanceId) {
        _instanceId = instanceId;
    }

    public String getMachineName() {
        return _machineName;
    }

    public void setMachineName(String machineName) {
        _machineName = machineName;
    }

    public String getIp() {
        return _ip;
    }

    public void setIp(String ip) {
        _ip = ip;
    }

    public int getPort() {
        return _port;
    }

    public void setPort(int port) {
        _port = port;
    }

    public String getProtocol() {
        return _protocol;
    }

    public void setProtocol(String protocol) {
        _protocol = protocol;
    }

    public String getUrl() {
        return _url;
    }

    public void setUrl(String url) {
        _url = url;
    }

    public String getHealthCheckUrl() {
        return _healthCheckUrl;
    }

    public void setHealthCheckUrl(String healthCheckUrl) {
        _healthCheckUrl = healthCheckUrl;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public Map<String, String> getMetadata() {
        return _metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        _metadata = metadata;
    }

    @Override
    public String toString() {
        String string = _regionId + "/" + _zoneId + "/" + _serviceId;
        if (!StringValues.isNullOrWhitespace(_groupId))
            string += "/" + _groupId;
        string += "/" + _instanceId;
        return string.toLowerCase();
    }

    @Override
    public int hashCode() {
        return InstanceKey.of(this).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (other.getClass() != this.getClass())
            return false;

        return Objects.equal(InstanceKey.of(this), InstanceKey.of((Instance) other));
    }

    @Override
    public Instance clone() {
        Instance cloned;
        try {
            cloned = (Instance) super.clone();
        } catch (Throwable ex) {
            cloned = new Instance(_regionId, _zoneId, _groupId, _serviceId, _instanceId, _machineName, _ip, _port, _protocol, _url, _healthCheckUrl, _status,
                    _metadata);
        }

        Map<String, String> metadata = _metadata;
        if (metadata != null)
            metadata = new HashMap<>(metadata);
        cloned.setMetadata(metadata);
        return cloned;
    }

}
