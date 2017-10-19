package com.ctrip.soa.artemis.management.util;

import com.ctrip.soa.artemis.InstanceKey;
import com.ctrip.soa.artemis.ServerKey;
import com.ctrip.soa.artemis.management.dao.InstanceModel;
import com.ctrip.soa.artemis.management.dao.ServerModel;
import com.ctrip.soa.caravan.common.value.StringValues;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ModelUtil {

    private ModelUtil() {

    }

    public static CheckResult check(InstanceModel instance) {
        if (StringValues.isNullOrWhitespace(instance.getRegionId()))
            return new CheckResult(false, "regionId is null or empty");

        if (StringValues.isNullOrWhitespace(instance.getServiceId()))
            return new CheckResult(false, "serviceId is null or empty");

        if (StringValues.isNullOrWhitespace(instance.getInstanceId()))
            return new CheckResult(false, "instanceId is null or empty");

        if (StringValues.isNullOrWhitespace(instance.getOperation()))
            return new CheckResult(false, "operation is null or empty");

        if (StringValues.isNullOrWhitespace(instance.getOperatorId()))
            return new CheckResult(false, "operatorId is null or empty");

        return new CheckResult(true, null);
    }

    public static CheckResult check(ServerModel server) {
        if (StringValues.isNullOrWhitespace(server.getRegionId()))
            return new CheckResult(false, "regionId is null or empty");

        if (StringValues.isNullOrWhitespace(server.getServerId()))
            return new CheckResult(false, "serverId is null or empty");

        if (StringValues.isNullOrWhitespace(server.getOperation()))
            return new CheckResult(false, "operation is null or empty");

        if (StringValues.isNullOrWhitespace(server.getOperatorId()))
            return new CheckResult(false, "operatorId is null or empty");

        return new CheckResult(true, null);
    }

    public static InstanceModel newInstance(InstanceKey instanceKey, String operation, String operatorId, String token) {
        return newInstance(instanceKey.getRegionId(), instanceKey.getServiceId(), instanceKey.getInstanceId(), operation, operatorId, token);
    }

    public static InstanceModel newInstance(String regionId, String serviceId, String instanceId, String operation, String operatorId, String token) {
        regionId = StringValues.trim(regionId);
        serviceId = StringValues.trim(serviceId);
        instanceId = StringValues.trim(instanceId);
        operation = StringValues.trim(operation);
        operatorId = StringValues.trim(operatorId);
        token = StringValues.trim(token);
        return new InstanceModel(regionId, serviceId, instanceId, operation, operatorId, token);
    }

    public static ServerModel newServer(ServerKey serverKey, String operation, String operatorId, String token) {
        return newServer(serverKey.getRegionId(), serverKey.getServerId(), operation, operatorId, token);
    }

    public static ServerModel newServer(String regionId, String serverId, String operation, String operatorId, String token) {
        regionId = StringValues.trim(regionId);
        serverId = StringValues.trim(serverId);
        operation = StringValues.trim(operation);
        operatorId = StringValues.trim(operatorId);
        token = StringValues.trim(token);
        return new ServerModel(regionId, serverId, operation, operatorId, token);
    }
}
