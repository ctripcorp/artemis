package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.group.log.GroupTagLog;
import com.ctrip.soa.artemis.management.group.model.GroupTagLogModel;
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
public class GroupTagLogDaoTest {
    private GroupTagLogDao groupTagLogDao = GroupTagLogDao.INSTANCE;
    @Test
    public void testInsert() {
        final GroupTagLogModel log1 = newModel();
        final GroupTagLogModel log2 = newModel();
        final List<GroupTagLogModel> logs = Lists.newArrayList(log1, log2);
        groupTagLogDao.insert(log1, log2);
        for (final GroupTagLogModel log : logs) {
            final List<GroupTagLog> ls = query(log);
            Assert.assertTrue(ls.size() == 1);
            assertLog(log, ls.get(0));
        }
        Assert.assertTrue(groupTagLogDao.query("create_time >= DATE_SUB(now(), INTERVAL ? MINUTE)", "2").size() >= logs.size());
        Assert.assertTrue(groupTagLogDao.query("create_time > now()").size() == 0);
        Assert.assertTrue(groupTagLogDao.query("create_time <= now()").size() >= logs.size());
    }

    private List<GroupTagLog> query(final GroupTagLogModel log) {
        return  groupTagLogDao.query("group_id=? and tag=? and operation=?",
                log.getGroupId(), log.getTag(), log.getOperation());
    }

    private final static SecureRandom random = new SecureRandom();
    private GroupTagLogModel newModel() {
        final GroupTagLogModel log = new GroupTagLogModel();
        log.setGroupId(random.nextLong());
        log.setTag(new BigInteger(130, random).toString(32));
        log.setValue(new BigInteger(130, random).toString(32));
        log.setOperation(new BigInteger(130, random).toString(32));
        log.setOperatorId(new BigInteger(130, random).toString(32));
        log.setToken(new BigInteger(130, random).toString(32));
        log.setExtensions("{}");
        return log;
    }

    public static void assertLog(final GroupTagLogModel expected, final GroupTagLog actual) {
        Assert.assertEquals(expected.getGroupId(), actual.getGroupId());
        Assert.assertEquals(expected.getOperation(), actual.getOperation());
        Assert.assertEquals(expected.getTag(), actual.getTag());
        Assert.assertEquals(expected.getValue(), actual.getValue());
        Assert.assertEquals(expected.getOperatorId(), actual.getOperatorId());
        Assert.assertEquals(expected.getToken(), actual.getToken());
        Assert.assertEquals(expected.getExtensions(), actual.getExtensions());
    }
}
