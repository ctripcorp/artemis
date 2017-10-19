package com.ctrip.soa.artemis.management.group.util;

import com.ctrip.soa.artemis.management.common.OperationContext;
import com.ctrip.soa.artemis.management.group.ServiceInstance;
import com.ctrip.soa.artemis.management.group.model.ServiceInstanceLogModel;
import com.ctrip.soa.artemis.management.group.model.ServiceInstanceModel;
import com.ctrip.soa.caravan.common.delegate.Func1;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServiceInstances {
    @SuppressWarnings("unchecked")
    public static ServiceInstance newServiceInstance(ServiceInstanceModel model) {
        Map<String, String> metadata = Maps.newHashMap();
        if (!StringValues.isNullOrWhitespace(model.getMetadata())) {
            try {
                metadata = JacksonJsonSerializer.INSTANCE.deserialize(model.getMetadata(), metadata.getClass());
            } catch (Throwable ex) {
            }
        }
        return new ServiceInstance(model.getId(), model.getServiceId(), model.getInstanceId(), model.getIp(), model.getMachineName(), metadata, model.getPort(),
                model.getProtocol(), model.getRegionId(), model.getZoneId(), model.getHealthCheckUrl(), model.getUrl(), model.getDescription(),
                model.getGroupId());
    }

    public static ServiceInstanceModel newServiceInstanceModel(ServiceInstance serviceInstance) {
        String metadata = StringValues.EMPTY;
        if (serviceInstance.getMetadata() != null) {
            metadata = JacksonJsonSerializer.INSTANCE.serialize(serviceInstance.getMetadata());
        }
        return new ServiceInstanceModel(serviceInstance.getServiceId(), serviceInstance.getInstanceId(), serviceInstance.getIp(),
                serviceInstance.getMachineName(), metadata, serviceInstance.getPort(), serviceInstance.getProtocol(), serviceInstance.getRegionId(),
                serviceInstance.getZoneId(), serviceInstance.getHealthCheckUrl(), serviceInstance.getUrl(), serviceInstance.getDescription(),
                serviceInstance.getGroupId());
    }

    public static List<ServiceInstanceModel> newServiceInstanceModels(List<ServiceInstance> groupInstances) {
        return Converts.convert(groupInstances, new Func1<ServiceInstance, ServiceInstanceModel>() {
            @Override
            public ServiceInstanceModel execute(ServiceInstance ServiceInstance) {
                return newServiceInstanceModel(ServiceInstance);
            }
        });
    }

    public static List<ServiceInstance> newServiceInstances(List<ServiceInstanceModel> models) {
        return Converts.convert(models, new Func1<ServiceInstanceModel, ServiceInstance>() {
            @Override
            public ServiceInstance execute(ServiceInstanceModel model) {
                return newServiceInstance(model);
            }
        });
    }

    public static List<ServiceInstanceLogModel> newServiceInstanceLogModels(OperationContext operationContext, List<ServiceInstanceModel> groupInstances) {
        List<ServiceInstanceLogModel> logs = Lists.newArrayList();
        if (operationContext == null || CollectionValues.isNullOrEmpty(groupInstances)) {
            return logs;
        }
        for (ServiceInstanceModel ServiceInstance : groupInstances) {
            if (ServiceInstance == null) {
                continue;
            }
            logs.add(new ServiceInstanceLogModel(operationContext, ServiceInstance));
        }

        return logs;
    }
}
