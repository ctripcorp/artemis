package com.ctrip.soa.artemis.web.controller;

import com.ctrip.soa.artemis.management.canary.CanaryService;
import com.ctrip.soa.artemis.management.canary.CanaryServiceImpl;
import com.ctrip.soa.artemis.management.canary.UpdateCanaryIPsRequest;
import com.ctrip.soa.artemis.management.canary.UpdateCanaryIPsResponse;
import com.ctrip.soa.artemis.management.config.RestPaths;
import com.ctrip.soa.artemis.metric.MetricLoggerHelper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by fang_j on 10/07/2016.
 */
@RestController
@RequestMapping(path = RestPaths.MANAGEMENT_CANARY_PATH)
public class CanaryController {
    private final CanaryService canaryService = CanaryServiceImpl.getInstance();

    @RequestMapping(path = RestPaths.MANAGEMENT_UPDATE_CANARY_IPS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public UpdateCanaryIPsResponse updateCanaryIPs(@RequestBody UpdateCanaryIPsRequest request) {
        UpdateCanaryIPsResponse response = canaryService.updateCanaryIPs(request);
        MetricLoggerHelper.logResponseEvent("mock", "update-canary-ips", response);
        return response;
    }
}
