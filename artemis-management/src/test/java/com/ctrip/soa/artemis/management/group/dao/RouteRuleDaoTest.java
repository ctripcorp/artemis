package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.group.model.RouteRuleModel;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
@Transactional
public class RouteRuleDaoTest {
    private final RouteRuleDao routeRuleDao = RouteRuleDao.INSTANCE;

    @Test
    public void testInsertOrUpdate() {
        RouteRuleModel routeRule = newRouteRule();
        routeRuleDao.insertOrUpdate(routeRule);
        assertRouteRule(routeRule, query(routeRule));
        routeRuleDao.insertOrUpdate(routeRule);
        assertRouteRule(routeRule, query(routeRule));
    }

    @Test
    public void testDelete() {
        RouteRuleModel routeRule = newRouteRule();
        routeRuleDao.insertOrUpdate(routeRule);
        RouteRuleModel copy1 = query(routeRule);
        routeRuleDao.delete(copy1.getId());
        Assert.assertNull(query(routeRule, 0));
        routeRuleDao.insertOrUpdate(routeRule);
        RouteRuleModel copy2 = query(routeRule);
        Assert.assertEquals(copy1.getId(), copy2.getId());
        assertRouteRule(copy1, copy2);
    }

    @Test
    public void testSelect() {
        RouteRuleModel routeRule = newRouteRule();
        routeRuleDao.insertOrUpdate(routeRule);
        RouteRuleModel filter = new RouteRuleModel();
        filter.setName(routeRule.getName());
        filter.setServiceId(routeRule.getServiceId());
        Assert.assertEquals(1, routeRuleDao.select(filter).size());
        Assert.assertEquals(1, routeRuleDao.select(Lists.newArrayList(routeRuleDao.select(filter).get(0).getId())).size());
    }

    private RouteRuleModel query(RouteRuleModel routeRule) {
        return query(routeRule, 1);
    }

    private RouteRuleModel query(RouteRuleModel routeRule, int expected) {
        List<RouteRuleModel> routeRules = routeRuleDao.select(routeRule);
        Assert.assertEquals(expected, routeRules.size());
        if (expected >= 1) {
            return routeRules.get(0);
        }
        return null;
    }

    private final static SecureRandom random = new SecureRandom();
    private RouteRuleModel newRouteRule() {
        RouteRuleModel routeRule = new RouteRuleModel();
        routeRule.setName(new BigInteger(130, random).toString(32));
        routeRule.setServiceId(new BigInteger(130, random).toString(32));
        routeRule.setDescription(new BigInteger(130, random).toString(32));
        routeRule.setStatus(new BigInteger(130, random).toString(32));
        routeRule.setStrategy(new BigInteger(130, random).toString(32));
        return routeRule;
    }

    private void assertRouteRule(RouteRuleModel expected, RouteRuleModel actual) {
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getServiceId(), actual.getServiceId());
        Assert.assertEquals(expected.getDescription(), actual.getDescription());
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
    }
}
