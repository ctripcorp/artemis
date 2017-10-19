package com.ctrip.soa.artemis.management.dao;

import java.util.List;

import com.ctrip.soa.artemis.ServerKey;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ctrip.soa.artemis.management.util.Constants;
import com.ctrip.soa.artemis.management.util.InstanceModels;
import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
@Transactional
public class InstanceDaoTest {
    InstanceDao instanceDao = InstanceDao.INSTANCE;

    @Test
    public void testInsert() {
        final InstanceModel instance1 = InstanceModels.newInstanceModel();
        final InstanceModel instance2 = InstanceModels.newInstanceModel();
        final List<InstanceModel> instances = Lists.newArrayList(instance1, instance2);
        instanceDao.insert(instance1, instance2);
        instanceDao.insert(instances);
        for (final InstanceModel instance : instances) {
            final List<InstanceModel> instanceModels = query(instance);
            Assert.assertTrue(instanceModels.size() == 1);
            InstanceModels.assertInstance(instance, instanceModels.get(0));
            instance.setOperatorId(InstanceModels.newInstanceModel().getOperatorId());
            instance.setToken(InstanceModels.newInstanceModel().getToken());
        }
        instanceDao.insert(instances);
        for (final InstanceModel instance : instances) {
            final List<InstanceModel> instanceModels = query(instance);
            Assert.assertTrue(instanceModels.size() == 1);
            InstanceModels.assertInstance(instance, instanceModels.get(0));
        }
    }

    @Test
    public void testQueryUncompletedInstance() {
        final InstanceModel instance = InstanceModels.newInstanceModel();
        instanceDao.insert(instance);
        final List<InstanceModel> instanceModels = instanceDao.queryInstance(Constants.regionId, instance.getServiceId(), instance.getInstanceId());
        Assert.assertEquals(1, instanceModels.size());
    }

    @Test
    public void testQueryUncompletedInstances() {
        final List<InstanceModel> instances = Lists.newArrayList(InstanceModels.newInstanceModel(), InstanceModels.newInstanceModel());
        final List<String> instanceIds = InstanceModels.ids(instances);
        instanceDao.insert(instances);

        {
            final List<InstanceModel> instanceModels = instanceDao.queryInstances();
            Assert.assertTrue(instanceModels.size() >= instances.size());
            final List<String> ids = InstanceModels.ids(instanceModels);
            Assert.assertTrue(ids.containsAll(instanceIds));
        }

        {
            final List<InstanceModel> instanceModels = instanceDao.queryInstances(Constants.regionId);
            Assert.assertTrue(instanceModels.size() >= instances.size());
            final List<String> ids = InstanceModels.ids(instanceModels);
            Assert.assertTrue(ids.containsAll(instanceIds));
        }
    }

    @Test
    public void testDeleteById() {
        final InstanceModel instance = InstanceModels.newInstanceModel();
        instanceDao.insert(instance);
        List<InstanceModel> instanceModels = query(instance);
        Assert.assertEquals(1, instanceModels.size());
        instanceDao.delete(instanceModels.get(0).getId());
        instanceModels =  query(instance);
        Assert.assertEquals(0, instanceModels.size());
    }

    @Test
    public void testDeleteByInstance() {
        final InstanceModel instance1 = InstanceModels.newInstanceModel();
        final InstanceModel instance2 = InstanceModels.newInstanceModel();
        instanceDao.insert(instance1, instance2);
        Assert.assertTrue(query(instance1).size() == 1);
        Assert.assertTrue(query(instance2).size() == 1);
        instanceDao.delete(instance1, instance2);
        Assert.assertTrue(query(instance1).size() == 0);
        Assert.assertTrue(query(instance2).size() == 0);
    }

    @Test
    public void testDestroyServers() {
        final InstanceModel instance1 = InstanceModels.newInstanceModel();
        final InstanceModel instance2 = InstanceModels.newInstanceModel();
        final List<InstanceModel> instances = Lists.newArrayList(instance1, instance2);
        instanceDao.insert(instances);
        List<ServerKey> serverKeys = Lists.newArrayList();
        for (final InstanceModel instance : instances) {
            serverKeys.add(new ServerKey(instance.getRegionId(), instance.getInstanceId()));
            List<InstanceModel> instanceModels = query(instance);
            Assert.assertEquals(1, instanceModels.size());
        }
        instanceDao.destroyServers(serverKeys);
        for (final InstanceModel instance : instances) {
            List<InstanceModel> instanceModels = query(instance);
            Assert.assertEquals(0, instanceModels.size());
        }
    }

    private List<InstanceModel> query(final InstanceModel instance) {
        return  instanceDao.query("region_id=? and service_id=? and instance_id=? and operation=?",
                instance.getRegionId(), instance.getServiceId(), instance.getInstanceId(), instance.getOperation());
    }
}