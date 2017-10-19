package com.ctrip.soa.artemis.cache;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class VersionedCache<T, D> {

    private VersionedData<T> _versionedData;
    private VersionedData<D> _versionedDelta;

    public VersionedCache() {

    }

    public VersionedCache(VersionedData<T> versionedData, VersionedData<D> versionedDelta) {
        _versionedData = versionedData;
        _versionedDelta = versionedDelta;
    }

    public VersionedData<T> getVersionedData() {
        return _versionedData;
    }

    public void setVersionedData(VersionedData<T> versionedData) {
        _versionedData = versionedData;
    }

    public VersionedData<D> getVersionedDelta() {
        return _versionedDelta;
    }

    public void setVersionedDelta(VersionedData<D> versionedDelta) {
        _versionedDelta = versionedDelta;
    }

}
