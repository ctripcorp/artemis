package com.ctrip.soa.artemis.management.group.util;

import com.ctrip.soa.artemis.management.common.OperationContext;
import com.ctrip.soa.artemis.management.group.RouteRuleGroup;
import com.ctrip.soa.artemis.management.group.model.RouteRuleGroupLogModel;
import com.ctrip.soa.artemis.management.group.model.RouteRuleGroupModel;
import com.ctrip.soa.caravan.common.delegate.Func1;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class RouteRuleGroups {
    public static RouteRuleGroupModel newRouteRuleGroupModel(RouteRuleGroup routeRuleGroup) {
        return new RouteRuleGroupModel(routeRuleGroup.getRouteRuleId(), routeRuleGroup.getGroupId(), routeRuleGroup.getUnreleasedWeight());
    }

    public static RouteRuleGroup newRouteRuleGroup(RouteRuleGroupModel model) {
        return new RouteRuleGroup(model.getId(), model.getRouteRuleId(), model.getGroupId(), model.getWeight(), model.getUnreleasedWeight());
    }

    public static List<RouteRuleGroupModel> newRouteRuleGroupModels(List<RouteRuleGroup> routeRuleGroups) {
        return Converts.convert(routeRuleGroups, new Func1<RouteRuleGroup, RouteRuleGroupModel>() {
            @Override
            public RouteRuleGroupModel execute(RouteRuleGroup routeRuleGroup) {
                return newRouteRuleGroupModel(routeRuleGroup);
            }
        });
    }

    public static List<RouteRuleGroup> newRouteRuleGroups(List<RouteRuleGroupModel> models) {
        return Converts.convert(models, new Func1<RouteRuleGroupModel, RouteRuleGroup>() {
            @Override
            public RouteRuleGroup execute(RouteRuleGroupModel model) {
                return newRouteRuleGroup(model);
            }
        });
    }

    public static List<RouteRuleGroupLogModel> newRouteRuleGroupLogs(OperationContext operationContext,List<RouteRuleGroupModel> models) {
        List<RouteRuleGroupLogModel> logs = Lists.newArrayList();
        if (operationContext == null || CollectionValues.isNullOrEmpty(models)) {
            return logs;
        }
        for (RouteRuleGroupModel model : models) {
            if (model == null) {
                continue;
            }
            logs.add(new RouteRuleGroupLogModel(model, operationContext));
        }
        return logs;
    }


}
