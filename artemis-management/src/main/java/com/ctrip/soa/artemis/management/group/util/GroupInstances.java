package com.ctrip.soa.artemis.management.group.util;

import com.ctrip.soa.artemis.management.common.OperationContext;
import com.ctrip.soa.artemis.management.group.GroupInstance;
import com.ctrip.soa.artemis.management.group.model.GroupInstanceLogModel;
import com.ctrip.soa.artemis.management.group.model.GroupInstanceModel;
import com.ctrip.soa.caravan.common.delegate.Func1;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupInstances {
    public static GroupInstance newGroupInstance(GroupInstanceModel model) {
        return new GroupInstance(model.getId(), model.getGroupId(), model.getInstanceId());
    }

    public static GroupInstanceModel newGroupInstanceModel(GroupInstance groupInstance) {
        return new GroupInstanceModel(groupInstance.getGroupId(), groupInstance.getInstanceId());
    }

    public static List<GroupInstanceModel> newGroupInstanceModels(List<GroupInstance> groupInstances) {
        return Converts.convert(groupInstances, new Func1<GroupInstance, GroupInstanceModel>() {
            @Override
            public GroupInstanceModel execute(GroupInstance groupInstance) {
                return newGroupInstanceModel(groupInstance);
            }
        });
    }

    public static List<GroupInstance> newGroupInstances(List<GroupInstanceModel> models) {
        return Converts.convert(models, new Func1<GroupInstanceModel, GroupInstance>() {
            @Override
            public GroupInstance execute(GroupInstanceModel model) {
                return newGroupInstance(model);
            }
        });
    }

    public static List<GroupInstanceLogModel> newGroupLogModels(OperationContext operationContext, List<GroupInstanceModel> groupInstances) {
        List<GroupInstanceLogModel> logs = Lists.newArrayList();
        if (operationContext == null || CollectionValues.isNullOrEmpty(groupInstances)) {
            return logs;
        }
        for (GroupInstanceModel groupInstance : groupInstances) {
            if (groupInstance == null) {
                continue;
            }
            logs.add(new GroupInstanceLogModel(operationContext, groupInstance));
        }

        return logs;
    }

}
