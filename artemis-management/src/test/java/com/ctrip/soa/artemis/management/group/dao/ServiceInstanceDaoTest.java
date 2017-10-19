package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.group.model.ServiceInstanceModel;
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
public class ServiceInstanceDaoTest {
    private final ServiceInstanceDao serviceInstanceDao = ServiceInstanceDao.INSTANCE;
    @Test
    public void testQuery() throws Exception {
        serviceInstanceDao.insertOrUpdate(newServiceInstance());
        serviceInstanceDao.insertOrUpdate(newServiceInstance());
        Assert.assertTrue(serviceInstanceDao.query().size() >= 2);
    }

    @Test
    public void testDelete() throws Exception {
        ServiceInstanceModel serviceInstance = newServiceInstance();
        serviceInstanceDao.insertOrUpdate(serviceInstance);
        ServiceInstanceModel newServiceInstance = query(serviceInstance);
        assertServiceInstance(serviceInstance, query(serviceInstance));
        serviceInstanceDao.delete(newServiceInstance.getId());
        Assert.assertNull(query(serviceInstance, 0));
    }

    @Test
    public void testDeleteByFilters() throws Exception {
        ServiceInstanceModel serviceInstance1 = newServiceInstance();
        ServiceInstanceModel serviceInstance2 = newServiceInstance();
        serviceInstanceDao.insertOrUpdate(serviceInstance1, serviceInstance2);
        query(serviceInstance1, 1);
        query(serviceInstance2, 1);
        serviceInstanceDao.deleteByFilters(Lists.newArrayList(serviceInstance1, serviceInstance2));
        query(serviceInstance1, 0);
        query(serviceInstance2, 0);
    }

    @Test
    public void testInsertOrUpdate() throws Exception {
        ServiceInstanceModel serviceInstance = newServiceInstance();
        serviceInstanceDao.insertOrUpdate(serviceInstance);
        serviceInstanceDao.insertOrUpdate(serviceInstance);
        assertServiceInstance(serviceInstance, query(serviceInstance));
    }

    private ServiceInstanceModel query(ServiceInstanceModel filter) {
        return query(filter, 1);
    }

    private ServiceInstanceModel query(ServiceInstanceModel filter, int expectedSize) {
        List<ServiceInstanceModel> newServiceInstance = serviceInstanceDao.query("service_id = ? and instance_id=?", Lists.newArrayList(filter.getServiceId(), filter.getInstanceId()));
        Assert.assertEquals(expectedSize, newServiceInstance.size());
        if (newServiceInstance.size() > 0) {
            return newServiceInstance.get(0);
        }
        return null;
    }

    private void assertServiceInstance(ServiceInstanceModel expected, ServiceInstanceModel actual) {
        Assert.assertEquals(expected.getServiceId(), actual.getServiceId());
        Assert.assertEquals(expected.getInstanceId(), actual.getInstanceId());
        Assert.assertEquals(expected.getIp(), actual.getIp());
        Assert.assertEquals(expected.getMachineName(), actual.getMachineName());
        Assert.assertEquals(expected.getMetadata(), actual.getMetadata());
        Assert.assertEquals(expected.getPort(), actual.getPort());
        Assert.assertEquals(expected.getProtocol(), actual.getProtocol());
        Assert.assertEquals(expected.getRegionId(), actual.getRegionId());
        Assert.assertEquals(expected.getZoneId(), actual.getZoneId());
        Assert.assertEquals(expected.getHealthCheckUrl(), actual.getHealthCheckUrl());
        Assert.assertEquals(expected.getUrl(), actual.getUrl());
        Assert.assertEquals(expected.getDescription(), actual.getDescription());
        Assert.assertEquals(expected.getGroupId(), actual.getGroupId());
    }

    private final static SecureRandom random = new SecureRandom();
    private ServiceInstanceModel newServiceInstance() {
        ServiceInstanceModel serviceInstance = new ServiceInstanceModel();
        serviceInstance.setServiceId(new BigInteger(130, random).toString(32));
        serviceInstance.setInstanceId(new BigInteger(130, random).toString(32));
        serviceInstance.setIp(new BigInteger(130, random).toString(32));
        serviceInstance.setMachineName(new BigInteger(130, random).toString(32));
        serviceInstance.setMetadata(new BigInteger(130, random).toString(32));
        serviceInstance.setProtocol(new BigInteger(60, random).toString(32));
        serviceInstance.setRegionId(new BigInteger(60, random).toString(32));
        serviceInstance.setZoneId(new BigInteger(60, random).toString(32));
        serviceInstance.setHealthCheckUrl(new BigInteger(130, random).toString(32));
        serviceInstance.setUrl(new BigInteger(130, random).toString(32));
        serviceInstance.setDescription(new BigInteger(130, random).toString(32));
        serviceInstance.setGroupId(new BigInteger(130, random).toString(32));
        return serviceInstance;
    }
}
