package com.ctrip.soa.artemis.discovery;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface DiscoveryService {

    LookupResponse lookup(LookupRequest request);

    GetServiceResponse getService(GetServiceRequest request);

    GetServicesResponse getServices(GetServicesRequest request);

    GetServicesDeltaResponse getServicesDelta(GetServicesDeltaRequest request);
}
