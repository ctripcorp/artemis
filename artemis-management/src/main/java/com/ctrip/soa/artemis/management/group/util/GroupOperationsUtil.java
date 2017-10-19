package com.ctrip.soa.artemis.management.group.util;

import com.ctrip.soa.artemis.management.common.OperationContext;
import com.ctrip.soa.artemis.management.group.GroupOperations;
import com.ctrip.soa.artemis.management.group.model.GroupOperationLogModel;
import com.ctrip.soa.artemis.management.group.model.GroupOperationModel;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupOperationsUtil {
    public static List<GroupOperationModel> newGroupOperationModels(GroupOperations groupOperations) {
        List<GroupOperationModel> operations = Lists.newArrayList();
        if (groupOperations == null || groupOperations.getGroupId() == null || CollectionValues.isNullOrEmpty(groupOperations.getOperations())) {
            return operations;
        }
        Long groupId = groupOperations.getGroupId();

        for (String operation : groupOperations.getOperations()) {
            if (StringValues.isNullOrWhitespace(operation)) {
                continue;
            }

            operations.add(new GroupOperationModel(groupId, operation));
        }

        return operations;
    }

    public static List<GroupOperationModel> newGroupOperationModels(List<GroupOperations> groupOperationsList) {
        List<GroupOperationModel> operations = Lists.newArrayList();
        if (CollectionValues.isNullOrEmpty(groupOperationsList)) {
            return operations;
        }

        for (GroupOperations groupOperations : groupOperationsList) {
            if (groupOperations == null) {
                continue;
            }

            operations.addAll(newGroupOperationModels(groupOperations));
        }

        return operations;
    }

    public static List<GroupOperationLogModel> newGroupOperationLogModels(OperationContext operationContext, List<GroupOperationModel> groupOperations, boolean complete) {
        List<GroupOperationLogModel> logs = Lists.newArrayList();
        if (operationContext == null || CollectionValues.isNullOrEmpty(groupOperations)) {
            return logs;
        }
        for (GroupOperationModel groupOperation : groupOperations) {
            logs.add(new GroupOperationLogModel(groupOperation, operationContext, complete));
        }
        return logs;
    }
}
