package com.ctrip.soa.artemis;

import com.ctrip.soa.artemis.Instance;
import com.google.common.base.Objects;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class InstanceChange {

    public interface ChangeType {
        String NEW = "new";
        String DELETE = "delete";
        String CHANGE = "change";
        String RELOAD = "reload";
    }

    private Instance _instance;

    private String _changeType;

    private long _changeTime;

    public InstanceChange() {

    }

    public InstanceChange(Instance instance, String changeType) {
        this(instance, changeType, System.currentTimeMillis());
    }

    public InstanceChange(Instance instance, String changeType, long changeTime) {
        _instance = instance;
        _changeType = changeType;
        _changeTime = changeTime;
    }

    public Instance getInstance() {
        return _instance;
    }

    public void setInstance(Instance instance) {
        _instance = instance;
    }

    public String getChangeType() {
        return _changeType;
    }

    public void setChangeType(String changeType) {
        _changeType = changeType;
    }

    public long getChangeTime() {
        return _changeTime;
    }

    public void setChangeTime(long changeTime) {
        _changeTime = changeTime;
    }

    @Override
    public int hashCode() {
        return _instance == null ? 0 : _instance.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        InstanceChange other = (InstanceChange) obj;
        return Objects.equal(_instance, other._instance);
    }

    @Override
    public String toString() {
        return "{ instance=" + _instance + ", changeType=" + _changeType + ", changeTime=" + _changeTime + " }";
    }

}
