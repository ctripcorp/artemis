package com.ctrip.soa.artemis.management.zone;

/**
 * Created by fang_j on 10/07/2016.
 */
public interface ZoneService {
    GetAllZoneOperationsResponse getAllZoneOperations(GetAllZoneOperationsRequest request);

    GetZoneOperationsResponse getZoneOperations(GetZoneOperationsRequest request);

    GetZoneOperationsListResponse getZoneOperationsList(GetZoneOperationsListRequest request);

    IsZoneDownResponse isZoneDown(IsZoneDownRequest request);

    OperateZoneOperationsResponse operateZoneOperations(OperateZoneOperationsRequest request);
}
