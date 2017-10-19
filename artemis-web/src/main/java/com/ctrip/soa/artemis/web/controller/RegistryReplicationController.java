package com.ctrip.soa.artemis.web.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ctrip.soa.artemis.config.RestPaths;
import com.ctrip.soa.artemis.metric.MetricLoggerHelper;
import com.ctrip.soa.artemis.registry.replication.RegistryReplicationService;
import com.ctrip.soa.artemis.registry.replication.RegistryReplicationServiceImpl;
import com.ctrip.soa.artemis.registry.replication.GetServicesRequest;
import com.ctrip.soa.artemis.registry.replication.GetServicesResponse;
import com.ctrip.soa.artemis.registry.HeartbeatRequest;
import com.ctrip.soa.artemis.registry.HeartbeatResponse;
import com.ctrip.soa.artemis.registry.RegisterRequest;
import com.ctrip.soa.artemis.registry.RegisterResponse;
import com.ctrip.soa.artemis.registry.UnregisterRequest;
import com.ctrip.soa.artemis.registry.UnregisterResponse;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
@RestController
@RequestMapping(path = RestPaths.REPLICATION_REGISTRY_PATH)
public class RegistryReplicationController {

    private RegistryReplicationService _registryReplicationService = RegistryReplicationServiceImpl.getInstance();

    @RequestMapping(path = RestPaths.REPLICATION_REGISTRY_REGISTER_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        RegisterResponse response = _registryReplicationService.register(request);
        MetricLoggerHelper.logResponseEvent("registry-replication", "register", response);
        return response;
    }

    @RequestMapping(path = RestPaths.REPLICATION_REGISTRY_HEARTBEAT_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public HeartbeatResponse heartbeat(@RequestBody HeartbeatRequest request) {
        HeartbeatResponse response = _registryReplicationService.heartbeat(request);
        MetricLoggerHelper.logResponseEvent("registry-replication", "heartbeat", response);
        return response;
    }

    @RequestMapping(path = RestPaths.REPLICATION_REGISTRY_UNREGISTER_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public UnregisterResponse unregister(@RequestBody UnregisterRequest request) {
        UnregisterResponse response = _registryReplicationService.unregister(request);
        MetricLoggerHelper.logResponseEvent("registry-replication", "unregister", response);
        return response;
    }

    @RequestMapping(path = RestPaths.REPLICATION_REGISTRY_GET_SERVICES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetServicesResponse getServices(@RequestBody GetServicesRequest request) {
        GetServicesResponse response = _registryReplicationService.getServices(request);
        MetricLoggerHelper.logResponseEvent("registry-replication", "get-services", response);
        return response;
    }

    @RequestMapping(path = RestPaths.REPLICATION_REGISTRY_GET_SERVICES_RELATIVE_PATH, method = RequestMethod.GET, produces = "application/json")
    public GetServicesResponse getServices(@RequestParam(required=true) String regionId, @RequestParam(required=false) String zoneId) {
        GetServicesResponse response = _registryReplicationService.getServices(new GetServicesRequest(regionId, zoneId));
        MetricLoggerHelper.logResponseEvent("registry-replication", "get-services", response);
        return response;
    }

}
