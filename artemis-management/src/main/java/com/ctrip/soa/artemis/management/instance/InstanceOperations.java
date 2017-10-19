package com.ctrip.soa.artemis.management.instance;

import java.util.List;

import com.ctrip.soa.artemis.InstanceKey;

/**
 * Created by fang_j on 10/07/2016.
 */
public class InstanceOperations {

    private InstanceKey instanceKey;
    private List<String> operations;

    public InstanceOperations() {

    }

    public InstanceOperations(InstanceKey instanceKey, List<String> operations) {
        this.instanceKey = instanceKey;
        this.operations = operations;
    }

    public InstanceKey getInstanceKey() {
        return instanceKey;
    }

    public void setInstanceKey(InstanceKey instanceKey) {
        this.instanceKey = instanceKey;
    }

    public List<String> getOperations() {
        return operations;
    }

    public void setOperations(List<String> operations) {
        this.operations = operations;
    }

}
