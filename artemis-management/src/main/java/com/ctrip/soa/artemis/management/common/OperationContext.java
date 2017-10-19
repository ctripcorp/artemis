package com.ctrip.soa.artemis.management.common;

/**
 * Created by fang_j on 10/07/2016.
 */
public class OperationContext {
    private String operatorId;
    private String token;
    private String operation;
    private String reason;
    private String extensions;

    public OperationContext() {
        this.extensions = "{}";
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

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    @Override
    public String toString() {
        return "OperationContext{" +
                "operatorId='" + operatorId + '\'' +
                ", token='" + token + '\'' +
                ", operation='" + operation + '\'' +
                ", reason='" + reason + '\'' +
                ", extensions='" + extensions + '\'' +
                '}';
    }
}
