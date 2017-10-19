package com.ctrip.soa.artemis.registry;

import com.ctrip.soa.artemis.Instance;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class FailedInstance {

    private Instance _instance;
    private String _errorCode;
    private String _errorMessage;

    public FailedInstance() {

    }

    public FailedInstance(Instance instance, String errorCode, String errorMessage) {
        _instance = instance;
        _errorCode = errorCode;
        _errorMessage = errorMessage;
    }

    public Instance getInstance() {
        return _instance;
    }

    public void setInstance(Instance instance) {
        _instance = instance;
    }

    public String getErrorCode() {
        return _errorCode;
    }

    public void setErrorCode(String errorCode) {
        _errorCode = errorCode;
    }

    public String getErrorMessage() {
        return _errorMessage;
    }

    public void setErrorMessage(String errorMesssage) {
        _errorMessage = errorMesssage;
    }

}
