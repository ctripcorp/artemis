package com.ctrip.soa.artemis.discovery;

import java.util.List;
import java.util.Map;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.InstanceChange;
import com.ctrip.soa.artemis.ResponseStatus;
import com.ctrip.soa.artemis.Service;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class GetServicesDeltaResponse implements HasResponseStatus {

    private Map<Service, List<InstanceChange>> _delta;

    private long _version;

    private ResponseStatus _responseStatus;

    public GetServicesDeltaResponse() {

    }

    public GetServicesDeltaResponse(Map<Service, List<InstanceChange>> delta, long version, ResponseStatus responseStatus) {
        _delta = delta;
        _version = version;
        _responseStatus = responseStatus;
    }

    @Override
    public ResponseStatus getResponseStatus() {
        return _responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        _responseStatus = responseStatus;
    }

    public Map<Service, List<InstanceChange>> getDelta() {
        return _delta;
    }

    public void setDelta(Map<Service, List<InstanceChange>> delta) {
        _delta = delta;
    }

    public long getVersion() {
        return _version;
    }

    public void setVersion(long version) {
        _version = version;
    }

}
