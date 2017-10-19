package com.ctrip.soa.artemis;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class ResponseStatus {

    public interface Status {

        String SUCCESS = "success";
        String FAIL = "fail";
        String PARTIAL_FAIL = "partial_fail";
        String UKNOWN = "unknown";

    }

    private String _status;
    private String _errorCode;
    private String _message;

    public ResponseStatus() {

    }

    public ResponseStatus(String status, String message, String errorCode) {
        _status = status;
        _message = message;
        _errorCode = errorCode;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        _message = message;
    }

    public String getErrorCode() {
        return _errorCode;
    }

    public void setErrorCode(String errorCode) {
        _errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "ResponseStatus{" +
                "status='" + _status + '\'' +
                ", errorCode='" + _errorCode + '\'' +
                ", message='" + _message + '\'' +
                '}';
    }
}
