package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.group.log.RouteRuleGroupLog;
import com.ctrip.soa.artemis.management.group.model.RouteRuleGroupLogModel;
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
public class RouteRuleGroupLogDaoTest {
    private RouteRuleGroupLogDao routeRuleGroupLogDao = RouteRuleGroupLogDao.INSTANCE;
    @Test
    public void testInsert() {
        final RouteRuleGroupLogModel log1 = newModel();
        final RouteRuleGroupLogModel log2 = newModel();
        final List<RouteRuleGroupLogModel> logs = Lists.newArrayList(log1, log2);
        routeRuleGroupLogDao.insert(log1, log2);
        for (final RouteRuleGroupLogModel log : logs) {
            final List<RouteRuleGroupLog> logModels = query(log);
            Assert.assertEquals(1, logModels.size());
            assertLog(log, logModels.get(0));
        }
    }

    private List<RouteRuleGroupLog> query(final RouteRuleGroupLogModel log) {
        return  routeRuleGroupLogDao.select(log);
    }

    private final static SecureRandom random = new SecureRandom();
    private RouteRuleGroupLogModel newModel() {
        final RouteRuleGroupLogModel log = new RouteRuleGroupLogModel();
        log.setGroupId(random.nextLong());
        log.setRouteRuleId(random.nextLong());
        log.setWeight(random.nextInt());
        log.setOperation(new BigInteger(130, random).toString(32));
        log.setOperatorId(new BigInteger(130, random).toString(32));
        log.setToken(new BigInteger(130, random).toString(32));
        log.setReason(new BigInteger(130, random).toString(32));
        log.setExtensions("{}");
        return log;
    }

    public static void assertLog(final RouteRuleGroupLogModel expected, final RouteRuleGroupLog actual) {
        Assert.assertEquals(expected.getGroupId(), actual.getGroupId());
        Assert.assertEquals(expected.getOperation(), actual.getOperation());
        Assert.assertEquals(expected.getOperatorId(), actual.getOperatorId());
        Assert.assertEquals(expected.getToken(), actual.getToken());
        Assert.assertEquals(expected.getExtensions(), actual.getExtensions());
        Assert.assertEquals(expected.getReason(), actual.getReason());
    }
}
