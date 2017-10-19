package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.group.model.RouteRuleGroupModel;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
@Transactional
public class RouteRuleGroupDaoTest {
    private final RouteRuleGroupDao routeRuleGroupDao = RouteRuleGroupDao.INSTANCE;

    @Test
    public void testInsertOrUpdate() {
        for (RouteRuleGroupModel routeRuleGroup : newRouteRuleGroups()) {
            routeRuleGroupDao.insertOrUpdate(routeRuleGroup);
            assertRouteRuleGroup(routeRuleGroup, query(routeRuleGroup));
            routeRuleGroup.setUnreleasedWeight(null);
            routeRuleGroupDao.insertOrUpdate(routeRuleGroup);
            assertRouteRuleGroup(routeRuleGroup, query(routeRuleGroup));
        }
    }

    @Test
    public void testInsert() {
        for (RouteRuleGroupModel routeRuleGroup : newRouteRuleGroups()) {
            routeRuleGroupDao.insert(routeRuleGroup);
            assertRouteRuleGroup(routeRuleGroup, query(routeRuleGroup));
            routeRuleGroup.setUnreleasedWeight(null);
            routeRuleGroupDao.insert(routeRuleGroup);
            assertRouteRuleGroup(routeRuleGroup, query(routeRuleGroup));
        }
    }

    @Test
    public void testDelete() {
        for (RouteRuleGroupModel routeRuleGroup : newRouteRuleGroups()) {
            routeRuleGroupDao.insertOrUpdate(routeRuleGroup);
            RouteRuleGroupModel copy = query(routeRuleGroup);
            routeRuleGroupDao.delete(copy.getId());
            Assert.assertNull(query(routeRuleGroup, 0));
        }
    }

    @Test
    public void testSelect() {
        for (RouteRuleGroupModel routeRuleGroup : newRouteRuleGroups()) {
            routeRuleGroupDao.insertOrUpdate(routeRuleGroup);
            RouteRuleGroupModel filter = new RouteRuleGroupModel();
            filter.setGroupId(routeRuleGroup.getGroupId());
            Assert.assertEquals(1, routeRuleGroupDao.select(filter).size());
            Assert.assertEquals(1, routeRuleGroupDao.select(Lists.newArrayList(routeRuleGroupDao.select(filter).get(0).getId())).size());
        }
    }

    @Test
    public void testRelease() {
        for (RouteRuleGroupModel routeRuleGroup : newRouteRuleGroups()) {
            routeRuleGroupDao.insertOrUpdate(routeRuleGroup);
            RouteRuleGroupModel valueBeforeReleased = query(routeRuleGroup);
            assertRouteRuleGroup(routeRuleGroup, valueBeforeReleased);
            if (routeRuleGroup.getUnreleasedWeight() != null) {
                Assert.assertNull(valueBeforeReleased.getWeight());
                Assert.assertNotEquals(routeRuleGroup.getUnreleasedWeight(), valueBeforeReleased.getWeight());
            } else {
                Assert.assertEquals(routeRuleGroup.getUnreleasedWeight(), valueBeforeReleased.getWeight());
            }

            routeRuleGroupDao.release(Lists.newArrayList(valueBeforeReleased));
            RouteRuleGroupModel valueAfterReleased = query(routeRuleGroup);
            assertRouteRuleGroup(routeRuleGroup, valueAfterReleased);
            Assert.assertEquals(routeRuleGroup.getUnreleasedWeight(), valueAfterReleased.getWeight());
        }
    }

    @Test
    public void testPublish() {
        for (RouteRuleGroupModel routeRuleGroup : newRouteRuleGroups()) {
            routeRuleGroupDao.publish(routeRuleGroup);
            RouteRuleGroupModel published = query(routeRuleGroup);
            Assert.assertEquals(routeRuleGroup.getRouteRuleId(), published.getRouteRuleId());
            Assert.assertEquals(routeRuleGroup.getGroupId(), published.getGroupId());
            Assert.assertEquals(null, published.getUnreleasedWeight());
            Assert.assertEquals(routeRuleGroup.getWeight(), published.getWeight());
        }
    }

    private RouteRuleGroupModel query(RouteRuleGroupModel routeRuleGroup) {
        return query(routeRuleGroup, 1);
    }

    private RouteRuleGroupModel query(RouteRuleGroupModel routeRuleGroup, int expected) {
        List<RouteRuleGroupModel> routeRules = routeRuleGroupDao.select(routeRuleGroup);
        Assert.assertEquals(expected, routeRules.size());
        if (expected >= 1) {
            return routeRules.get(0);
        }
        return null;
    }

    private final static SecureRandom random = new SecureRandom();

    private RouteRuleGroupModel newRouteRuleGroup() {
        RouteRuleGroupModel routeRuleGroup = new RouteRuleGroupModel();
        routeRuleGroup.setRouteRuleId(random.nextLong());
        routeRuleGroup.setGroupId(random.nextLong());
        routeRuleGroup.setWeight(random.nextInt());
        routeRuleGroup.setUnreleasedWeight(random.nextInt());
        return routeRuleGroup;
    }

    private List<RouteRuleGroupModel> newRouteRuleGroups() {
        RouteRuleGroupModel routeRuleGroup = newRouteRuleGroup();
        routeRuleGroup.setUnreleasedWeight(null);
        return Lists.newArrayList(newRouteRuleGroup(), routeRuleGroup);
    }

    private void assertRouteRuleGroup(RouteRuleGroupModel expected, RouteRuleGroupModel actual) {
        Assert.assertEquals(expected.getRouteRuleId(), actual.getRouteRuleId());
        Assert.assertEquals(expected.getGroupId(), actual.getGroupId());
        Assert.assertEquals(expected.getUnreleasedWeight(), actual.getUnreleasedWeight());
    }
}
