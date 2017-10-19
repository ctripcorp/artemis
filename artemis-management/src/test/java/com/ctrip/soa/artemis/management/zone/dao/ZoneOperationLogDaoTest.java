package com.ctrip.soa.artemis.management.zone.dao;

import com.ctrip.soa.artemis.management.log.ZoneOperationLog;
import com.ctrip.soa.artemis.management.util.Constants;
import com.ctrip.soa.artemis.management.zone.model.ZoneOperationLogModel;
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
public class ZoneOperationLogDaoTest {
    ZoneOperationLogDao zoneOperationLogDao = ZoneOperationLogDao.INSTANCE;

    @Test
    public void testInsert() {
        final ZoneOperationLogModel log1 = newZoneOperationLogModel();
        final ZoneOperationLogModel log2 = newZoneOperationLogModel();
        log1.setComplete(true);
        log2.setComplete(true);
        final List<ZoneOperationLogModel> logs = Lists.newArrayList(log1, log2);
        zoneOperationLogDao.insert(log1, log2);
        for (final ZoneOperationLogModel log : logs) {
            final List<ZoneOperationLog> logModels = query(log);
            Assert.assertEquals(1, logModels.size());
            assertZoneOperationLog(log, logModels.get(0));
        }
        log1.setComplete(false);
        log2.setComplete(false);
        zoneOperationLogDao.insert(log1, log2);
        for (final ZoneOperationLogModel log : logs) {
            final List<ZoneOperationLog> logModels = query(log);
            Assert.assertEquals(1, logModels.size());
            assertZoneOperationLog(log, logModels.get(0));
        }
    }

    private List<ZoneOperationLog> query(final ZoneOperationLogModel instanceLog) {
        return zoneOperationLogDao.select(instanceLog, instanceLog.isComplete());
    }

    private final static SecureRandom random = new SecureRandom();

    public static ZoneOperationLogModel newZoneOperationLogModel() {
        final ZoneOperationLogModel log = new ZoneOperationLogModel();
        log.setRegionId(Constants.regionId);
        log.setServiceId(new BigInteger(130, random).toString(32));
        log.setZoneId(new BigInteger(130, random).toString(32));
        log.setOperation(new BigInteger(130, random).toString(32));
        log.setOperatorId(new BigInteger(130, random).toString(32));
        log.setToken(new BigInteger(130, random).toString(32));
        log.setComplete(random.nextBoolean());
        return log;
    }

    public static void assertZoneOperationLog(final ZoneOperationLogModel expected, final ZoneOperationLog actual) {
        Assert.assertEquals(expected.getRegionId(), actual.getRegionId());
        Assert.assertEquals(expected.getServiceId(), actual.getServiceId());
        Assert.assertEquals(expected.getZoneId(), actual.getZoneId());
        Assert.assertEquals(expected.getOperation(), actual.getOperation());
        Assert.assertEquals(expected.getOperatorId(), actual.getOperatorId());
        Assert.assertEquals(expected.getToken(), actual.getToken());
        Assert.assertEquals(expected.isComplete(), actual.isComplete());
    }
}
