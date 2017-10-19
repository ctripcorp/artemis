package com.ctrip.soa.artemis.management.zone.model;

import com.ctrip.soa.artemis.management.common.OperationContext;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ZoneOperationLogModel  extends ZoneOperationModel {
    private String operatorId;
    private String token;
    private String reason;
    private boolean complete;

    public ZoneOperationLogModel() {
    }

    public ZoneOperationLogModel(String operation, String zoneId, String serviceId, String regionId, String operatorId) {
        super(operation, zoneId, serviceId, regionId);
        this.operatorId = operatorId;
    }

    public ZoneOperationLogModel(OperationContext operationContext, ZoneOperationModel zoneOperationModel, boolean isComplete) {
        super(zoneOperationModel.getOperation(), zoneOperationModel.getZoneId(), zoneOperationModel.getServiceId(), zoneOperationModel.getRegionId());
        this.operatorId = operationContext.getOperatorId();
        this.token = operationContext.getToken();
        this.reason = operationContext.getReason();
        this.complete = isComplete;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
