package com.ctrip.soa.artemis.status;

import java.util.List;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class GetLeasesStatusRequest {

    private List<String> _serviceIds;

    public GetLeasesStatusRequest() {

    }

    public GetLeasesStatusRequest(List<String> serviceIds) {
        _serviceIds = serviceIds;
    }

    public List<String> getServiceIds() {
        return _serviceIds;
    }

    public void setServiceIds(List<String> serviceIds) {
        _serviceIds = serviceIds;
    }

}
