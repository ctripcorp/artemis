package com.ctrip.soa.artemis.management;

import com.ctrip.soa.artemis.RouteRule;
import com.ctrip.soa.artemis.ServiceGroup;
import com.ctrip.soa.artemis.management.common.OperationContext;
import com.ctrip.soa.artemis.management.group.Group;
import com.ctrip.soa.artemis.management.group.ServiceRouteRule;
import com.ctrip.soa.artemis.management.group.model.GroupModel;
import com.ctrip.soa.artemis.management.group.model.RouteRuleGroupModel;
import com.ctrip.soa.artemis.management.group.model.RouteRuleModel;
import com.ctrip.soa.artemis.util.ServiceGroups;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fang_j on 10/07/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
@Transactional
public class GroupRepositoryTest {
    private static final String regionId = "SHA";
    private static GroupRepository groupRepository = GroupRepository.getInstance();
    private static List<String> serviceIds = Lists.newArrayList("GroupRepositoryTest.service1", "GroupRepositoryTest.service2");
    private static List<String> zones = Lists.newArrayList("shajq", "shaoy", "shatt");
    private static List<String> routeIds = Lists.newArrayList("routeId1", "routeId2", "routeId3", "routeId4");
    private static final int minWeight = 100;
    private static List<RouteRuleModel> routeRuleModels = Lists.newArrayList();
    private static List<GroupModel> groupModels = Lists.newArrayList();
    private static List<RouteRuleGroupModel>  routeRuleGroupModels = Lists.newArrayList();
    private static Map<String, Integer> routeRuleGroupWeights = Maps.newHashMap();
    private static OperationContext operationContext = new OperationContext();

    @Before
    public void setUp() {
        groupRepository.stopRefresh();
        groupRepository.refreshCache();
        operationContext.setOperatorId("repository");
        operationContext.setToken("repository");
        operationContext.setOperation("operation");
        operationContext.setReason("reason");
        AtomicInteger count = new AtomicInteger(0);
        for (String serviceId : serviceIds) {
            for (String routeId : routeIds) {
                RouteRuleModel routeRuleModel = new RouteRuleModel(serviceId, routeId, "description", GroupRepository.RouteRuleStatus.ACTIVE, RouteRule.Strategy.WEIGHTED_ROUND_ROBIN);
                routeRuleModels.add(routeRuleModel);
            }

            for (String zone : zones) {
                for (int i = 0; i < 3; i++) {
                    String groupName = "custom" + count.getAndIncrement();
                    GroupModel groupModel = new GroupModel(serviceId, "sha", zone, groupName, "appId", "description", GroupRepository.GroupStatus.ACTIVE);
                    groupModels.add(groupModel);
                }
            }
        }

        groupRepository.insertRouteRules(operationContext, routeRuleModels);
        groupRepository.insertGroups(operationContext, groupModels);
        initWeight();
    }

    private void initWeight() {
        AtomicInteger weight = new AtomicInteger(minWeight);
        for (String serviceId : serviceIds) {
            RouteRuleModel rrFilter = new RouteRuleModel();
            rrFilter.setServiceId(serviceId);
            GroupModel gFilter = new GroupModel();
            gFilter.setServiceId(serviceId);
            List<ServiceRouteRule> routeRules = groupRepository.getRouteRules(rrFilter);
            List<Group> groups = groupRepository.getGroups(gFilter);
            for (ServiceRouteRule routeRule : routeRules) {
                for (Group group : groups) {
                    Integer w = weight.getAndIncrement();
                    routeRuleGroupWeights.put(routeRule.getRouteRuleId() + "-" + group.getGroupId(), w);
                    routeRuleGroupModels.add(new RouteRuleGroupModel(routeRule.getRouteRuleId(), group.getGroupId(), w));
                }
            }
        }
        groupRepository.insertServiceRouteRuleGroups(operationContext, routeRuleGroupModels);
    }

    @Test
    public void testRefreshGroupCache() {
        groupRepository.refreshCache();
        Assert.assertTrue(groupRepository.getAllGroups(regionId).size() > 0);
        Assert.assertTrue(groupRepository.getAllRouteRules(regionId).size() > 0);
        for (String serviceId : serviceIds) {
            List<RouteRule> routeRules = groupRepository.getServiceRouteRules(serviceId, null);
            Assert.assertEquals(routeIds.size(), routeRules.size());
            for (RouteRule routeRule : routeRules) {
                Assert.assertTrue(routeIds.contains(routeRule.getRouteId()));
                Assert.assertEquals(zones.size() * 3, routeRule.getGroups().size());
                for (ServiceGroup group : routeRule.getGroups()) {
                    Assert.assertEquals(ServiceGroups.DEFAULT_WEIGHT_VALUE, group.getWeight().intValue());
                }
            }
        }

        groupRepository.releaseServiceRouteRuleGroups(operationContext, routeRuleGroupModels);
        groupRepository.refreshCache();
        for (String serviceId : serviceIds) {
            List<RouteRule> routeRules = groupRepository.getServiceRouteRules(serviceId, null);
            Assert.assertEquals(routeIds.size(), routeRules.size());
            for (RouteRule routeRule : routeRules) {
                Assert.assertTrue(routeIds.contains(routeRule.getRouteId()));
                Assert.assertEquals(zones.size() * 3, routeRule.getGroups().size());
                for (ServiceGroup group : routeRule.getGroups()) {
                    Assert.assertTrue(group.getWeight().intValue() >= minWeight);
                }
            }
        }
    }
}
