package com.ctrip.soa.artemis.management.instance;

import com.ctrip.soa.artemis.InstanceKey;

/**
 * Created by fang_j on 10/07/2016.
 */
public class OperateInstanceRequest {

    private InstanceKey instanceKey;
    private String operation;
    private boolean operationComplete;
    private String operatorId;
    private String token;

    public InstanceKey getInstanceKey() {
        return instanceKey;
    }

    public void setInstanceKey(InstanceKey instanceKey) {
        this.instanceKey = instanceKey;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public boolean isOperationComplete() {
        return operationComplete;
    }

    public void setOperationComplete(boolean operationComplete) {
        this.operationComplete = operationComplete;
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

}
