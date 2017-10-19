package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.group.model.GroupOperationModel;
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
public class GroupOperationDaoTest {
    private final GroupOperationDao groupOperationDao = GroupOperationDao.INSTANCE;
    @Test
    public void testQuery() throws Exception {
        groupOperationDao.insertOrUpdate(newGroupOperation());
        groupOperationDao.insertOrUpdate(newGroupOperation());
        Assert.assertTrue(groupOperationDao.query().size() >= 2);
    }

    @Test
    public void testDelete() throws Exception {
        GroupOperationModel groupOperation = newGroupOperation();
        groupOperationDao.insertOrUpdate(groupOperation);
        assertGroupOperation(groupOperation, query(groupOperation));
        groupOperationDao.delete(groupOperation);
        Assert.assertNull(query(groupOperation));
    }

    @Test
    public void testInsertOrUpdate() throws Exception {
        GroupOperationModel groupOperation = newGroupOperation();
        groupOperationDao.insertOrUpdate(groupOperation);
        groupOperationDao.insertOrUpdate(groupOperation);
        assertGroupOperation(groupOperation, query(groupOperation));
    }

    private GroupOperationModel query(GroupOperationModel groupOperation) {
        List<GroupOperationModel> groupOperationFromQuery = groupOperationDao.query("group_id = ? and operation=?", Lists.newArrayList(Long.toString(groupOperation.getGroupId()), groupOperation.getOperation()));
        if (groupOperationFromQuery.size() == 1) {
            return groupOperationFromQuery.get(0);
        }
        return null;
    }

    private void assertGroupOperation(GroupOperationModel expected, GroupOperationModel actual) {
        Assert.assertEquals(expected.getGroupId(), actual.getGroupId());
        Assert.assertEquals(expected.getOperation(), actual.getOperation());
    }

    private final static SecureRandom random = new SecureRandom();
    private GroupOperationModel newGroupOperation() {
        GroupOperationModel groupOperation = new GroupOperationModel();
        groupOperation.setGroupId(random.nextLong());
        groupOperation.setOperation(new BigInteger(130, random).toString(32));
        return groupOperation;
    }
}
