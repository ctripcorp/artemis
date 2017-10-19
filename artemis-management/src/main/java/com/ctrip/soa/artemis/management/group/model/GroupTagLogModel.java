package com.ctrip.soa.artemis.management.group.model;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupTagLogModel extends GroupTagModel {
    private String operation;
    private String extensions;
    private String operatorId;
    private String token;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
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
