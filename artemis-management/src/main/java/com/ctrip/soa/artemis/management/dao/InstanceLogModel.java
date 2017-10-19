package com.ctrip.soa.artemis.management.dao;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
public class InstanceLogModel extends InstanceModel {
    private boolean complete;
    private String extensions;

    public InstanceLogModel() {
    }

    public InstanceLogModel(String regionId, String serviceId, String instanceId, String operation, String operatorId) {
        super(regionId, serviceId, instanceId, operation, operatorId, null);
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(final boolean complete) {
        this.complete = complete;
    }
    public String getExtensions() {
        return extensions;
    }
    public void setExtensions(final String extensions) {
        this.extensions = extensions;
    }


    public static InstanceLogModel of(final InstanceModel instance, final boolean isComplete) {
        if (instance == null) {
            return null;
        }
        final InstanceLogModel log = new InstanceLogModel();
        log.setRegionId(instance.getRegionId());
        log.setServiceId(instance.getServiceId());
        log.setInstanceId(instance.getInstanceId());
        log.setOperation(instance.getOperation());
        log.setOperatorId(instance.getOperatorId());
        log.setToken(instance.getToken());
        log.setComplete(isComplete);
        log.setExtensions("{}");
        return log;
    }

    public static List<InstanceLogModel> of(final List<InstanceModel> instances, final boolean isComplete) {
        if (CollectionUtils.isEmpty(instances)) {
            return Lists.newArrayList();
        }
        final List<InstanceLogModel> logs = Lists.newArrayList();
        for (final InstanceModel instance : instances) {
            logs.add(InstanceLogModel.of(instance, isComplete));
        }
        return logs;
    }
}
