package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.group.model.GroupTagModel;
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
public class GroupTagDaoTest {
    private final GroupTagDao groupTagDao = GroupTagDao.INSTANCE;
    @Test
    public void testQuery() throws Exception {
        groupTagDao.insertOrUpdate(newGroupTag());
        groupTagDao.insertOrUpdate(newGroupTag());
        Assert.assertTrue(groupTagDao.query().size() >= 2);
    }

    @Test
    public void testDelete() throws Exception {
        GroupTagModel groupTag = newGroupTag();
        groupTagDao.insertOrUpdate(groupTag);
        assertGroupOperation(groupTag, query(groupTag));
        groupTagDao.delete(groupTag);
        Assert.assertNull(query(groupTag));
    }

    @Test
    public void testInsertOrUpdate() throws Exception {
        GroupTagModel GroupTag = newGroupTag();
        groupTagDao.insertOrUpdate(GroupTag);
        groupTagDao.insertOrUpdate(GroupTag);
        assertGroupOperation(GroupTag, query(GroupTag));
    }

    private GroupTagModel query(GroupTagModel GroupTag) {
        List<GroupTagModel> GroupTagFromQuery = groupTagDao.query("group_id = ? and tag=?", Lists.newArrayList(Long.toString(GroupTag.getGroupId()), GroupTag.getTag()));
        if (GroupTagFromQuery.size() == 1) {
            return GroupTagFromQuery.get(0);
        }
        return null;
    }

    private void assertGroupOperation(GroupTagModel expected, GroupTagModel actual) {
        Assert.assertEquals(expected.getGroupId(), actual.getGroupId());
        Assert.assertEquals(expected.getTag(), actual.getTag());
        Assert.assertEquals(expected.getValue(), actual.getValue());
    }

    private final static SecureRandom random = new SecureRandom();
    private GroupTagModel newGroupTag() {
        GroupTagModel GroupTag = new GroupTagModel();
        GroupTag.setGroupId(random.nextLong());
        GroupTag.setTag(new BigInteger(130, random).toString(32));
        GroupTag.setValue(new BigInteger(130, random).toString(32));
        return GroupTag;
    }
}
