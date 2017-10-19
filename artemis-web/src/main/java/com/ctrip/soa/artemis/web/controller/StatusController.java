package com.ctrip.soa.artemis.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ctrip.soa.artemis.config.RestPaths;
import com.ctrip.soa.artemis.metric.MetricLoggerHelper;
import com.ctrip.soa.artemis.status.GetClusterNodeStatusRequest;
import com.ctrip.soa.artemis.status.GetClusterNodeStatusResponse;
import com.ctrip.soa.artemis.status.GetClusterStatusRequest;
import com.ctrip.soa.artemis.status.GetClusterStatusResponse;
import com.ctrip.soa.artemis.status.GetConfigStatusRequest;
import com.ctrip.soa.artemis.status.GetConfigStatusResponse;
import com.ctrip.soa.artemis.status.GetDeploymentStatusRequest;
import com.ctrip.soa.artemis.status.GetDeploymentStatusResponse;
import com.ctrip.soa.artemis.status.GetLeasesStatusRequest;
import com.ctrip.soa.artemis.status.GetLeasesStatusResponse;
import com.ctrip.soa.artemis.status.StatusService;
import com.ctrip.soa.artemis.status.StatusServiceImpl;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
@RestController
@RequestMapping(path = RestPaths.STATUS_PATH)
public class StatusController {

    private StatusService _statusService = StatusServiceImpl.getInstance();

    @RequestMapping(path = RestPaths.STATUS_NODE_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetClusterNodeStatusResponse getClusterNodeStatus(@RequestBody GetClusterNodeStatusRequest request) {
        GetClusterNodeStatusResponse response = _statusService.getClusterNodeStatus(request);
        MetricLoggerHelper.logResponseEvent("status", "get-cluster-node", response);
        return response;
    }

    @RequestMapping(path = RestPaths.STATUS_NODE_RELATIVE_PATH, method = RequestMethod.GET, produces = "application/json")
    public GetClusterNodeStatusResponse getClusterNodeStatus() {
        GetClusterNodeStatusResponse response = _statusService.getClusterNodeStatus(new GetClusterNodeStatusRequest());
        MetricLoggerHelper.logResponseEvent("status", "get-cluster-node", response);
        return response;
    }

    @RequestMapping(path = RestPaths.STATUS_CLUSTER_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetClusterStatusResponse getClusterStatus(@RequestBody GetClusterStatusRequest request) {
        GetClusterStatusResponse response = _statusService.getClusterStatus(request);
        MetricLoggerHelper.logResponseEvent("status", "get-cluster", response);
        return response;
    }

    @RequestMapping(path = RestPaths.STATUS_CLUSTER_RELATIVE_PATH, method = RequestMethod.GET, produces = "application/json")
    public GetClusterStatusResponse getClusterStatus() {
        GetClusterStatusResponse response = _statusService.getClusterStatus(new GetClusterStatusRequest());
        MetricLoggerHelper.logResponseEvent("status", "get-cluster", response);
        return response;
    }

    @RequestMapping(path = RestPaths.STATUS_LEASES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetLeasesStatusResponse getLeasesStatus(@RequestBody GetLeasesStatusRequest request) {
        GetLeasesStatusResponse response = _statusService.getLeasesStatus(request);
        MetricLoggerHelper.logResponseEvent("status", "get-leases", response);
        return response;
    }

    @RequestMapping(path = RestPaths.STATUS_LEASES_RELATIVE_PATH, method = RequestMethod.GET, produces = "application/json")
    public GetLeasesStatusResponse getLeasesStatus(@RequestParam(required = false) List<String> appIds) {
        GetLeasesStatusResponse response = _statusService.getLeasesStatus(new GetLeasesStatusRequest(appIds));
        MetricLoggerHelper.logResponseEvent("status", "get-leases", response);
        return response;
    }

    @RequestMapping(path = RestPaths.STATUS_LEGACY_LEASES_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetLeasesStatusResponse getLegacyLeasesStatus(@RequestBody GetLeasesStatusRequest request) {
        GetLeasesStatusResponse response = _statusService.getLegacyLeasesStatus(request);
        MetricLoggerHelper.logResponseEvent("status", "get-legacy-leases", response);
        return response;
    }

    @RequestMapping(path = RestPaths.STATUS_LEGACY_LEASES_RELATIVE_PATH, method = RequestMethod.GET, produces = "application/json")
    public GetLeasesStatusResponse getLegacyLeasesStatus(@RequestParam(required = false) List<String> appIds) {
        GetLeasesStatusResponse response = _statusService.getLegacyLeasesStatus(new GetLeasesStatusRequest(appIds));
        MetricLoggerHelper.logResponseEvent("status", "get-legacy-leases", response);
        return response;
    }

    @RequestMapping(path = RestPaths.STATUS_CONFIG_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetConfigStatusResponse getConfigStatus(@RequestBody GetConfigStatusRequest request) {
        GetConfigStatusResponse response = _statusService.getConfigStatus(request);
        MetricLoggerHelper.logResponseEvent("status", "get-config", response);
        return response;
    }

    @RequestMapping(path = RestPaths.STATUS_CONFIG_RELATIVE_PATH, method = RequestMethod.GET, produces = "application/json")
    public GetConfigStatusResponse getConfigStatus() {
        GetConfigStatusResponse response = _statusService.getConfigStatus(new GetConfigStatusRequest());
        MetricLoggerHelper.logResponseEvent("status", "get-config", response);
        return response;
    }

    @RequestMapping(path = RestPaths.STATUS_DEPLOYMENT_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetDeploymentStatusResponse getDeploymentStatus(@RequestBody GetDeploymentStatusRequest request) {
        GetDeploymentStatusResponse response = _statusService.getDeploymentStatus(request);
        MetricLoggerHelper.logResponseEvent("status", "get-deployment", response);
        return response;
    }

    @RequestMapping(path = RestPaths.STATUS_DEPLOYMENT_RELATIVE_PATH, method = RequestMethod.GET, produces = "application/json")
    public GetDeploymentStatusResponse getDeploymentStatus() {
        GetDeploymentStatusResponse response = _statusService.getDeploymentStatus(new GetDeploymentStatusRequest());
        MetricLoggerHelper.logResponseEvent("status", "get-deployment", response);
        return response;
    }

}
