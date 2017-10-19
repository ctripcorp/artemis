package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.group.model.GroupInstanceModel;
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
public class GroupInstanceDaoTest {
    private final GroupInstanceDao groupInstanceDao = GroupInstanceDao.INSTANCE;

    @Test
    public void testInsertOrUpdate() {
        GroupInstanceModel groupInstance = newGroupInstance();
        groupInstanceDao.insert(groupInstance);
        assertGroupInstance(groupInstance, query(groupInstance));
        groupInstanceDao.insert(groupInstance);
        assertGroupInstance(groupInstance, query(groupInstance));
    }

    @Test
    public void testDelete() {
        GroupInstanceModel groupInstance = newGroupInstance();
        groupInstanceDao.insert(groupInstance);
        GroupInstanceModel copy1 = query(groupInstance);
        groupInstanceDao.delete(copy1.getId());
        Assert.assertNull(query(groupInstance, 0));
    }

    @Test
    public void testDeleteByFilters() {
        GroupInstanceModel groupInstance1 = newGroupInstance();
        GroupInstanceModel groupInstance2 = newGroupInstance();
        groupInstanceDao.insert(groupInstance1, groupInstance2);
        query(groupInstance1, 1);
        query(groupInstance2, 1);
        groupInstanceDao.deleteByFilters(Lists.newArrayList(groupInstance1, groupInstance2));
        query(groupInstance1, 0);
        query(groupInstance2, 0);
    }

    @Test
    public void testSelect() {
        GroupInstanceModel groupInstance = newGroupInstance();
        groupInstanceDao.insert(groupInstance);
        GroupInstanceModel filter = new GroupInstanceModel();
        filter.setGroupId(groupInstance.getGroupId());
        filter.setInstanceId(groupInstance.getInstanceId());
        assertGroupInstance(groupInstance, groupInstanceDao.select(filter).get(0));
    }

    private GroupInstanceModel query(GroupInstanceModel group) {
        return query(group, 1);
    }

    private GroupInstanceModel query(GroupInstanceModel group, int expected) {
        List<GroupInstanceModel> groups = groupInstanceDao.select(group);
        Assert.assertEquals(expected, groups.size());
        if (expected >= 1) {
            return groups.get(0);
        }
        return null;
    }

    private final static SecureRandom random = new SecureRandom();
    private GroupInstanceModel newGroupInstance() {
        GroupInstanceModel groupInstance = new GroupInstanceModel();
        groupInstance.setGroupId(random.nextLong());
        groupInstance.setInstanceId(new BigInteger(130, random).toString(32));
        return groupInstance;
    }

    private void assertGroupInstance(GroupInstanceModel expected, GroupInstanceModel actual) {
        Assert.assertEquals(expected.getGroupId(), actual.getGroupId());
        Assert.assertEquals(expected.getInstanceId(), actual.getInstanceId());
    }
}
