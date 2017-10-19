package com.ctrip.soa.artemis.web.controller;

import com.ctrip.soa.artemis.management.ZoneServiceImpl;
import com.ctrip.soa.artemis.management.config.RestPaths;
import com.ctrip.soa.artemis.management.zone.*;
import com.ctrip.soa.artemis.metric.MetricLoggerHelper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by fang_j on 10/07/2016.
 */
@RestController
@RequestMapping(path = RestPaths.MANAGEMENT_ZONE_PATH)
public class ManagementZoneController{
    ZoneServiceImpl zoneService = ZoneServiceImpl.getInstance();

    @RequestMapping(path = RestPaths.MANAGEMENT_GET_ALL_ZONE_OPERATIONS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetAllZoneOperationsResponse getAllZoneOperations(@RequestBody GetAllZoneOperationsRequest request) {
        GetAllZoneOperationsResponse response = zoneService.getAllZoneOperations(request);
        MetricLoggerHelper.logResponseEvent("management-zone", "get-all-zone-operations", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GET_ZONE_OPERATIONS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetZoneOperationsResponse getZoneOperations(@RequestBody GetZoneOperationsRequest request) {
        GetZoneOperationsResponse response = zoneService.getZoneOperations(request);
        MetricLoggerHelper.logResponseEvent("management-zone", "get-zone-operations", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_GET_ZONE_OPERATIONS_LIST_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GetZoneOperationsListResponse getZoneOperationsList(@RequestBody GetZoneOperationsListRequest request) {
        GetZoneOperationsListResponse response = zoneService.getZoneOperationsList(request);
        MetricLoggerHelper.logResponseEvent("management-zone", "get-zone-operations-list", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_IS_ZONE_DOWN_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public IsZoneDownResponse isZoneDown(@RequestBody IsZoneDownRequest request) {
        IsZoneDownResponse response = zoneService.isZoneDown(request);
        MetricLoggerHelper.logResponseEvent("management-zone", "is-zone-down", response);
        return response;
    }

    @RequestMapping(path = RestPaths.MANAGEMENT_OPERATE_ZONE_OPERATIONS_RELATIVE_PATH, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public OperateZoneOperationsResponse operateZoneOperations(@RequestBody OperateZoneOperationsRequest request) {
        OperateZoneOperationsResponse response = zoneService.operateZoneOperations(request);
        MetricLoggerHelper.logResponseEvent("management-zone", "operate-zone-operations", response);
        return response;
    }
}
