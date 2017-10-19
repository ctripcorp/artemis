package com.ctrip.soa.artemis.management.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ctrip.soa.artemis.management.log.InstanceOperationLog;
import com.ctrip.soa.artemis.management.util.InstanceLogModels;
import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
@Transactional
public class InstanceLogDaoTest {
    InstanceLogDao _instanceLogDao = InstanceLogDao.INSTANCE;

    @Test
    public void testInsert() {
        final InstanceLogModel log1 = InstanceLogModels.newInstanceLogModel();
        final InstanceLogModel log2 = InstanceLogModels.newInstanceLogModel();
        log1.setComplete(true);
        log2.setComplete(true);
        final List<InstanceLogModel> logs = Lists.newArrayList(log1, log2);
        _instanceLogDao.insert(log1, log2);
        for (final InstanceLogModel log : logs) {
            final List<InstanceOperationLog> logModels = query(log);
            Assert.assertEquals(1, logModels.size());
            InstanceLogModels.assertInstanceLog(log, logModels.get(0));
        }
        log1.setComplete(false);
        log2.setComplete(false);
        _instanceLogDao.insert(log1, log2);
        for (final InstanceLogModel log : logs) {
            final List<InstanceOperationLog> logModels = query(log);
            Assert.assertEquals(1, logModels.size());
            InstanceLogModels.assertInstanceLog(log, logModels.get(0));
        }
    }

    private List<InstanceOperationLog> query(final InstanceLogModel instanceLog) {
        return  _instanceLogDao.select(instanceLog, instanceLog.isComplete());
    }    
}
