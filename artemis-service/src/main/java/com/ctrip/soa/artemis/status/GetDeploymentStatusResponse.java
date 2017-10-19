package com.ctrip.soa.artemis.status;

import java.util.Map;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class GetDeploymentStatusResponse implements HasResponseStatus {

    private String _regionId;
    private String _zoneId;
    private String _appId;
    private String _machineName;
    private String _ip;
    private int _port;
    private String _protocol;
    private String _path;

    private Map<String, Integer> _sources;
    private Map<String, String> _properties;

    private ResponseStatus _responseStatus;

    public GetDeploymentStatusResponse() {

    }

    public GetDeploymentStatusResponse(String regionId, String zoneId, String appId, String machineName, String ip,
            int port, String protocol, String path, Map<String, Integer> sources, Map<String, String> properties,
            ResponseStatus responseStatus) {
        _regionId = regionId;
        _zoneId = zoneId;
        _appId = appId;
        _machineName = machineName;
        _ip = ip;
        _port = port;
        _protocol = protocol;
        _path = path;
        _sources = sources;
        _properties = properties;
        _responseStatus = responseStatus;
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

    public String getAppId() {
        return _appId;
    }

    public void setAppId(String appId) {
        _appId = appId;
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

    public String getPath() {
        return _path;
    }

    public void setPath(String path) {
        _path = path;
    }

    @Override
    public ResponseStatus getResponseStatus() {
        return _responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        _responseStatus = responseStatus;
    }

    public Map<String, Integer> getSources() {
        return _sources;
    }

    public void setSources(Map<String, Integer> sources) {
        _sources = sources;
    }

    public Map<String, String> getProperties() {
        return _properties;
    }

    public void setProperties(Map<String, String> properties) {
        _properties = properties;
    }

}
