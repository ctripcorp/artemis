package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.group.model.GroupModel;
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
public class GroupDaoTest {
    private final GroupDao groupDao = GroupDao.INSTANCE;

    @Test
    public void testGenerateGroup() {
        GroupModel group = newGroup();
        GroupModel newGroup = groupDao.generateGroup(group);
        Assert.assertNotNull(newGroup.getId());
        assertGroup(newGroup, group);
    }

    @Test
    public void testInsertOrUpdate() {
        GroupModel group = newGroup();
        groupDao.insertOrUpdate(group);
        assertGroup(group, query(group));
        groupDao.insertOrUpdate(group);
        assertGroup(group, query(group));
    }

    @Test
    public void testDelete() {
        GroupModel group = newGroup();
        groupDao.insertOrUpdate(group);
        GroupModel copy1 = query(group);
        groupDao.delete(copy1.getId());
        Assert.assertNull(query(group, 0));
        groupDao.insertOrUpdate(group);
        GroupModel copy2 = query(group);
        Assert.assertEquals(copy1.getId(), copy2.getId());
        assertGroup(copy1, copy2);
    }

    @Test
    public void testSelect() {
        GroupModel group = newGroup();
        groupDao.insertOrUpdate(group);
        GroupModel filter = new GroupModel();
        filter.setServiceId(group.getServiceId());
        filter.setName(group.getName());
        GroupModel newGroup = groupDao.select(filter).get(0);
        assertGroup(group, newGroup);
        assertGroup(group, groupDao.select(Lists.newArrayList(newGroup.getId())).get(0));
    }

    private GroupModel query(GroupModel group) {
        return query(group, 1);
    }

    private GroupModel query(GroupModel group, int expected) {
        List<GroupModel> groups = groupDao.select(group);
        Assert.assertEquals(expected, groups.size());
        if (expected >= 1) {
            return groups.get(0);
        }
        return null;
    }

    private final static SecureRandom random = new SecureRandom();
    private GroupModel newGroup() {
        GroupModel group = new GroupModel();
        group.setAppId(new BigInteger(130, random).toString(32));
        group.setName(new BigInteger(130, random).toString(32));
        group.setRegionId(new BigInteger(130, random).toString(32));
        group.setServiceId(new BigInteger(130, random).toString(32));
        group.setZoneId(new BigInteger(130, random).toString(32));
        group.setDescription(new BigInteger(130, random).toString(32));
        group.setStatus(new BigInteger(130, random).toString(32));
        return group;
    }

    private void assertGroup(GroupModel expected, GroupModel actual) {
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getServiceId(), actual.getServiceId());
        Assert.assertEquals(expected.getRegionId(), actual.getRegionId());
        Assert.assertEquals(expected.getZoneId(), actual.getZoneId());
        Assert.assertEquals(expected.getAppId(), actual.getAppId());
        Assert.assertEquals(expected.getDescription(), actual.getDescription());
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
    }
}
