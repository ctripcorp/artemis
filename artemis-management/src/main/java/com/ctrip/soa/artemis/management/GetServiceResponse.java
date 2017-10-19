package com.ctrip.soa.artemis.management;

import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;
import com.ctrip.soa.artemis.ServiceGroup;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetServiceResponse implements HasResponseStatus {

    private ResponseStatus responseStatus;

    private Service service;

    private List<ServiceGroup> groups;

    public GetServiceResponse() {

    }

    public GetServiceResponse(Service service, ResponseStatus responseStatus) {
        this.service = service;
        this.responseStatus = responseStatus;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public List<ServiceGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ServiceGroup> groups) {
        this.groups = groups;
    }
}