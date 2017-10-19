package com.ctrip.soa.artemis;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class ErrorCodes {

    public static final String SUCCESS = "success";
    public static final String PARTIAL_FAIL = "partial_fail";
    public static final String BAD_REQUEST = "bad-request";
    public static final String RATE_LIMITED = "rate-limited";
    public static final String NO_PERMISSION = "no-permission";
    public static final String DATA_NOT_FOUND = "data-not-found";
    public static final String INTERNAL_SERVICE_ERROR = "internal-service-error";
    public static final String SERVICE_UNAVAILABLE = "service-unavailable";
    public static final String UNKNOWN = "unknown";

    private static Set<String> _rerunnabledErrorCodes = new HashSet<>();
    private static Set<String> _serviceDownErrorCodes = new HashSet<>();

    static {
        _rerunnabledErrorCodes.add(RATE_LIMITED);
        _rerunnabledErrorCodes.add(UNKNOWN);
        _rerunnabledErrorCodes = Collections.unmodifiableSet(_rerunnabledErrorCodes);

        _serviceDownErrorCodes.add(INTERNAL_SERVICE_ERROR);
        _serviceDownErrorCodes.add(SERVICE_UNAVAILABLE);
        _serviceDownErrorCodes = Collections.unmodifiableSet(_serviceDownErrorCodes);
    }

    public static Set<String> rerunnableErrorCodes() {
        return _rerunnabledErrorCodes;
    }

    public static Set<String> serviceDownErrorCodes() {
        return _serviceDownErrorCodes;
    }

}
