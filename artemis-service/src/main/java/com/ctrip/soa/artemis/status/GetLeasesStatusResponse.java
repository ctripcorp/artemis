package com.ctrip.soa.artemis.status;

import java.util.List;
import java.util.Map;

import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class GetLeasesStatusResponse implements HasResponseStatus {

    private long _leaseUpdateMaxCount;
    private long _leaseUpdateMaxCountLastUpdateTime;
    private long _leaseUpdateCountLastTimeWindow;
    private boolean _isSafe;
    private boolean _isSafeCheckEnabled;
    private int _leaseCount;
    private Map<Service, List<LeaseStatus>> _leasesStatus;
    private ResponseStatus _responseStatus;

    public GetLeasesStatusResponse() {

    }

    public GetLeasesStatusResponse(long leaseUpdateMaxCount, long leaseUpdateMaxCountLastUpdateTime, long leaseUpdateCountLastTimeWindow,
            boolean isSafe, boolean isSafeCheckEnabled, int leaseCount, Map<Service, List<LeaseStatus>> leasesStatus, ResponseStatus responseStatus) {
        _leaseUpdateMaxCount = leaseUpdateMaxCount;
        _leaseUpdateMaxCountLastUpdateTime = leaseUpdateMaxCountLastUpdateTime;
        _leaseUpdateCountLastTimeWindow = leaseUpdateCountLastTimeWindow;
        _isSafe = isSafe;
        _isSafeCheckEnabled = isSafeCheckEnabled;
        _leaseCount = leaseCount;
        _leasesStatus = leasesStatus;
        _responseStatus = responseStatus;
    }

    @Override
    public ResponseStatus getResponseStatus() {
        return _responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        _responseStatus = responseStatus;
    }

    public long getLeaseUpdateMaxCount() {
        return _leaseUpdateMaxCount;
    }

    public void setLeaseUpdateMaxCount(long leaseUpdateMaxCount) {
        _leaseUpdateMaxCount = leaseUpdateMaxCount;
    }

    public long getLeaseUpdateMaxCountLastUpdateTime() {
        return _leaseUpdateMaxCountLastUpdateTime;
    }

    public void setLeaseUpdateMaxCountLastUpdateTime(long leaseUpdateMaxCountLastUpdateTime) {
        _leaseUpdateMaxCountLastUpdateTime = leaseUpdateMaxCountLastUpdateTime;
    }

    public long getLeaseUpdateCountLastTimeWindow() {
        return _leaseUpdateCountLastTimeWindow;
    }

    public void setLeaseUpdateCountLastTimeWindow(long leaseUpdateCountLastTimeWindow) {
        _leaseUpdateCountLastTimeWindow = leaseUpdateCountLastTimeWindow;
    }

    public boolean isIsSafe() {
        return _isSafe;
    }

    public void setIsSafe(boolean isSafe) {
        _isSafe = isSafe;
    }

    public boolean isIsSafeCheckEnabled() {
        return _isSafeCheckEnabled;
    }

    public void setIsSafeCheckEnabled(boolean isSafeCheckEnabled) {
        _isSafeCheckEnabled = isSafeCheckEnabled;
    }

    public int getLeaseCount() {
        return _leaseCount;
    }

    public void setLeaseCount(int leaseCount) {
        _leaseCount = leaseCount;
    }

    public Map<Service, List<LeaseStatus>> getLeases() {
        return _leasesStatus;
    }

    public void setLeases(Map<Service, List<LeaseStatus>> leasesStatus) {
        _leasesStatus = leasesStatus;
    }

}
