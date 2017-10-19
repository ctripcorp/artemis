package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.group.log.GroupInstanceLog;
import com.ctrip.soa.artemis.management.group.model.GroupInstanceLogModel;
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
public class GroupInstanceLogDaoTest {
    private GroupInstanceLogDao groupInstanceLogDao = GroupInstanceLogDao.INSTANCE;
    @Test
    public void testInsert() {
        final GroupInstanceLogModel log1 = newModel();
        final GroupInstanceLogModel log2 = newModel();
        final List<GroupInstanceLogModel> logs = Lists.newArrayList(log1, log2);
        groupInstanceLogDao.insert(log1, log2);
        for (final GroupInstanceLogModel log : logs) {
            final List<GroupInstanceLog> logModels = query(log);
            Assert.assertEquals(1, logModels.size());
            assertLog(log, logModels.get(0));
        }
    }

    private List<GroupInstanceLog> query(final GroupInstanceLogModel log) {
        return  groupInstanceLogDao.select(log);
    }

    private final static SecureRandom random = new SecureRandom();
    private GroupInstanceLogModel newModel() {
        final GroupInstanceLogModel log = new GroupInstanceLogModel();
        log.setGroupId(random.nextLong());
        log.setInstanceId(new BigInteger(130, random).toString(32));
        log.setOperation(new BigInteger(130, random).toString(32));
        log.setReason(new BigInteger(130, random).toString(32));
        log.setOperatorId(new BigInteger(130, random).toString(32));
        log.setToken(new BigInteger(130, random).toString(32));
        return log;
    }

    public static void assertLog(final GroupInstanceLogModel expected, final GroupInstanceLog actual) {
        Assert.assertEquals(expected.getGroupId(), actual.getGroupId());
        Assert.assertEquals(expected.getInstanceId(), actual.getInstanceId());
        Assert.assertEquals(expected.getOperation(), actual.getOperation());
        Assert.assertEquals(expected.getOperatorId(), actual.getOperatorId());
        Assert.assertEquals(expected.getToken(), actual.getToken());
        Assert.assertEquals(expected.getReason(), actual.getReason());
    }
}
