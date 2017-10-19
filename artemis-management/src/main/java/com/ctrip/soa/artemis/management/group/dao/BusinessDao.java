package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.GroupRepository;
import com.ctrip.soa.artemis.management.common.OperationContext;
import com.ctrip.soa.artemis.management.group.*;
import com.ctrip.soa.artemis.management.group.model.*;
import com.ctrip.soa.artemis.management.group.util.*;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.common.value.checker.ValueCheckers;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by fang_j on 10/07/2016.
 */
public class BusinessDao {
    public static final BusinessDao INSTANCE = new BusinessDao();

    private final RouteRuleDao routeRuleDao = RouteRuleDao.INSTANCE;
    private final GroupDao groupDao = GroupDao.INSTANCE;
    private final RouteRuleGroupDao routeRuleGroupDao = RouteRuleGroupDao.INSTANCE;
    private final GroupOperationDao groupOperationDao = GroupOperationDao.INSTANCE;
    private final GroupLogDao groupLogDao = GroupLogDao.INSTANCE;
    private final GroupOperationLogDao groupOperationLogDao = GroupOperationLogDao.INSTANCE;
    private final RouteRuleLogDao routeRuleLogDao = RouteRuleLogDao.INSTANCE;
    private final RouteRuleGroupLogDao routeRuleGroupLogDao = RouteRuleGroupLogDao.INSTANCE;
    private final GroupInstanceDao groupInstanceDao = GroupInstanceDao.INSTANCE;
    private final GroupInstanceLogDao groupInstanceLogDao = GroupInstanceLogDao.INSTANCE;
    private final ServiceInstanceDao serviceInstanceDao = ServiceInstanceDao.INSTANCE;
    private final ServiceInstanceLogDao serviceInstanceLogDao = ServiceInstanceLogDao.INSTANCE;

    @Transactional
    public void createServiceRouteRules(OperationContext operationContext, final String serviceId, List<RouteRuleInfo> routeRuleInfos) {
        checkServiceRouteRules(serviceId, routeRuleInfos);

        List<RouteRuleModel> routeRuleModels = Lists.newArrayList();
        Map<String, GroupModel> groupModels = Maps.newHashMap();
        ListMultimap<String, GroupWeight> routeRuleGroups = ArrayListMultimap.create();
        for (RouteRuleInfo info : routeRuleInfos) {
            routeRuleModels.add(ServiceRouteRules.newRouteRuleModel(info.getRouteRule()));
            for (GroupWeight group : info.getGroups()) {
                String groupKey = group.getGroupKey();
                groupModels.put(groupKey, Groups.newGroupModel(group));
                routeRuleGroups.put(info.getRouteRule().getName().toLowerCase(), group);
            }
        }

        insertRouteRules(operationContext, routeRuleModels);
        insertGroups(operationContext, Lists.newArrayList(groupModels.values()));
        insertOrUpdateRouteRuleGroups(operationContext, getRouteRuleGroups(serviceId, routeRuleGroups));
    }

    @Transactional
    public void activateServiceRouteRules(OperationContext operationContext, final String serviceId, List<RouteRuleInfo> routeRuleInfos) {
        checkServiceRouteRules(serviceId, routeRuleInfos);

        List<RouteRuleModel> routeRuleModels = Lists.newArrayList();
        Map<String, GroupModel> groupModels = Maps.newHashMap();
        ListMultimap<String, GroupWeight> routeRuleGroups = ArrayListMultimap.create();

        for (RouteRuleInfo info : routeRuleInfos) {
            RouteRuleModel routeRuleModel = ServiceRouteRules.newRouteRuleModel(info.getRouteRule());
            routeRuleModel.setStatus(GroupRepository.RouteRuleStatus.ACTIVE);
            routeRuleModels.add(routeRuleModel);
            for (GroupWeight group : info.getGroups()) {
                String groupKey = group.getGroupKey();
                GroupModel groupModel = Groups.newGroupModel(group);
                groupModel.setStatus(GroupRepository.GroupStatus.ACTIVE);
                groupModels.put(groupKey, groupModel);
                routeRuleGroups.put(info.getRouteRule().getName().toLowerCase(), group);
            }
        }

        /* inactivate unselected route-rule */
        RouteRuleModel routeRuleFilter = new RouteRuleModel();
        routeRuleFilter.setServiceId(serviceId);
        for (RouteRuleModel routeRule : routeRuleDao.select(routeRuleFilter)) {
            if (!routeRuleGroups.containsKey(routeRule.getName().toLowerCase())) {
                routeRule.setStatus(GroupRepository.RouteRuleStatus.INACTIVE);
                routeRuleModels.add(routeRule);
            }
        }

        /* inactive unselected group */
        GroupModel groupFilter = new GroupModel();
        groupFilter.setServiceId(serviceId);
        for (GroupModel groupModel : groupDao.select(groupFilter)) {
            String groupKey = Groups.newGroup(groupModel).getGroupKey();
            if (!groupModels.containsKey(groupKey)) {
                groupModel.setStatus(GroupRepository.GroupStatus.INACTIVE);
                groupModels.put(groupKey, groupModel);
            }
        }

        insertOrUpdateRouteRules(operationContext, routeRuleModels);
        insertOrUpdateGroups(operationContext, Lists.newArrayList(groupModels.values()));
        List<RouteRuleGroupModel> routeRuleGroupModels = getRouteRuleGroups(serviceId, routeRuleGroups);
        insertOrUpdateRouteRuleGroups(operationContext, routeRuleGroupModels);
        releaseRouteRuleGroups(operationContext, routeRuleGroupModels);
    }

