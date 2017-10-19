package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.group.log.RouteRuleLog;
import com.ctrip.soa.artemis.management.group.model.RouteRuleLogModel;
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
public class RouteRuleLogDaoTest {
    private RouteRuleLogDao routeRuleLogDao = RouteRuleLogDao.INSTANCE;

    @Test
    public void testInsert() {
        final RouteRuleLogModel log1 = newModel();
        final RouteRuleLogModel log2 = newModel();
        final List<RouteRuleLogModel> logs = Lists.newArrayList(log1, log2);
        routeRuleLogDao.insert(log1, log2);
        for (final RouteRuleLogModel log : logs) {
            final List<RouteRuleLog> logModels = query(log);
            Assert.assertEquals(1, logModels.size());
            assertLog(log, logModels.get(0));
        }
    }

    private List<RouteRuleLog> query(final RouteRuleLogModel log) {
        return routeRuleLogDao.select(log);
    }

    private final static SecureRandom random = new SecureRandom();

    private RouteRuleLogModel newModel() {
        final RouteRuleLogModel log = new RouteRuleLogModel();
        log.setServiceId(new BigInteger(130, random).toString(32));
        log.setName(new BigInteger(130, random).toString(32));
        log.setStatus(new BigInteger(130, random).toString(32));
        log.setOperation(new BigInteger(130, random).toString(32));
        log.setOperatorId(new BigInteger(130, random).toString(32));
        log.setToken(new BigInteger(130, random).toString(32));
        log.setReason(new BigInteger(130, random).toString(32));
        log.setExtensions("{}");
        return log;
    }

    public static void assertLog(final RouteRuleLogModel expected, final RouteRuleLog actual) {
        Assert.assertEquals(expected.getServiceId(), actual.getServiceId());
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
        Assert.assertEquals(expected.getOperation(), actual.getOperation());
        Assert.assertEquals(expected.getOperatorId(), actual.getOperatorId());
        Assert.assertEquals(expected.getToken(), actual.getToken());
        Assert.assertEquals(expected.getExtensions(), actual.getExtensions());
        Assert.assertEquals(expected.getReason(), actual.getReason());
    }
}
