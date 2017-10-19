package com.ctrip.soa.artemis.discovery;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class DiscoveryConfig implements Cloneable {

    public static final String DISCOVERY_GENERIC_SERVICE_ID = "discovery_generic_service_id";

    public static final DiscoveryConfig GENERIC = new DiscoveryConfig(DISCOVERY_GENERIC_SERVICE_ID, new HashMap<String, String>()) {
        @Override
        public void setServiceId(String serviceId) {
            throw new IllegalStateException("GENERIC config cannot be modified.");
        }

        @Override
        public void setRegionId(String regionId) {
            throw new IllegalStateException("GENERIC config cannot be modified.");
        }

        @Override
        public void setZoneId(String zoneId) {
            throw new IllegalStateException("GENERIC config cannot be modified.");
        }

        @Override
        public void setDiscoveryData(Map<String, String> discoveryData) {
            throw new IllegalStateException("GENERIC config cannot be modified.");
        }
    };

    public static boolean isGenericConfig(DiscoveryConfig discoveryConfig) {
        return discoveryConfig != null && DISCOVERY_GENERIC_SERVICE_ID.equals(discoveryConfig.getServiceId());
    }

    private String serviceId;
    private String regionId;
    private String zoneId;
    private Map<String, String> discoveryData;

    public DiscoveryConfig() {

    }

    public DiscoveryConfig(String serviceId) {
        this(serviceId, null);
    }

    public DiscoveryConfig(String serviceId, Map<String, String> discoveryData) {
        this.serviceId = serviceId;
        this.discoveryData = discoveryData;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Map<String, String> getDiscoveryData() {
        return discoveryData;
    }

    public void setDiscoveryData(Map<String, String> discoveryData) {
        this.discoveryData = discoveryData;
    }

    @Override
    public DiscoveryConfig clone() {
        DiscoveryConfig cloned = new DiscoveryConfig(serviceId);
        cloned.discoveryData = discoveryData == null ? null : new HashMap<>(discoveryData);
        return cloned;
    }

    @Override
    public String toString() {
        return "DiscoveryConfig{" +
                "serviceId='" + serviceId + '\'' +
                ", regionId='" + regionId + '\'' +
                ", zoneId='" + zoneId + '\'' +
                ", discoveryData=" + discoveryData +
                '}';
    }
}