    protected List<RouteRuleGroupModel> getRouteRuleGroups(final String serviceId, ListMultimap<String, GroupWeight> routeRuleGroups) {
        RouteRuleModel routeRuleFilter = new RouteRuleModel();
        routeRuleFilter.setServiceId(serviceId);
        List<RouteRuleModel> newRouteRules = routeRuleDao.select(routeRuleFilter);

        GroupModel groupFilter = new GroupModel();
        groupFilter.setServiceId(serviceId);
        Map<String, Long> newGroups = Maps.newHashMap();
        for (Group group : Groups.newGroups(groupDao.select(groupFilter))) {
            newGroups.put(group.getGroupKey(), group.getGroupId());
        }

        List<RouteRuleGroupModel> routeRuleGroupModels = Lists.newArrayList();
        for (RouteRuleModel routeRule : newRouteRules) {
            List<GroupWeight> groupWeights = routeRuleGroups.get(routeRule.getName().toLowerCase());
            if (CollectionValues.isNullOrEmpty(groupWeights)) {
                continue;
            }
            for (GroupWeight groupWeight : groupWeights) {
                final String groupKey = groupWeight.getGroupKey();
                routeRuleGroupModels.add(new RouteRuleGroupModel(routeRule.getId(), newGroups.get(groupKey), groupWeight.getWeight()));
            }
        }

        return routeRuleGroupModels;
    }

    @Transactional
    public void operationGroupOperation(OperationContext operationContext, GroupModel group, boolean isOperationComplete) {
        checkOperationContextArgument(operationContext);
        List<GroupOperationModel> groupOperation = Lists
                .newArrayList(new GroupOperationModel(groupDao.generateGroup(group).getId(), operationContext.getOperation()));
        if (isOperationComplete) {
            deleteGroupOperation(operationContext, groupOperation);
        } else {
            insertOrUpdateGroupOperation(operationContext, groupOperation);
        }
    }

    public RouteRuleModel generateRouteRule(RouteRuleModel routeRuleModel) {
        return routeRuleDao.generateRouteRule(routeRuleModel);
    }

    public GroupModel generateGroup(GroupModel groupModel) {
        return groupDao.generateGroup(groupModel);
    }

    @Transactional
    public void updateGroupInstance(OperationContext operationContext, Long groupId, Set<String> instanceIds) {
        checkOperationContextArgument(operationContext);
        List<GroupInstanceModel> newGroupInstances = Lists.newArrayList();
        List<Long> expireGroupInstanceIds = Lists.newArrayList();
        GroupInstanceModel filter = new GroupInstanceModel();
        filter.setGroupId(groupId);
        for (GroupInstanceModel groupInstanceModel : groupInstanceDao.select(filter)) {
            String instanceId = groupInstanceModel.getInstanceId();
            if (instanceIds.contains(instanceId)) {
                instanceIds.remove(instanceId);
            } else {
                expireGroupInstanceIds.add(groupInstanceModel.getId());
            }
        }
        for (String instanceId : instanceIds) {
            newGroupInstances.add(new GroupInstanceModel(groupId, instanceId));
        }
        if (!CollectionValues.isNullOrEmpty(expireGroupInstanceIds)) {
            deleteGroupInstances(operationContext, expireGroupInstanceIds);
        }
        if (!CollectionValues.isNullOrEmpty(newGroupInstances)) {
            insertGroupInstances(operationContext, newGroupInstances);
        }
    }

