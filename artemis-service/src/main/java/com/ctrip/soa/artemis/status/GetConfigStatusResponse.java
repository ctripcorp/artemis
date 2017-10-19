package com.ctrip.soa.artemis.status;

import java.util.Map;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class GetConfigStatusResponse implements HasResponseStatus {

    private Map<String, Integer> _sources;
    private Map<String, String> _properties;
    private ResponseStatus _responseStatus;

    public GetConfigStatusResponse() {

    }

    public GetConfigStatusResponse(Map<String, Integer> sources, Map<String, String> properties,
            ResponseStatus responseStatus) {
        _sources = sources;
        _properties = properties;
        _responseStatus = responseStatus;
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
