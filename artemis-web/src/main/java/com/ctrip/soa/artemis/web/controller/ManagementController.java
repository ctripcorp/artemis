package com.ctrip.soa.artemis.web.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ctrip.soa.artemis.management.config.RestPaths;
import com.ctrip.soa.artemis.management.instance.GetAllInstanceOperationsRequest;
import com.ctrip.soa.artemis.management.instance.GetAllInstanceOperationsResponse;
import com.ctrip.soa.artemis.management.server.GetAllServerOperationsRequest;
import com.ctrip.soa.artemis.management.server.GetAllServerOperationsResponse;
import com.ctrip.soa.artemis.management.instance.GetInstanceOperationsRequest;
import com.ctrip.soa.artemis.management.instance.GetInstanceOperationsResponse;
import com.ctrip.soa.artemis.management.server.GetServerOperationsRequest;
import com.ctrip.soa.artemis.management.server.GetServerOperationsResponse;
import com.ctrip.soa.artemis.management.GetServiceRequest;
import com.ctrip.soa.artemis.management.GetServiceResponse;
import com.ctrip.soa.artemis.management.GetServicesRequest;
import com.ctrip.soa.artemis.management.GetServicesResponse;
import com.ctrip.soa.artemis.management.instance.IsInstanceDownRequest;
import com.ctrip.soa.artemis.management.instance.IsInstanceDownResponse;
import com.ctrip.soa.artemis.management.server.IsServerDownRequest;
import com.ctrip.soa.artemis.management.server.IsServerDownResponse;
import com.ctrip.soa.artemis.management.ManagementServiceImpl;
import com.ctrip.soa.artemis.management.instance.OperateInstanceRequest;
import com.ctrip.soa.artemis.management.instance.OperateInstanceResponse;
import com.ctrip.soa.artemis.management.server.OperateServerRequest;
import com.ctrip.soa.artemis.management.server.OperateServerResponse;
import com.ctrip.soa.artemis.metric.MetricLoggerHelper;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
@RestController
@RequestMapping(path = RestPaths.MANAGEMENT_PATH)
public class ManagementController {
    private ManagementServiceImpl _managementService = ManagementServiceImpl.getInstance();

    @RequestMapping(path = RestPaths.MANAGEMENT_OPERATE_INSTANCE_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public OperateInstanceResponse operateInstance(@RequestBody OperateInstanceRequest request) {
        OperateInstanceResponse response = _managementService.operateInstance(request);
        MetricLoggerHelper.logResponseEvent("management", "operate-instance", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_OPERATE_SERVER_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public OperateServerResponse operateServer(@RequestBody OperateServerRequest request) {
        OperateServerResponse response = _managementService.operateServer(request);
        MetricLoggerHelper.logResponseEvent("management", "operate-server", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_INSTANCE_OPERATIONS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetInstanceOperationsResponse getInstanceOperations(@RequestBody GetInstanceOperationsRequest request) {
        GetInstanceOperationsResponse response = _managementService.getInstanceOperations(request);
        MetricLoggerHelper.logResponseEvent("management", "get-instance-operations", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_SERVER_OPERATIONS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetServerOperationsResponse getServerOperations(@RequestBody GetServerOperationsRequest request) {
        GetServerOperationsResponse response = _managementService.getServerOperations(request);
        MetricLoggerHelper.logResponseEvent("management", "get-server-operations", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_ALL_INSTANCE_OPERATIONS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetAllInstanceOperationsResponse getAllInstanceOperations(@RequestBody GetAllInstanceOperationsRequest request) {
        GetAllInstanceOperationsResponse response = _managementService.getAllInstanceOperations(request);
        MetricLoggerHelper.logResponseEvent("management", "get-all-instance-operations", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_ALL_INSTANCE_OPERATIONS_RELATIVE_PATH, method = RequestMethod.GET, produces = "application/json")
    public GetAllInstanceOperationsResponse getAllInstanceOperations(@RequestParam(required = false) String regionId) {
        GetAllInstanceOperationsRequest request = new GetAllInstanceOperationsRequest();
        request.setRegionId(regionId);
        GetAllInstanceOperationsResponse response = _managementService.getAllInstanceOperations(request);
        MetricLoggerHelper.logResponseEvent("management", "get-all-instance-operations", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_ALL_SERVER_OPERATIONS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetAllServerOperationsResponse getAllServerOperations(@RequestBody GetAllServerOperationsRequest request) {
        GetAllServerOperationsResponse response = _managementService.getAllServerOperations(request);
        MetricLoggerHelper.logResponseEvent("management", "get-all-server-operations", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_ALL_SERVER_OPERATIONS_RELATIVE_PATH, method = RequestMethod.GET, produces = "application/json")
    public GetAllServerOperationsResponse getAllServerOperations(@RequestParam(required = false) String regionId) {
        GetAllServerOperationsRequest request = new GetAllServerOperationsRequest();
        request.setRegionId(regionId);
        GetAllServerOperationsResponse response = _managementService.getAllServerOperations(request);
        MetricLoggerHelper.logResponseEvent("management", "get-all-server-operations", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_INSTANCE_DOWN_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public IsInstanceDownResponse isInstanceDown(@RequestBody IsInstanceDownRequest request) {
        IsInstanceDownResponse response = _managementService.isInstanceDown(request);
        MetricLoggerHelper.logResponseEvent("management", "is-instance-down", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_SERVER_DOWN_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public IsServerDownResponse isServerDown(@RequestBody IsServerDownRequest request) {
        IsServerDownResponse response = _managementService.isServerDown(request);
        MetricLoggerHelper.logResponseEvent("management", "is-server-down", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_SERVICES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetServicesResponse getServices(@RequestBody GetServicesRequest request) {
        GetServicesResponse response = _managementService.getServices(request);
        MetricLoggerHelper.logResponseEvent("management", "get-services", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_SERVICES_RELATIVE_PATH, method = RequestMethod.GET, produces = "application/json")
    public GetServicesResponse getServices(@RequestParam(required = false) String regionId, @RequestParam(required = false) String zoneId) {
        GetServicesRequest request = new GetServicesRequest(regionId, zoneId);
        GetServicesResponse response = _managementService.getServices(request);
        MetricLoggerHelper.logResponseEvent("management", "get-services", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_SERVICE_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetServiceResponse getService(@RequestBody GetServiceRequest request) {
        GetServiceResponse response = _managementService.getService(request);
        MetricLoggerHelper.logResponseEvent("management", "get-service", response);
        return response;
    }

}
