package com.ctrip.soa.artemis.lease;

import java.util.concurrent.locks.ReentrantLock;

import com.ctrip.soa.caravan.configuration.typed.TypedProperty;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class Lease<T> {

    private TypedProperty<Integer> _ttlProperty;

    private LeaseManager<T> _leaseManager;
    private long _creationTime;
    private volatile long _renewalTime;
    private volatile long _evictionTime;
    private volatile boolean _isExpired;

    private T _data;

    private ReentrantLock _lock = new ReentrantLock();

    Lease(LeaseManager<T> leaseManager, T data, TypedProperty<Integer> ttlProperty) {
        _leaseManager = leaseManager;

        _data = data;
        _ttlProperty = ttlProperty;
        _creationTime = System.currentTimeMillis();
        _renewalTime = _creationTime;
    }

    public long creationTime() {
        return _creationTime;
    }

    public long renewalTime() {
        return _renewalTime;
    }

    public long evictionTime() {
        return _evictionTime;
    }

    public long ttl() {
        return _ttlProperty.typedValue().intValue();
    }

    public T data() {
        return _data;
    }

    protected boolean isExpired() {
        if (_isExpired)
            return true;

        _isExpired = System.currentTimeMillis() > renewalTime() + ttl() || isEvicted();
        return _isExpired;
    }

    protected boolean isEvicted() {
        return evictionTime() > 0;
    }

    public boolean renew() {
        if (!tryLock())
            return false;

        try {
            if (isExpired())
                return false;

            _renewalTime = System.currentTimeMillis();
            _leaseManager.leaseUpdateSafeChecker().markUpdate();
            return true;
        } finally {
            releaseLock();
        }
    }

    public void evict() {
        if (_evictionTime != 0)
            return;

        _evictionTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return String.format("{ data: %s, creationTime: %s, renewalTime: %s, evictionTime: %s, ttl: %s }", data(), creationTime(), renewalTime(),
                evictionTime(), ttl());
    }

    @Override
    public int hashCode() {
        return data() == null ? 0 : data().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass())
            return false;

        @SuppressWarnings("unchecked")
        Lease<T> other = (Lease<T>) obj;
        if (data() == other.data())
            return true;

        if (data() == null || other.data() == null)
            return false;

        return data().equals(other.data());
    }

    protected boolean tryLock() {
        return _lock.tryLock();
    }

    protected void releaseLock() {
        _lock.unlock();
    }

}
