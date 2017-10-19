package com.ctrip.soa.artemis.management.server;

import com.ctrip.soa.artemis.ServerKey;

/**
 * Created by fang_j on 10/07/2016.
 */
public class OperateServerRequest {

    private ServerKey serverKey;
    private String operation;
    private boolean operationComplete;
    private String operatorId;
    private String token;

    public ServerKey getServerKey() {
        return serverKey;
    }

    public void setServerKey(ServerKey serverKey) {
        this.serverKey = serverKey;
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
