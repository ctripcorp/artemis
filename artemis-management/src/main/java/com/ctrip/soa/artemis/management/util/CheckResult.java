package com.ctrip.soa.artemis.management.util;

/**
 * Created by fang_j on 10/07/2016.
 */
public class CheckResult {

    private boolean _isValid;
    private String _errorMessage;

    public CheckResult(boolean isValid, String errorMessage) {
        _isValid = isValid;
        _errorMessage = errorMessage;
    }

    public boolean isValid() {
        return _isValid;
    }

    public String errorMessage() {
        return _errorMessage;
    }

}
