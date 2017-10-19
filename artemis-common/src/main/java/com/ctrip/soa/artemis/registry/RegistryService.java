package com.ctrip.soa.artemis.registry;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface RegistryService {

    RegisterResponse register(RegisterRequest request);

    HeartbeatResponse heartbeat(HeartbeatRequest request);

    UnregisterResponse unregister(UnregisterRequest request);

}
