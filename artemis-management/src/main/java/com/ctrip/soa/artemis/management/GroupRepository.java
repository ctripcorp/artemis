package com.ctrip.soa.artemis.management;

import com.ctrip.soa.artemis.*;
import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.management.common.OperationContext;
import com.ctrip.soa.artemis.management.group.*;
import com.ctrip.soa.artemis.management.group.dao.*;
import com.ctrip.soa.artemis.management.group.model.*;
import com.ctrip.soa.artemis.management.group.util.*;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.artemis.util.SearchTree;
import com.ctrip.soa.artemis.registry.RegistryRepository;
import com.ctrip.soa.artemis.util.InstanceChanges;
import com.ctrip.soa.artemis.util.ServiceGroupKeys;
import com.ctrip.soa.artemis.util.ServiceGroups;
import com.ctrip.soa.caravan.common.concurrent.Threads;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThread;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThreadConfig;
import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupRepository {
    public interface Operation {
        String CREATE = "create";
        String DELETE = "delete";
        String UPDATE = "update";
    }

    public interface GroupStatus {
        String ACTIVE = "active";
        String INACTIVE = "inactive";
    }

    public interface RouteRuleStatus {
        String ACTIVE = "active";
        String INACTIVE = "inactive";
    }

    private static final Logger logger = LoggerFactory.getLogger(GroupRepository.class);
    private static volatile GroupRepository instance;

    public static GroupRepository getInstance() {
        if (instance == null) {
            synchronized (GroupRepository.class) {
                if (instance == null)
                    instance = new GroupRepository();
            }
        }

        return instance;
    }

    private final RouteRuleDao routeRuleDao = RouteRuleDao.INSTANCE;
    private final RouteRuleGroupDao routeRuleGroupDao = RouteRuleGroupDao.INSTANCE;
    private final GroupDao groupDao = GroupDao.INSTANCE;
    private final GroupOperationDao groupOperationDao = GroupOperationDao.INSTANCE;
    private final GroupTagDao groupTagDao = GroupTagDao.INSTANCE;
    private final GroupInstanceDao groupInstanceDao = GroupInstanceDao.INSTANCE;
    private final BusinessDao businessDao = BusinessDao.INSTANCE;
    private final ServiceInstanceDao serviceInstanceDao = ServiceInstanceDao.INSTANCE;

    private final RegistryRepository registryRepository = RegistryRepository.getInstance();
    private final DynamicScheduledThread cacheRefresher;
    private final TypedProperty<Integer> managementDBSyncWaitTimeProperty = ArtemisConfig.properties().getIntProperty("artemis.management.db-sync.wait-time",
            2 * 1000, 0, 60 * 1000);
    private volatile long lastRefreshTime;
    private volatile boolean lastRefreshSuccess;

    private volatile ListMultimap<String, RouteRule> routeRules = ArrayListMultimap.create();

    private volatile Map<Long, ServiceRouteRule> serviceRouteRules = Maps.newHashMap();
    private volatile Map<Long, RouteRuleGroup> serviceRouteRuleGroups = Maps.newHashMap();
    private volatile Map<Long, Group> serviceGroups = Maps.newHashMap();
    private volatile Map<Long, GroupOperations> serviceGroupOperations = Maps.newHashMap();
    private volatile Map<Long, GroupTags> serviceGroupTags = Maps.newHashMap();
    private volatile ListMultimap<String, Instance> serviceInstances = ArrayListMultimap.create();
    private volatile ListMultimap<String, ServiceGroup> serviceInstanceGroups = ArrayListMultimap.create();

    private volatile SearchTree<String, GroupOperations> operationsSearchTree = new SearchTree<>();

    private GroupRepository() {
        DynamicScheduledThreadConfig dynamicScheduledThreadConfig = new DynamicScheduledThreadConfig(ArtemisConfig.properties(),
                new RangePropertyConfig<Integer>(0, 0, 10 * 1000), new RangePropertyConfig<Integer>(5 * 1000, 10, 60 * 1000));
        final String cacheRefreshKey = "artemis.management.group.data.cache-refresher";
        cacheRefresher = new DynamicScheduledThread(cacheRefreshKey, new Runnable() {
            @Override
            public void run() {
                lastRefreshTime = System.currentTimeMillis();
                lastRefreshSuccess = ArtemisTraceExecutor.INSTANCE.execute(cacheRefreshKey, new Func<Boolean>() {
                    @Override
                    public Boolean execute() {
                        return refreshCache();
                    }
                });
            }
        }, dynamicScheduledThreadConfig);
        cacheRefresher.setDaemon(true);
        cacheRefresher.start();
    }

    public boolean isInstanceDown(Instance instance) {
        return operationsSearchTree.first(ServiceGroupKeys.toGroupIdList(ServiceGroupKeys.of(instance))) != null;
    }

    public List<RouteRule> getServiceRouteRules(final String serviceId, final String regionId) {
        return getServiceRouteRules(serviceId);
    }

    public List<RouteRule> getServiceRouteRules(final String serviceId) {
        if (StringValues.isNullOrWhitespace(serviceId)) {
            return Lists.newArrayList();
        }
        return routeRules.get(serviceId);
    }

    public List<ServiceRouteRule> getAllRouteRules(String regionId) {
        return Lists.newArrayList(serviceRouteRules.values());
    }

    public List<Instance> getServiceInstances(String serviceId) {
        if (StringValues.isNullOrWhitespace(serviceId)) {
            return Lists.newArrayList();
        }
        return serviceInstances.get(serviceId);
    }

    public Map<InstanceKey, Instance> getLogicalInstances() {
        Map<InstanceKey, Instance> instances = Maps.newHashMap();
        for (Instance instance : serviceInstances.values()) {
            instances.put(InstanceKey.of(instance), instance);
        }
        return instances;
    }

    public List<ServiceGroup> getServiceInstanceGroups(String serviceId) {
        if (StringValues.isNullOrWhitespace(serviceId)) {
            return Lists.newArrayList();
        }
        return serviceInstanceGroups.get(serviceId);
    }

    public List<ServiceRouteRule> getRouteRules(RouteRuleModel filter) {
        return ServiceRouteRules.newServiceRouteRules(routeRuleDao.select(filter));
    }

    public void insertRouteRules(OperationContext operationContext, List<RouteRuleModel> models) {
        businessDao.insertOrUpdateRouteRules(operationContext, models);
    }

    public void updateRouteRules(OperationContext operationContext, List<RouteRuleModel> models) {
        businessDao.insertOrUpdateRouteRules(operationContext, models);
    }

    public void deleteRouteRules(OperationContext operationContext, List<Long> routeRuleIds) {
        businessDao.deleteRouteRules(operationContext, routeRuleIds);
    }

    public List<RouteRuleGroup> getAllRouteRuleGroups(String regionId) {
        return Lists.newArrayList(serviceRouteRuleGroups.values());
    }

    public List<RouteRuleGroup> getRouteRuleGroups(RouteRuleGroupModel filter) {
        return RouteRuleGroups.newRouteRuleGroups(routeRuleGroupDao.select(filter));
    }

    public void insertServiceRouteRuleGroups(OperationContext operationContext, List<RouteRuleGroupModel> models) {
        businessDao.insertOrUpdateRouteRuleGroups(operationContext, models);
    }

    public void updateServiceRouteRuleGroups(OperationContext operationContext, List<RouteRuleGroupModel> models) {
        businessDao.insertOrUpdateRouteRuleGroups(operationContext, models);
    }

    public void deleteServiceRouteRuleGroups(OperationContext operationContext, List<Long> routeRuleGroupIds) {
        businessDao.deleteRouteRuleGroups(operationContext, routeRuleGroupIds);
    }

    public void releaseServiceRouteRuleGroups(OperationContext operationContext, List<RouteRuleGroupModel> models) {
        businessDao.releaseRouteRuleGroups(operationContext, models);
    }

    public void publishServiceRouteRuleGroups(OperationContext operationContext, List<RouteRuleGroupModel> models) {
        businessDao.publishRouteRuleGroups(operationContext, models);
    }

    public List<Group> getAllGroups(String regionId) {
        return Lists.newArrayList(serviceGroups.values());
    }

    public List<Group> getGroups(GroupModel filter) {
        return Groups.newGroups(groupDao.select(filter));
    }

    public void insertGroups(OperationContext operationContext, List<GroupModel> groupModels) {
        businessDao.insertOrUpdateGroups(operationContext, groupModels);
    }

    public void updateGroups(OperationContext operationContext, List<GroupModel> groupModels) {
        businessDao.insertOrUpdateGroups(operationContext, groupModels);
    }

    public void deleteGroups(OperationContext operationContext, List<Long> groupIds) {
        businessDao.deleteGroups(operationContext, groupIds);
    }

    public List<GroupOperations> getAllGroupOperations(String regionId) {
        return Lists.newArrayList(serviceGroupOperations.values());
    }

    public GroupOperations getGroupOperations(Long groupId) {
        return serviceGroupOperations.get(groupId);
    }

    public void operateGroupOperations(OperationContext operationContext, List<GroupOperationModel> groupOperations, boolean isOperationComplete) {
        if (CollectionValues.isNullOrEmpty(groupOperations)) {
            return;
        }
        if (isOperationComplete) {
            businessDao.deleteGroupOperation(operationContext, groupOperations);
        } else {
            businessDao.insertOrUpdateGroupOperation(operationContext, groupOperations);
        }
    }

    public List<GroupTags> getAllGroupTags(String regionId) {
        return Lists.newArrayList(serviceGroupTags.values());
    }

    public GroupTags getGroupTags(Long groupId) {
        return serviceGroupTags.get(groupId);
    }

    public void insertGroupTags(List<GroupTagModel> tags) {
        groupTagDao.insertOrUpdate(tags);
    }

    public void updateGroupTags(List<GroupTagModel> tags) {
        groupTagDao.insertOrUpdate(tags);
    }

    public void deleteGroupTags(GroupTagModel filter) {
        groupTagDao.delete(filter);
    }

    public void createRouteRules(OperationContext operationContext, String serviceId, List<RouteRuleInfo> routeRuleInfos) {
        businessDao.createServiceRouteRules(operationContext, serviceId, routeRuleInfos);
    }

    public void activateRouteRules(OperationContext operationContext, String serviceId, List<RouteRuleInfo> routeRuleInfos) {
        businessDao.activateServiceRouteRules(operationContext, serviceId, routeRuleInfos);
    }

    public void operateGroupOperation(OperationContext operationContext, GroupModel groupModel, boolean isOperationComplete) {
        businessDao.operationGroupOperation(operationContext, groupModel, isOperationComplete);
    }

    public List<GroupInstance> getGroupInstances(GroupInstanceModel filter) {
        return GroupInstances.newGroupInstances(groupInstanceDao.select(filter));
    }

    public void deleteGroupInstances(OperationContext operationContext, List<Long> groupInstanceIds) {
        businessDao.deleteGroupInstances(operationContext, groupInstanceIds);
    }

    public void deleteGroupInstancesByFilter(OperationContext operationContext, List<GroupInstanceModel> groupInstances) {
        businessDao.deleteGroupInstancesByFilter(operationContext, groupInstances);
    }

    public void insertGroupInstances(OperationContext operationContext, List<GroupInstanceModel> groupInstanceModels) {
        businessDao.insertGroupInstances(operationContext, groupInstanceModels);
    }

    public List<ServiceInstance> getServiceInstances(ServiceInstanceModel filter) {
        return ServiceInstances.newServiceInstances(serviceInstanceDao.select(filter));
    }

    public void insertServiceInstances(OperationContext operationContext, List<ServiceInstanceModel> serviceInstances) {
        businessDao.insertServiceInstances(operationContext, serviceInstances);
    }

    public void deleteServiceInstances(OperationContext operationContext, List<Long> serviceInstanceIds) {
        businessDao.deleteServiceInstances(operationContext, serviceInstanceIds);
    }

    public void deleteServiceInstancesByFilter(OperationContext operationContext, List<ServiceInstanceModel> serviceInstances) {
        businessDao.deleteServiceInstancesByFilter(operationContext, serviceInstances);
    }

    public Group generateGroup(GroupModel groupModel) {
        return Groups.newGroup(businessDao.generateGroup(groupModel));
    }

    public ServiceRouteRule generateRouteRule(RouteRuleModel routeRuleModel) {
        return ServiceRouteRules.newServiceRouteRule(businessDao.generateRouteRule(routeRuleModel));
    }

    public RouteRuleGroup generateRouteRuleGroup(RouteRuleGroupModel routeRuleGroupModel) {
        return RouteRuleGroups.newRouteRuleGroup(routeRuleGroupDao.generateGroup(routeRuleGroupModel));
    }

    public void updateGroupInstances(OperationContext operationContext, Long groupId, Set<String> instanceIds) {
        businessDao.updateGroupInstance(operationContext, groupId, instanceIds);
    }

    public boolean isLastRefreshSuccess() {
        return lastRefreshSuccess;
    }

    public long lastRefreshTime() {
        return lastRefreshTime;
    }

    public void waitForPeerSync() {
        Threads.sleep(managementDBSyncWaitTimeProperty.typedValue());
    }

    protected boolean refreshCache() {
        Set<String> changedServices;

        try {
            changedServices = refreshServiceRouteRulesCache();
        } catch (Throwable ex) {
            logger.error("service route-rules cache refresh failed", ex);
            return false;
        }

        try {
            changedServices.addAll(refreshServiceInstancesCache());
        } catch (Throwable ex) {
            logger.error("service instances cache refresh failed", ex);
            return false;
        }

        Set<Long> changeGroups;
        try {
            changeGroups = refreshGroupOperationsCache();
        } catch (Throwable ex) {
            logger.error("group operations cache refresh failed", ex);
            return false;
        }
        SearchTree<String, GroupOperations> newOperationsSearchTree = new SearchTree<>();
        for (Long id : serviceGroupOperations.keySet()) {
            Group group = serviceGroups.get(id);
            if (group == null) {
                continue;
            }

            newOperationsSearchTree.add(ServiceGroupKeys.toGroupIdList(group.getGroupKey()), serviceGroupOperations.get(id));
        }
        operationsSearchTree = newOperationsSearchTree;

        for (Long groupId : changeGroups) {
            Group group = serviceGroups.get(groupId);
            if (group == null) {
                continue;
            }
            changedServices.add(group.getServiceId());
        }

        for (String serviceKey : changedServices) {
            registryRepository.addInstanceChange(InstanceChanges.newReloadInstanceChange(serviceKey));
        }

        return true;
    }

    protected Set<String> refreshServiceRouteRulesCache() {
        ListMultimap<String, RouteRule> newRouteRules = ArrayListMultimap.create();
        ListMultimap<Long, RouteRuleGroup> newRouteRuleGroups = ArrayListMultimap.create();

        Map<Long, ServiceRouteRule> newServiceRouteRules = Maps.newHashMap();
        Map<Long, RouteRuleGroup> newServiceRouteRuleGroups = getRouteRuleGroups();
        Map<Long, Group> newServiceGroups = getServiceGroups();
        Map<Long, GroupTags> newServiceGroupTags = getServiceGroupTags();
        ListMultimap<Long, String> newGroupInstances = getServiceGroupInstances();
        ListMultimap<String, ServiceGroup> newServiceInstanceGroups = ArrayListMultimap.create();
        for (Group group : newServiceGroups.values()) {
            GroupTags tags = newServiceGroupTags.get(group.getGroupId());
            List<String> instanceIds = newGroupInstances.get(group.getGroupId());
            ServiceGroup serviceGroup = new ServiceGroup(group.getGroupKey(), null);
            if (tags != null) {
                serviceGroup.setMetadata(tags.getTags());
            }
            if (instanceIds != null) {
                serviceGroup.setInstanceIds(instanceIds);
            }
            newServiceInstanceGroups.put(group.getServiceId(), serviceGroup);
        }

        for (RouteRuleGroup routeRuleGroup : newServiceRouteRuleGroups.values()) {
            newRouteRuleGroups.put(routeRuleGroup.getRouteRuleId(), routeRuleGroup);
        }

        List<RouteRuleModel> models = routeRuleDao.query();
        for (RouteRuleModel model : models) {
            newServiceRouteRules.put(model.getId(), ServiceRouteRules.newServiceRouteRule(model));
            if (!RouteRuleStatus.ACTIVE.equalsIgnoreCase(model.getStatus())) {
                continue;
            }
            RouteRule routeRule = new RouteRule(model.getName(), model.getStrategy());
            List<ServiceGroup> serviceGroups = Lists.newArrayList();
            routeRule.setGroups(serviceGroups);
            for (RouteRuleGroup routeRuleGroup : newRouteRuleGroups.get(model.getId())) {
                Group group = newServiceGroups.get(routeRuleGroup.getGroupId());
                GroupTags tags = newServiceGroupTags.get(routeRuleGroup.getGroupId());
                List<String> instanceIds = newGroupInstances.get(routeRuleGroup.getGroupId());
                if (group == null || !GroupStatus.ACTIVE.equalsIgnoreCase(group.getStatus())) {
                    continue;
                }

                ServiceGroup serviceGroup = new ServiceGroup(group.getGroupKey(), ServiceGroups.fixWeight(routeRuleGroup.getWeight()));
                if (tags != null) {
                    serviceGroup.setMetadata(tags.getTags());
                }
                if (instanceIds != null) {
                    serviceGroup.setInstanceIds(instanceIds);
                }
                serviceGroups.add(serviceGroup);
            }
            if (CollectionValues.isNullOrEmpty(serviceGroups)) {
                continue;
            }
            newRouteRules.put(model.getServiceId(), routeRule);
        }

        ListMultimap<String, RouteRule> oldRouteRules = routeRules;
        routeRules = newRouteRules;
        serviceRouteRules = newServiceRouteRules;
        serviceRouteRuleGroups = newServiceRouteRuleGroups;
        serviceGroups = newServiceGroups;
        serviceGroupTags = newServiceGroupTags;
        serviceInstanceGroups = newServiceInstanceGroups;
        return generateServiceChanges(routeRules, oldRouteRules);
    }

    protected Set<String> refreshServiceInstancesCache() {
        ListMultimap<String, Instance> newInstances = getServiceInstances();
        ListMultimap<String, Instance> oldInstances = serviceInstances;
        serviceInstances = newInstances;

        Set<String> changeServices = Sets.newHashSet();
        for (String serviceId : newInstances.keySet()) {
            if (!oldInstances.containsKey(serviceId)) {
                changeServices.add(serviceId);
            }

            Set<String> newInstanceIds = generateServiceInstanceIds(newInstances.get(serviceId));
            Set<String> oldInstanceIds = generateServiceInstanceIds(oldInstances.get(serviceId));
            if (newInstanceIds.size() != oldInstanceIds.size() || !newInstanceIds.containsAll(oldInstanceIds)) {
                changeServices.add(serviceId);
            }
        }

        for (String serviceId : oldInstances.keySet()) {
            if (!newInstances.containsKey(serviceId)) {
                changeServices.add(serviceId);
            }
        }

        return changeServices;
    }

    protected Map<Long, RouteRuleGroup> getRouteRuleGroups() {
        Map<Long, RouteRuleGroup> newRouteRuleGroups = Maps.newHashMap();
        for (RouteRuleGroupModel model : routeRuleGroupDao.query()) {
            newRouteRuleGroups.put(model.getId(), RouteRuleGroups.newRouteRuleGroup(model));
        }
        return newRouteRuleGroups;
    }

    protected Map<Long, Group> getServiceGroups() {
        Map<Long, Group> newServiceGroups = Maps.newHashMap();
        for (GroupModel model : groupDao.query()) {
            newServiceGroups.put(model.getId(), Groups.newGroup(model));
        }
        return newServiceGroups;
    }

    protected Map<Long, GroupTags> getServiceGroupTags() {
        Map<Long, GroupTags> newGroupTags = Maps.newHashMap();
        ListMultimap<Long, GroupTagModel> m = ArrayListMultimap.create();
        for (GroupTagModel model : groupTagDao.query()) {
            m.put(model.getGroupId(), model);
        }

        for (Long id : m.keySet()) {
            Map<String, String> tags = Maps.newHashMap();
            for (GroupTagModel tag : m.get(id)) {
                tags.put(tag.getTag(), tag.getValue());
            }
            newGroupTags.put(id, new GroupTags(id, tags));
        }
        return newGroupTags;
    }

    protected ListMultimap<Long, String> getServiceGroupInstances() {
        ListMultimap<Long, String> newGroupInstances = ArrayListMultimap.create();

        for (GroupInstanceModel model : groupInstanceDao.query()) {
            newGroupInstances.put(model.getGroupId(), model.getInstanceId());
        }

        return newGroupInstances;
    }

    @SuppressWarnings("unchecked")
    protected ListMultimap<String, Instance> getServiceInstances() {
        ListMultimap<String, Instance> newServiceInstances = ArrayListMultimap.create();
        for (ServiceInstanceModel model : serviceInstanceDao.query()) {
            Instance instance = new Instance();
            instance.setServiceId(model.getServiceId());
            instance.setInstanceId(model.getInstanceId());
            instance.setIp(model.getIp());
            instance.setMachineName(model.getMachineName());
            instance.setPort(model.getPort());
            instance.setProtocol(model.getProtocol());
            instance.setHealthCheckUrl(model.getHealthCheckUrl());
            instance.setRegionId(model.getRegionId());
            instance.setZoneId(model.getZoneId());
            instance.setGroupId(model.getGroupId());
            Map<String, String> metadata = Maps.newHashMap();
            if (!StringValues.isNullOrWhitespace(model.getMetadata())) {
                try {
                    metadata = JacksonJsonSerializer.INSTANCE.deserialize(model.getMetadata(), Map.class);
                } catch (Throwable ex) {
                }
            }
            instance.setMetadata(metadata);
            instance.setStatus(Instance.Status.UP);
            instance.setUrl(model.getUrl());
            newServiceInstances.put(instance.getServiceId(), instance);
        }
        return newServiceInstances;
    }

    protected Set<String> generateServiceChanges(ListMultimap<String, RouteRule> newValues, ListMultimap<String, RouteRule> oldValues) {
        Set<String> changeServices = Sets.newHashSet();
        for (String serviceId : newValues.keySet()) {
            if (!oldValues.containsKey(serviceId)) {
                changeServices.add(serviceId);
            }

            Set<String> newRouteRuleGroupKeys = generateRouteRuleGroupKeys(newValues.get(serviceId));
            Set<String> oldRouteRuleGroupKeys = generateRouteRuleGroupKeys(oldValues.get(serviceId));
            if (newRouteRuleGroupKeys.size() != oldRouteRuleGroupKeys.size() || !newRouteRuleGroupKeys.containsAll(oldRouteRuleGroupKeys)) {
                changeServices.add(serviceId);
            }
        }

        for (String serviceId : oldValues.keySet()) {
            if (!newValues.containsKey(serviceId)) {
                changeServices.add(serviceId);
            }
        }

        return changeServices;
    }

    protected Set<String> generateRouteRuleGroupKeys(List<RouteRule> routeRules) {
        Set<String> routeRuleGroupKeys = Sets.newHashSet();
        for (RouteRule routeRule : routeRules) {
            routeRuleGroupKeys.add(routeRule.getRouteId() + "/" + routeRule.getStrategy());
            for (ServiceGroup group : routeRule.getGroups()) {
                if (CollectionValues.isNullOrEmpty(group.getInstanceIds())) {
                    routeRuleGroupKeys.add(routeRule.getRouteId() + "/" + group.getGroupKey() + "/" + group.getWeight());
                } else {
                    for (String instanceId : group.getInstanceIds()) {
                        routeRuleGroupKeys.add(routeRule.getRouteId() + "/" + group.getGroupKey() + "/" + group.getWeight() + "/" + instanceId);
                    }
                }
            }
        }
        return routeRuleGroupKeys;
    }

    protected Set<String> generateServiceInstanceIds(List<Instance> instances) {
        Set<String> instanceIds = Sets.newHashSet();
        if (CollectionValues.isNullOrEmpty(instances)) {
            return instanceIds;
        }
        for (Instance instance : instances) {
            if (instance == null) {
                continue;
            }
            instanceIds.add(instance.getRegionId() + "/" + instance.getZoneId() + "/" + instance.getGroupId() + "/" + instance.getServiceId() + "/"
                    + instance.getInstanceId() + "/" + instance.getMachineName() + "/" + instance.getIp() + "/" + instance.getPort() + "/"
                    + instance.getProtocol() + "/" + instance.getUrl() + "/" + instance.getHealthCheckUrl() + "/"
                    + JacksonJsonSerializer.INSTANCE.serialize(instance.getMetadata()));
        }
        return instanceIds;
    }

    protected Set<Long> refreshGroupOperationsCache() {
        Map<Long, GroupOperations> newServiceGroupOperations = Maps.newHashMap();
        ListMultimap<Long, String> m = ArrayListMultimap.create();
        for (GroupOperationModel model : groupOperationDao.query()) {
            m.put(model.getGroupId(), model.getOperation());
        }

        for (Long id : m.keySet()) {
            GroupOperations groupOperations = new GroupOperations(id, m.get(id));
            newServiceGroupOperations.put(id, groupOperations);
        }

        Set<Long> oldKeys = serviceGroupOperations.keySet();
        serviceGroupOperations = newServiceGroupOperations;
        return diff(oldKeys, serviceGroupOperations.keySet());
    }

    protected Set<Long> diff(Set<Long> key1, Set<Long> key2) {
        Set<Long> diffs = Sets.newHashSet(Sets.difference(key1, key2));
        diffs.addAll(Sets.difference(key2, key1));
        return diffs;
    }

    protected void stopRefresh() {
        cacheRefresher.shutdown();
    }
}
