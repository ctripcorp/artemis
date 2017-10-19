package com.ctrip.soa.artemis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class ServiceGroup implements  Cloneable {
    private String groupKey;
    private Integer weight;
    private List<String> instanceIds;
    private List<Instance> instances;
    private Map<String, String> metadata;

    public ServiceGroup() {
        this(null, null);
    }

    public ServiceGroup(String groupKey, Integer weight) {
        this(groupKey, weight, null, null);
    }

    public ServiceGroup(String groupKey, Integer weight, List<Instance> instances, Map<String, String> metadata) {
        this.groupKey = groupKey;
        this.weight = weight;
        this.instances = instances;
        this.metadata = metadata;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public List<String> getInstanceIds() {
        return instanceIds;
    }

    public void setInstanceIds(List<String> instanceIds) {
        this.instanceIds = instanceIds;
    }

    public List<Instance> getInstances() {
        return instances;
    }

    public void setInstances(List<Instance> instances) {
        this.instances = instances;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @Override
    public ServiceGroup clone() {
        ServiceGroup cloned = new ServiceGroup(groupKey, weight, instances, metadata);
        Map<String, String> metadata = this.metadata;
        if (metadata != null) {
            metadata = new HashMap<>(metadata);
        }
        cloned.setMetadata(metadata);
        return cloned;
    }
}
