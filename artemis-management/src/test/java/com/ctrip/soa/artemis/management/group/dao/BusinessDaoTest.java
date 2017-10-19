package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.GroupRepository;
import com.ctrip.soa.artemis.management.common.OperationContext;
import com.ctrip.soa.artemis.management.group.Group;
import com.ctrip.soa.artemis.management.group.GroupWeight;
import com.ctrip.soa.artemis.management.group.RouteRuleInfo;
import com.ctrip.soa.artemis.management.group.ServiceRouteRule;
import com.ctrip.soa.artemis.management.group.log.GroupOperationLog;
import com.ctrip.soa.artemis.management.group.model.*;
import com.ctrip.soa.artemis.management.group.util.Groups;
import com.google.common.collect.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

/**
 * Created by fang_j on 10/07/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
@Transactional
public class BusinessDaoTest {
    private final BusinessDao businessDao = BusinessDao.INSTANCE;
    private final RouteRuleDao routeRuleDao = RouteRuleDao.INSTANCE;
    private final GroupDao groupDao = GroupDao.INSTANCE;
    private final RouteRuleGroupDao routeRuleGroupDao = RouteRuleGroupDao.INSTANCE;
    private final GroupOperationDao groupOperationDao = GroupOperationDao.INSTANCE;
    private final GroupOperationLogDao groupOperationLogDao = GroupOperationLogDao.INSTANCE;

    @Test
    public void testCreateServiceRouteRules() {
        OperationContext operator = newOperationContext();
        String serviceId = new BigInteger(130, random).toString(32);
        List<RouteRuleInfo> routeRuleInfos = newRouteRuleInfos(serviceId);
        businessDao.createServiceRouteRules(operator, serviceId, routeRuleInfos);
        assertServiceRouteRule(serviceId, routeRuleInfos, null, null);
        businessDao.createServiceRouteRules(operator, serviceId, routeRuleInfos);
        assertServiceRouteRule(serviceId, routeRuleInfos, null, null);

        List<RouteRuleInfo> activeRouteRuleInfos = newRouteRuleInfos(serviceId);
        businessDao.createServiceRouteRules(operator, serviceId, activeRouteRuleInfos);
        assertServiceRouteRule(serviceId, routeRuleInfos, null, null);

        businessDao.activateServiceRouteRules(operator, serviceId, activeRouteRuleInfos);
        assertServiceRouteRule(serviceId, activeRouteRuleInfos, GroupRepository.RouteRuleStatus.ACTIVE, GroupRepository.GroupStatus.ACTIVE);
        assertServiceRouteRule(serviceId, routeRuleInfos, GroupRepository.RouteRuleStatus.INACTIVE, GroupRepository.GroupStatus.INACTIVE);

        businessDao.activateServiceRouteRules(operator, serviceId, activeRouteRuleInfos);
        assertServiceRouteRule(serviceId, activeRouteRuleInfos, GroupRepository.RouteRuleStatus.ACTIVE, GroupRepository.GroupStatus.ACTIVE);
        assertServiceRouteRule(serviceId, routeRuleInfos, GroupRepository.RouteRuleStatus.INACTIVE, GroupRepository.GroupStatus.INACTIVE);
    }

    @Test
    public void testOperateGroupOperation() {
        GroupModel group = newGroup();
        OperationContext operationContext = newOperationContext();
        businessDao.operationGroupOperation(operationContext, group, false);
        GroupModel newGroup = query(group, 1);
        List<GroupOperationModel> operationModels = groupOperationDao.query("group_id=?", Lists.newArrayList(Long.toString(newGroup.getId())));
        Assert.assertEquals(1, operationModels.size());
        Assert.assertEquals(operationContext.getOperation(), operationModels.get(0).getOperation());
        List<GroupOperationLog> logs = groupOperationLogDao
                .select(new GroupOperationLogModel(newGroup.getId(), operationContext.getOperation(), operationContext.getOperatorId()), false);
        Assert.assertEquals(1, logs.size());
        Assert.assertEquals(operationContext.getOperation(), logs.get(0).getOperation());
        Assert.assertEquals(operationContext.getToken(), logs.get(0).getToken());

        businessDao.operationGroupOperation(operationContext, group, true);
        operationModels = groupOperationDao.query("group_id=?", Lists.newArrayList(Long.toString(newGroup.getId())));
        Assert.assertEquals(0, operationModels.size());

        logs = groupOperationLogDao.select(new GroupOperationLogModel(newGroup.getId(), operationContext.getOperation(), operationContext.getOperatorId()),
                true);
        Assert.assertEquals(1, logs.size());
        Assert.assertEquals(operationContext.getOperation(), logs.get(0).getOperation());
        Assert.assertEquals(operationContext.getToken(), logs.get(0).getToken());
    }

    @Test
    public void testInsertOrUpdateRouteRuleGroups() {
        businessDao.insertOrUpdateRouteRuleGroups(newOperationContext(), Lists.newArrayList(newRouteRuleGroup()));
    }

    private void assertServiceRouteRule(String serviceId, List<RouteRuleInfo> routeRuleInfos, String routeRuleStatus, String groupStatus) {
        Map<String, ServiceRouteRule> routeRules = Maps.newHashMap();
        ListMultimap<String, String> routeRuleGroups = ArrayListMultimap.create();
        Map<String, GroupWeight> groupWeights = Maps.newHashMap();
        for (RouteRuleInfo routeRuleInfo : routeRuleInfos) {
            for (GroupWeight group : routeRuleInfo.getGroups()) {
                String groupKey = group.getGroupKey();
                routeRules.put(routeRuleInfo.getRouteRule().getName().toLowerCase(), routeRuleInfo.getRouteRule());
                routeRuleGroups.put(routeRuleInfo.getRouteRule().getName().toLowerCase(), groupKey);
                groupWeights.put(groupKey, group);
            }
        }

        Map<Long, Map<Long, RouteRuleGroupModel>> newRouteRuleGroups = Maps.newHashMap();
        for (RouteRuleGroupModel routeRuleGroup : routeRuleGroupDao.query()) {
            Map<Long, RouteRuleGroupModel> m = newRouteRuleGroups.get(routeRuleGroup.getRouteRuleId());
            if (m == null) {
                m = Maps.newHashMap();
                newRouteRuleGroups.put(routeRuleGroup.getRouteRuleId(), m);
            }
            m.put(routeRuleGroup.getGroupId(), routeRuleGroup);
        }

        GroupModel groupFilter = new GroupModel();
        groupFilter.setServiceId(serviceId);
        Map<String, Group> newGroups = Maps.newHashMap();
        for (Group group : Groups.newGroups(groupDao.select(groupFilter))) {
            String groupKey = group.getGroupKey();
            if (!groupWeights.containsKey(groupKey)) {
                continue;
            }
            newGroups.put(groupKey, group);
            if (groupStatus != null) {
                Assert.assertEquals(groupStatus, group.getStatus());
            } else {
                Assert.assertEquals(groupWeights.get(groupKey).getStatus(), group.getStatus());
            }
            assertGroup(groupWeights.get(groupKey), group);
        }
        Assert.assertEquals(groupWeights.size(), newGroups.size());

        RouteRuleModel filter = new RouteRuleModel();
        filter.setServiceId(serviceId);
        int routeRuleCount = 0;
        for (RouteRuleModel routeRuleModel : routeRuleDao.select(filter)) {
            String key = routeRuleModel.getName().toLowerCase();
            if (!routeRules.containsKey(key)) {
                continue;
            }
            routeRuleCount++;
            if (routeRuleStatus != null) {
                Assert.assertEquals(routeRuleStatus, routeRuleModel.getStatus());
            } else {
                Assert.assertEquals(routeRules.get(key).getStatus(), routeRuleModel.getStatus());
            }
            assertRouteRule(routeRules.get(key), routeRuleModel);
            for (String groupKey : routeRuleGroups.get(key)) {
                RouteRuleGroupModel routeRuleGroupModel = newRouteRuleGroups.get(routeRuleModel.getId()).get(newGroups.get(groupKey).getGroupId());
                Assert.assertEquals(groupWeights.get(groupKey).getWeight(), routeRuleGroupModel.getUnreleasedWeight());
                if (GroupRepository.GroupStatus.ACTIVE.equals(groupStatus) && GroupRepository.RouteRuleStatus.ACTIVE.equals(routeRuleStatus)) {
                    Assert.assertEquals(routeRuleGroupModel.getWeight(), routeRuleGroupModel.getUnreleasedWeight());
                }
            }
        }
        Assert.assertEquals(routeRules.size(), routeRuleCount);
    }

    private GroupModel query(GroupModel group, int expected) {
        List<GroupModel> groups = groupDao.select(group);
        Assert.assertEquals(expected, groups.size());
        if (expected >= 1) {
            return groups.get(0);
        }
        return null;
    }

    private void assertRouteRule(ServiceRouteRule expected, RouteRuleModel actual) {
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getServiceId(), actual.getServiceId());
        Assert.assertEquals(expected.getDescription(), actual.getDescription());
        Assert.assertEquals(expected.getStrategy(), actual.getStrategy());
    }

    private void assertGroup(Group expected, Group actual) {
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getServiceId(), actual.getServiceId());
        Assert.assertEquals(expected.getRegionId(), actual.getRegionId());
        Assert.assertEquals(expected.getZoneId(), actual.getZoneId());
        Assert.assertEquals(expected.getAppId(), actual.getAppId());
        Assert.assertEquals(expected.getDescription(), actual.getDescription());
    }

    private final static SecureRandom random = new SecureRandom();

    private List<RouteRuleInfo> newRouteRuleInfos(final String serviceId) {
        List<RouteRuleInfo> routeRuleInfos = Lists.newArrayList();
        int count = random.nextInt(2) + 3;
        for (int i = 0; i < count; i++) {
            final int groupCount = random.nextInt(2) + 2;

            ServiceRouteRule routeRule = newRouteRule();
            routeRule.setServiceId(serviceId);
            List<GroupWeight> groups = Lists.newArrayList();
            for (int j = 0; j < groupCount; j++) {
                GroupWeight group = newGroupWeight();
                group.setServiceId(serviceId);
                groups.add(group);
            }
            routeRuleInfos.add(new RouteRuleInfo(routeRule, groups));
        }

        return routeRuleInfos;
    }

    private ServiceRouteRule newRouteRule() {
        ServiceRouteRule routeRule = new ServiceRouteRule();
        routeRule.setName(new BigInteger(130, random).toString(32));
        routeRule.setServiceId(new BigInteger(130, random).toString(32));
        routeRule.setDescription(new BigInteger(130, random).toString(32));
        routeRule.setStatus(new BigInteger(130, random).toString(32));
        routeRule.setStrategy(new BigInteger(130, random).toString(32));
        return routeRule;
    }

    private GroupWeight newGroupWeight() {
        GroupWeight group = new GroupWeight();
        group.setAppId(new BigInteger(130, random).toString(32));
        group.setName(new BigInteger(130, random).toString(32));
        group.setRegionId(new BigInteger(130, random).toString(32));
        group.setServiceId(new BigInteger(130, random).toString(32));
        group.setZoneId(new BigInteger(130, random).toString(32));
        group.setDescription(new BigInteger(130, random).toString(32));
        group.setStatus(new BigInteger(130, random).toString(32));
        group.setWeight(random.nextInt(1000));
        return group;
    }

    private GroupModel newGroup() {
        GroupModel group = new GroupModel();
        group.setAppId(new BigInteger(130, random).toString(32));
        group.setName(new BigInteger(130, random).toString(32));
        group.setRegionId(new BigInteger(130, random).toString(32));
        group.setServiceId(new BigInteger(130, random).toString(32));
        group.setZoneId(new BigInteger(130, random).toString(32));
        group.setDescription(new BigInteger(130, random).toString(32));
        group.setStatus(new BigInteger(130, random).toString(32));
        return group;
    }

    private OperationContext newOperationContext() {
        OperationContext context = new OperationContext();
        context.setOperatorId(new BigInteger(130, random).toString(32));
        context.setToken(new BigInteger(130, random).toString(32));
        context.setOperation(new BigInteger(130, random).toString(32));
        context.setReason(new BigInteger(130, random).toString(32));
        return context;
    }

    private RouteRuleGroupModel newRouteRuleGroup() {
        RouteRuleGroupModel routeRuleGroup = new RouteRuleGroupModel();
        routeRuleGroup.setRouteRuleId(random.nextLong());
        routeRuleGroup.setGroupId(random.nextLong());
        routeRuleGroup.setWeight(random.nextInt());
        routeRuleGroup.setUnreleasedWeight(random.nextInt());
        return routeRuleGroup;
    }
}
