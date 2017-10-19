package com.ctrip.soa.artemis.management.zone.util;

import com.ctrip.soa.artemis.management.common.OperationContext;
import com.ctrip.soa.artemis.management.zone.ZoneKey;
import com.ctrip.soa.artemis.management.zone.ZoneOperations;
import com.ctrip.soa.artemis.management.zone.model.ZoneOperationLogModel;
import com.ctrip.soa.artemis.management.zone.model.ZoneOperationModel;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ZoneOperationsUtil {
    public static List<ZoneOperationModel> newZoneOperationModels(ZoneOperations zoneOperations) {
        List<ZoneOperationModel> operations = Lists.newArrayList();
        if (zoneOperations == null || zoneOperations.getZoneKey() == null || CollectionValues.isNullOrEmpty(zoneOperations.getOperations())) {
            return operations;
        }

        ZoneKey zoneKey = zoneOperations.getZoneKey();
        for (String operation : zoneOperations.getOperations()) {
            if (StringValues.isNullOrWhitespace(operation)) {
                continue;
            }

            operations.add(new ZoneOperationModel(operation, zoneKey.getZoneId(), zoneKey.getServiceId(), zoneKey.getRegionId()));
        }

        return operations;
    }

    public static List<ZoneOperationModel> newZoneOperationModels(List<ZoneOperations> zoneOperationsList) {
        List<ZoneOperationModel> operations = Lists.newArrayList();
        if (CollectionValues.isNullOrEmpty(zoneOperationsList)) {
            return operations;
        }

        for (ZoneOperations zoneOperations : zoneOperationsList) {
            if (zoneOperations == null) {
                continue;
            }

            operations.addAll(newZoneOperationModels(zoneOperations));
        }

        return operations;
    }

    public static List<ZoneOperationLogModel> newZoneOperationLogModels(OperationContext operationContext, List<ZoneOperationModel> models,
            boolean isComplete) {
        List<ZoneOperationLogModel> logs = Lists.newArrayList();
        if (operationContext == null || CollectionValues.isNullOrEmpty(models)) {
            return logs;
        }
        for (ZoneOperationModel model : models) {
            if (model == null) {
                continue;
            }
            logs.add(new ZoneOperationLogModel(operationContext, model, isComplete));
        }

        return logs;
    }
}
