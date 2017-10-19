package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.group.log.ServiceInstanceLog;
import com.ctrip.soa.artemis.management.group.model.ServiceInstanceLogModel;
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
public class ServiceInstanceLogDaoTest {
    private ServiceInstanceLogDao serviceInstanceLogDao = ServiceInstanceLogDao.INSTANCE;

    @Test
    public void testInsert() {
        final ServiceInstanceLogModel log1 = newModel();
        final ServiceInstanceLogModel log2 = newModel();
        final List<ServiceInstanceLogModel> logs = Lists.newArrayList(log1, log2);
        serviceInstanceLogDao.insert(log1, log2);
        for (final ServiceInstanceLogModel log : logs) {
            final List<ServiceInstanceLog> logModels = query(log);
            Assert.assertEquals(1, logModels.size());
            assertLog(log, logModels.get(0));
        }
    }

    private List<ServiceInstanceLog> query(final ServiceInstanceLogModel log) {
        return serviceInstanceLogDao.select(log);
    }

    private final static SecureRandom random = new SecureRandom();

    private ServiceInstanceLogModel newModel() {
        final ServiceInstanceLogModel log = new ServiceInstanceLogModel();
        log.setServiceId(new BigInteger(130, random).toString(32));
        log.setInstanceId(new BigInteger(130, random).toString(32));
        log.setIp(new BigInteger(130, random).toString(32));
        log.setMachineName(new BigInteger(130, random).toString(32));
        log.setMetadata(new BigInteger(130, random).toString(32));
        log.setProtocol(new BigInteger(60, random).toString(32));
        log.setRegionId(new BigInteger(60, random).toString(32));
        log.setZoneId(new BigInteger(60, random).toString(32));
        log.setHealthCheckUrl(new BigInteger(130, random).toString(32));
        log.setUrl(new BigInteger(130, random).toString(32));
        log.setOperation(new BigInteger(130, random).toString(32));
        log.setOperatorId(new BigInteger(130, random).toString(32));
        log.setToken(new BigInteger(130, random).toString(32));
        log.setGroupId(new BigInteger(130, random).toString(32));
        return log;
    }

    public static void assertLog(final ServiceInstanceLogModel expected, final ServiceInstanceLog actual) {
        Assert.assertEquals(expected.getServiceId(), actual.getServiceId());
        Assert.assertEquals(expected.getInstanceId(), actual.getInstanceId());
        Assert.assertEquals(expected.getIp(), actual.getIp());
        Assert.assertEquals(expected.getMachineName(), actual.getMachineName());
        Assert.assertEquals(expected.getMetadata(), actual.getMetadata());
        Assert.assertEquals(expected.getPort(), actual.getPort());
        Assert.assertEquals(expected.getProtocol(), actual.getProtocol());
        Assert.assertEquals(expected.getRegionId(), actual.getRegionId());
        Assert.assertEquals(expected.getZoneId(), actual.getZoneId());
        Assert.assertEquals(expected.getHealthCheckUrl(), actual.getHealthCheckUrl());
        Assert.assertEquals(expected.getUrl(), actual.getUrl());
        Assert.assertEquals(expected.getOperation(), actual.getOperation());
        Assert.assertEquals(expected.getOperatorId(), actual.getOperatorId());
        Assert.assertEquals(expected.getToken(), actual.getToken());
        Assert.assertEquals(expected.getGroupId(), actual.getGroupId());
    }
}
