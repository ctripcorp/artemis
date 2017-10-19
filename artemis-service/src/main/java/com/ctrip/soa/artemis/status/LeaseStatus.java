package com.ctrip.soa.artemis.status;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class LeaseStatus {

    private String _instance;
    private String _creationTime;
    private String _renewalTime;
    private String _evitionTime;
    private long _ttl;

    public LeaseStatus(String instance, String creationTime, String renewalTime, String evitionTime, long ttl) {
        setInstance(instance);
        _creationTime = creationTime;
        _renewalTime = renewalTime;
        _evitionTime = evitionTime;
        _ttl = ttl;
    }

    public String getCreationTime() {
        return _creationTime;
    }

    public void setCreationTime(String _creationTime) {
        this._creationTime = _creationTime;
    }

    public String getRenewalTime() {
        return _renewalTime;
    }

    public void setRenewalTime(String _renewalTime) {
        this._renewalTime = _renewalTime;
    }

    public String getEvitionTime() {
        return _evitionTime;
    }

    public void setEvitionTime(String _evitionTime) {
        this._evitionTime = _evitionTime;
    }

    public long getTtl() {
        return _ttl;
    }

    public void setTtl(long _ttl) {
        this._ttl = _ttl;
    }

    public String getInstance() {
        return _instance;
    }

    public void setInstance(String instance) {
        _instance = instance;
    }

}
