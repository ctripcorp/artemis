package com.ctrip.soa.artemis.cache;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class VersionedData<T> {

    private long _version;
    private T _data;

    public VersionedData() {

    }

    public VersionedData(long version, T data) {
        _version = version;
        _data = data;
    }

    public long getVersion() {
        return _version;
    }

    public void setVersion(long version) {
        _version = version;
    }

    public T getData() {
        return _data;
    }

    public void setData(T data) {
        _data = data;
    }

}
