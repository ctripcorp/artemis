package com.ctrip.soa.artemis.registry.replication;

import com.ctrip.soa.artemis.registry.RegistryService;
import com.ctrip.soa.artemis.registry.replication.GetServicesRequest;
import com.ctrip.soa.artemis.registry.replication.GetServicesResponse;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface RegistryReplicationService extends RegistryService {

    GetServicesResponse getServices(GetServicesRequest request);

}
