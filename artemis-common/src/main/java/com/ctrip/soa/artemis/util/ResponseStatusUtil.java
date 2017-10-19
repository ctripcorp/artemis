package com.ctrip.soa.artemis.util;

import com.ctrip.soa.artemis.ErrorCodes;
import com.ctrip.soa.artemis.ResponseStatus;
import com.ctrip.soa.caravan.common.value.StringValues;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public final class ResponseStatusUtil {

    public static final ResponseStatus SUCCESS_STATUS = new ResponseStatus(ResponseStatus.Status.SUCCESS, StringValues.EMPTY, ErrorCodes.SUCCESS);
    public static final ResponseStatus RATE_LIMITED_STATUS = newFailStatus("Request is rate limited.", ErrorCodes.RATE_LIMITED);

    public static ResponseStatus newFailStatus(String errorMessage, String errorCode) {
        return new ResponseStatus(ResponseStatus.Status.FAIL, errorMessage, errorCode);
    }

    public static ResponseStatus newPartialFailStatus(String errorMessage) {
        return new ResponseStatus(ResponseStatus.Status.PARTIAL_FAIL, errorMessage, ErrorCodes.PARTIAL_FAIL);
    }

    public static boolean isSuccess(ResponseStatus status) {
        if (status == null)
            return false;

        return ResponseStatus.Status.SUCCESS.equals(status.getStatus());
    }

    public static boolean isFail(ResponseStatus status) {
        if (status == null)
            return false;

        return ResponseStatus.Status.FAIL.equals(status.getStatus());
    }

    public static boolean isPartialFail(ResponseStatus status) {
        if (status == null)
            return false;

        return ResponseStatus.Status.PARTIAL_FAIL.equals(status.getStatus());
    }

    public static boolean isServiceDown(ResponseStatus status) {
        if (status == null)
            return false;

        return isFail(status) && status.getErrorCode() != null && ErrorCodes.serviceDownErrorCodes().contains(status.getErrorCode());
    }

    public static boolean isRateLimited(ResponseStatus status) {
        if (status == null)
            return false;

        return isFail(status) && ErrorCodes.RATE_LIMITED.equals(status.getErrorCode());
    }

    public static boolean isRerunnable(ResponseStatus status) {
        if (status == null)
            return false;

        return isFail(status) && ErrorCodes.rerunnableErrorCodes().contains(status.getErrorCode());
    }

    private ResponseStatusUtil() {

    }

}
