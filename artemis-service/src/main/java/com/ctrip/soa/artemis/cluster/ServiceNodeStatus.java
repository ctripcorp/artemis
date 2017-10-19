package com.ctrip.soa.artemis.cluster;

import com.ctrip.soa.artemis.cluster.ServiceNode;
import com.google.common.base.Objects;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class ServiceNodeStatus {

    public interface Status {

        String STARTING = "starting";
        String UP = "up";
        String DOWN = "down";
        String UNKNOWN = "unknown";

    }

    private ServiceNode _node;

    private volatile String _status;

    private volatile Boolean _canServiceDiscovery;

    private volatile Boolean _canServiceRegistry;

    private volatile Boolean _allowRegistryFromOtherZone;

    private volatile Boolean _allowDiscoveryFromOtherZone;

    public ServiceNodeStatus() {

    }

    public ServiceNodeStatus(ServiceNode node, String status, Boolean canServiceRegistry, Boolean canServiceDiscovery, Boolean allowRegistryFromOtherZone,
            Boolean allowDiscoveryFromOtherZone) {
        _node = node;
        _status = status;
        _canServiceRegistry = canServiceRegistry;
        _canServiceDiscovery = canServiceDiscovery;
        _allowRegistryFromOtherZone = allowRegistryFromOtherZone;
        _allowDiscoveryFromOtherZone = allowDiscoveryFromOtherZone;
    }

    public ServiceNode getNode() {
        return _node;
    }

    public void setNode(ServiceNode node) {
        _node = node;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public Boolean isCanServiceDiscovery() {
        return _canServiceDiscovery;
    }

    public void setCanServiceDiscovery(Boolean canServiceDiscovery) {
        _canServiceDiscovery = canServiceDiscovery;
    }

    public Boolean isCanServiceRegistry() {
        return _canServiceRegistry;
    }

    public void setCanServiceRegistry(Boolean canServiceRegistry) {
        _canServiceRegistry = canServiceRegistry;
    }

    public Boolean isAllowRegistryFromOtherZone() {
        return _allowRegistryFromOtherZone;
    }

    public void setAllowRegistryFromOtherZone(Boolean allowRegistryFromOtherZone) {
        _allowRegistryFromOtherZone = allowRegistryFromOtherZone;
    }

    public Boolean isAllowDiscoveryFromOtherZone() {
        return _allowDiscoveryFromOtherZone;
    }

    public void setAllowDiscoveryFromOtherZone(Boolean allowDiscoveryFromOtherZone) {
        _allowDiscoveryFromOtherZone = allowDiscoveryFromOtherZone;
    }

    @Override
    public String toString() {
        return String.format("{ node: %s, status: %s, canServiceRegistry: %s, canServiceDiscovery: %s }", _node, _status, _canServiceRegistry,
                _canServiceDiscovery);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (this.getClass() != other.getClass())
            return false;

        ServiceNodeStatus nodeStatus = (ServiceNodeStatus) other;
        return Objects.equal(_node, nodeStatus._node) && Objects.equal(_status, nodeStatus._status)
                && Objects.equal(_canServiceRegistry, nodeStatus._canServiceRegistry) && Objects.equal(_canServiceDiscovery, nodeStatus._canServiceDiscovery);
    }

    @Override
    public int hashCode() {
        int result = _node != null ? _node.hashCode() : 0;
        result = 31 * result + (_status != null ? _status.hashCode() : 0);
        result = 31 * result + (_canServiceDiscovery != null ? _canServiceDiscovery.hashCode() : 0);
        result = 31 * result + (_canServiceRegistry != null ? _canServiceRegistry.hashCode() : 0);
        result = 31 * result + (_allowRegistryFromOtherZone != null ? _allowRegistryFromOtherZone.hashCode() : 0);
        result = 31 * result + (_allowDiscoveryFromOtherZone != null ? _allowDiscoveryFromOtherZone.hashCode() : 0);
        return result;
    }
}
