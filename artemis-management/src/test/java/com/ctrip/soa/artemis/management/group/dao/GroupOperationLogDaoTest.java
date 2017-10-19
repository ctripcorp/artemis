package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.group.model.GroupOperationLogModel;
import com.ctrip.soa.artemis.management.group.log.GroupOperationLog;
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
public class GroupOperationLogDaoTest {
    private GroupOperationLogDao groupOperationLogDao = GroupOperationLogDao.INSTANCE;
    @Test
    public void testInsert() {
        final GroupOperationLogModel log1 = newModel();
        final GroupOperationLogModel log2 = newModel();
        log1.setComplete(true);
        log2.setComplete(true);
        final List<GroupOperationLogModel> logs = Lists.newArrayList(log1, log2);
        groupOperationLogDao.insert(log1, log2);
        for (final GroupOperationLogModel log : logs) {
            final List<GroupOperationLog> logModels = query(log);
            Assert.assertEquals(1, logModels.size());
            assertLog(log, logModels.get(0));
        }
        log1.setComplete(false);
        log2.setComplete(false);
        groupOperationLogDao.insert(log1, log2);
        for (final GroupOperationLogModel log : logs) {
            final List<GroupOperationLog> logModels = query(log);
            Assert.assertEquals(1, logModels.size());
            assertLog(log, logModels.get(0));
        }
    }

    private List<GroupOperationLog> query(final GroupOperationLogModel log) {
        return  groupOperationLogDao.select(log, log.isComplete());
    }

    private final static SecureRandom random = new SecureRandom();
    private GroupOperationLogModel newModel() {
        final GroupOperationLogModel log = new GroupOperationLogModel();
        log.setGroupId(random.nextLong());
        log.setComplete(random.nextBoolean());
        log.setOperation(new BigInteger(130, random).toString(32));
        log.setOperatorId(new BigInteger(130, random).toString(32));
        log.setToken(new BigInteger(130, random).toString(32));
        log.setReason(new BigInteger(130, random).toString(32));
        log.setExtensions("{}");
        return log;
    }

    public static void assertLog(final GroupOperationLogModel expected, final GroupOperationLog actual) {
        Assert.assertEquals(expected.getGroupId(), actual.getGroupId());
        Assert.assertEquals(expected.getOperation(), actual.getOperation());
        Assert.assertEquals(expected.isComplete(), actual.isComplete());
        Assert.assertEquals(expected.getOperatorId(), actual.getOperatorId());
        Assert.assertEquals(expected.getToken(), actual.getToken());
        Assert.assertEquals(expected.getExtensions(), actual.getExtensions());
        Assert.assertEquals(expected.getReason(), actual.getReason());
    }
}
