package com.ctrip.soa.artemis.registry;

import java.util.List;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class UnregisterResponse implements HasResponseStatus, HasFailedInstances {

    private List<FailedInstance> _failedFailedInstances;

    private ResponseStatus _responseStatus;

    public UnregisterResponse() {

    }

    public UnregisterResponse(List<FailedInstance> failedFailedInstances, ResponseStatus responseStatus) {
        _failedFailedInstances = failedFailedInstances;
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

    @Override
    public List<FailedInstance> getFailedInstances() {
        return _failedFailedInstances;
    }

    public void setFailedInstances(List<FailedInstance> failedFailedInstances) {
        _failedFailedInstances = failedFailedInstances;
    }

}