    public void deleteGroupOperation(OperationContext operationContext, List<GroupOperationModel> groupOperations) {
        checkOperationContextArgument(operationContext);
        groupOperationDao.delete(groupOperations);
        groupOperationLogDao.insert(GroupOperationsUtil.newGroupOperationLogModels(operationContext, groupOperations, true));
    }

    public void insertOrUpdateGroupOperation(OperationContext operationContext, List<GroupOperationModel> groupOperations) {
        checkOperationContextArgument(operationContext);
        groupOperationDao.insertOrUpdate(groupOperations);
        groupOperationLogDao.insert(GroupOperationsUtil.newGroupOperationLogModels(operationContext, groupOperations, false));
    }

    public void deleteGroups(OperationContext operationContext, List<Long> groupIds) {
        checkOperationContextArgument(operationContext);
        List<GroupModel> groups = groupDao.select(groupIds);
        groupDao.delete(groupIds);
        groupLogDao.insert(Groups.newGroupLogModels(operationContext, groups));
    }

    public void insertOrUpdateGroups(OperationContext operationContext, List<GroupModel> groups) {
        checkOperationContextArgument(operationContext);
        groupDao.insertOrUpdate(groups);
        groupLogDao.insert(Groups.newGroupLogModels(operationContext, groups));
    }

    public void insertGroups(OperationContext operationContext, List<GroupModel> groups) {
        checkOperationContextArgument(operationContext);
        groupDao.insert(groups);
        groupLogDao.insert(Groups.newGroupLogModels(operationContext, groups));
    }

    public void deleteRouteRules(OperationContext operationContext, List<Long> routeRuleIds) {
        checkOperationContextArgument(operationContext);
        List<RouteRuleModel> routeRules = routeRuleDao.select(routeRuleIds);
        routeRuleDao.delete(routeRuleIds);
        routeRuleLogDao.insert(ServiceRouteRules.newRouteRuleLogModels(operationContext, routeRules));
    }

    public void insertOrUpdateRouteRules(OperationContext operationContext, List<RouteRuleModel> routeRules) {
        checkOperationContextArgument(operationContext);
        routeRuleDao.insertOrUpdate(routeRules);
        routeRuleLogDao.insert(ServiceRouteRules.newRouteRuleLogModels(operationContext, routeRules));
    }

    public void insertRouteRules(OperationContext operationContext, List<RouteRuleModel> routeRules) {
        checkOperationContextArgument(operationContext);
        routeRuleDao.insert(routeRules);
        routeRuleLogDao.insert(ServiceRouteRules.newRouteRuleLogModels(operationContext, routeRules));
    }

    public void deleteRouteRuleGroups(OperationContext operationContext, List<Long> ids) {
        checkOperationContextArgument(operationContext);
        List<RouteRuleGroupModel> routeRuleGroups = routeRuleGroupDao.select(ids);
        routeRuleGroupDao.delete(ids);
        routeRuleGroupLogDao.insert(RouteRuleGroups.newRouteRuleGroupLogs(operationContext, routeRuleGroups));
    }

    public void insertOrUpdateRouteRuleGroups(OperationContext operationContext, List<RouteRuleGroupModel> routeRuleGroups) {
        checkOperationContextArgument(operationContext);
        routeRuleGroupDao.insertOrUpdate(routeRuleGroups);
        routeRuleGroupLogDao.insert(RouteRuleGroups.newRouteRuleGroupLogs(operationContext, routeRuleGroups));
    }

    public void releaseRouteRuleGroups(OperationContext operationContext, List<RouteRuleGroupModel> routeRuleGroups) {
        checkOperationContextArgument(operationContext);
        routeRuleGroupDao.release(routeRuleGroups);
        routeRuleGroupLogDao.insert(RouteRuleGroups.newRouteRuleGroupLogs(operationContext, routeRuleGroups));
    }

    public void publishRouteRuleGroups(OperationContext operationContext, List<RouteRuleGroupModel> routeRuleGroups) {
        checkOperationContextArgument(operationContext);
        routeRuleGroupDao.publish(routeRuleGroups);
        routeRuleGroupLogDao.insert(RouteRuleGroups.newRouteRuleGroupLogs(operationContext, routeRuleGroups));
    }

