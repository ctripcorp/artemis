package com.ctrip.soa.artemis;

import com.google.common.base.Objects;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class ServerKey {

    public static final ServerKey EMPTY = new ServerKey(null, null) {

        @Override
        public void setRegionId(String regionId) {
            throw new IllegalStateException("EMPTY ServerKey cannot be modified.");
        }

        @Override
        public void setServerId(String serverId) {
            throw new IllegalStateException("EMPTY ServerKey cannot be modified.");
        }

    };

    public static ServerKey of(Instance instance) {
        if (instance == null)
            return EMPTY;

        return new ServerKey(instance.getRegionId(), instance.getIp());
    }

    private String regionId;
    private String serverId;

    public ServerKey() {

    }

    public ServerKey(String regionId, String serverId) {
        this.regionId = regionId;
        this.serverId = serverId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    @Override
    public String toString() {
        return String.format("%s.%s", regionId, serverId).toLowerCase();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other == null)
            return false;

        if (other.getClass() != this.getClass())
            return false;

        return Objects.equal(toString(), other.toString());
    }

}
