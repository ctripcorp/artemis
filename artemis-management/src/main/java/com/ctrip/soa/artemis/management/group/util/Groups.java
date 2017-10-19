package com.ctrip.soa.artemis.management.group.util;

import com.ctrip.soa.artemis.management.common.OperationContext;
import com.ctrip.soa.artemis.management.group.Group;
import com.ctrip.soa.artemis.management.group.model.GroupLogModel;
import com.ctrip.soa.artemis.management.group.model.GroupModel;
import com.ctrip.soa.caravan.common.delegate.Func1;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class Groups {
    public static Group newGroup(GroupModel model) {
        return new Group(model.getId(), model.getServiceId(), model.getRegionId(), model.getZoneId(), model.getName(), model.getAppId(),
                model.getDescription(), model.getStatus(), null);
    }

    public static GroupModel newGroupModel(Group group) {
        return new GroupModel(group.getServiceId(), group.getRegionId(), group.getZoneId(), group.getName(), group.getAppId(),
                group.getDescription(), group.getStatus());
    }

    public static List<GroupModel> newGroupModels(List<Group> groups) {
        return Converts.convert(groups, new Func1<Group, GroupModel>() {
            @Override
            public GroupModel execute(Group group) {
                return newGroupModel(group);
            }
        });
    }

    public static List<Group> newGroups(List<GroupModel> models) {
        return Converts.convert(models, new Func1<GroupModel, Group>() {
            @Override
            public Group execute(GroupModel model) {
                return newGroup(model);
            }
        });
    }

    public static List<GroupLogModel> newGroupLogModels(OperationContext operationContext, List<GroupModel> groups) {
        List<GroupLogModel> logs = Lists.newArrayList();
        if (operationContext == null || CollectionValues.isNullOrEmpty(groups)) {
            return logs;
        }
        for (GroupModel group : groups) {
            if (group == null) {
                continue;
            }
            logs.add(new GroupLogModel(group, operationContext));
        }

        return logs;
    }
}