    public void deleteGroupInstances(OperationContext operationContext, List<Long> groupInstanceIds) {
        checkOperationContextArgument(operationContext);
        List<GroupInstanceModel> groupInstanceModels = groupInstanceDao.select(groupInstanceIds);
        groupInstanceDao.delete(groupInstanceIds);
        groupInstanceLogDao.insert(GroupInstances.newGroupLogModels(operationContext, groupInstanceModels));
    }

    public void deleteGroupInstancesByFilter(OperationContext operationContext, List<GroupInstanceModel> groupInstances) {
        checkOperationContextArgument(operationContext);
        groupInstanceDao.deleteByFilters(groupInstances);
        groupInstanceLogDao.insert(GroupInstances.newGroupLogModels(operationContext, groupInstances));
    }

    public void insertGroupInstances(OperationContext operationContext, List<GroupInstanceModel> groupInstanceModels) {
        checkOperationContextArgument(operationContext);
        groupInstanceDao.insert(groupInstanceModels);
        groupInstanceLogDao.insert(GroupInstances.newGroupLogModels(operationContext, groupInstanceModels));
    }

    public void deleteServiceInstances(OperationContext operationContext, List<Long> serviceInstanceIds) {
        checkOperationContextArgument(operationContext);
        List<ServiceInstanceModel> serviceInstanceModels = serviceInstanceDao.select(serviceInstanceIds);
        serviceInstanceDao.delete(serviceInstanceIds);
        serviceInstanceLogDao.insert(ServiceInstances.newServiceInstanceLogModels(operationContext, serviceInstanceModels));
    }

    public void deleteServiceInstancesByFilter(OperationContext operationContext, List<ServiceInstanceModel> serviceInstances) {
        checkOperationContextArgument(operationContext);
        serviceInstanceDao.deleteByFilters(serviceInstances);
        serviceInstanceLogDao.insert(ServiceInstances.newServiceInstanceLogModels(operationContext, serviceInstances));
    }

    public void insertServiceInstances(OperationContext operationContext, List<ServiceInstanceModel> serviceInstanceModels) {
        checkOperationContextArgument(operationContext);
        serviceInstanceDao.insertOrUpdate(serviceInstanceModels);
        serviceInstanceLogDao.insert(ServiceInstances.newServiceInstanceLogModels(operationContext, serviceInstanceModels));
    }

    protected void checkOperationContextArgument(OperationContext operationContext) {
        ValueCheckers.notNullOrWhiteSpace(operationContext.getOperation(), "operation");
        ValueCheckers.notNullOrWhiteSpace(operationContext.getExtensions(), "extensions");
        ValueCheckers.notNullOrWhiteSpace(operationContext.getOperatorId(), "operatorId");
        ValueCheckers.notNullOrWhiteSpace(operationContext.getToken(), "token");
    }

    protected void checkServiceRouteRules(String serviceId, List<RouteRuleInfo> routeRuleInfos) {
        ValueCheckers.notNullOrWhiteSpace("serviceId", serviceId);
        ValueCheckers.notNullOrEmpty(routeRuleInfos, "routeRules");
        for (RouteRuleInfo info : routeRuleInfos) {
            ServiceRouteRule routeRule = info.getRouteRule();
            ValueCheckers.notNull(routeRule, "routeRule");
            Preconditions.checkArgument(serviceId.equalsIgnoreCase(routeRule.getServiceId()), "routeRule.serviceId not equals to: " + serviceId);
            ValueCheckers.notNullOrWhiteSpace(routeRule.getName(), "routeRule.name");
            ValueCheckers.notNullOrWhiteSpace(routeRule.getStatus(), "routeRule.status");
            ValueCheckers.notNullOrEmpty(info.getGroups(), "groups");
            for (GroupWeight group : info.getGroups()) {
                ValueCheckers.notNull(group.getWeight(), "weight");
                ValueCheckers.notNull(group, "group");
                Preconditions.checkArgument(serviceId.equalsIgnoreCase(group.getServiceId()), "group.serviceId not equals to: " + serviceId);
                ValueCheckers.notNullOrWhiteSpace(group.getRegionId(), "group.regionId");
                ValueCheckers.notNullOrWhiteSpace(group.getZoneId(), "group.zoneId");
                ValueCheckers.notNullOrWhiteSpace(group.getName(), "group.name");
                ValueCheckers.notNullOrWhiteSpace(group.getAppId(), "group.appId");
                ValueCheckers.notNullOrWhiteSpace(group.getStatus(), "group.status");
            }
        }
    }
}
