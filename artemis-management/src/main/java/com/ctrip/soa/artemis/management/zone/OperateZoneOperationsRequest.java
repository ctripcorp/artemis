package com.ctrip.soa.artemis.management.zone;

import com.ctrip.soa.artemis.management.common.OperationContext;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class OperateZoneOperationsRequest extends OperationContext{
    private List<ZoneOperations> zoneOperationsList;
    private boolean operationComplete;
    private String operatorId;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public boolean isOperationComplete() {
        return operationComplete;
    }

    public void setOperationComplete(boolean operationComplete) {
        this.operationComplete = operationComplete;
    }

    public List<ZoneOperations> getZoneOperationsList() {
        return zoneOperationsList;
    }

    public void setZoneOperationsList(List<ZoneOperations> zoneOperationsList) {
        this.zoneOperationsList = zoneOperationsList;
    }

    @Override
    public String toString() {
        return "OperateZoneOperationsRequest{" +
                "zoneOperationsList=" + zoneOperationsList +
                ", operationComplete=" + operationComplete +
                ", operatorId='" + operatorId + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
