package com.ctrip.soa.artemis.management.zone.dao;

import com.ctrip.soa.artemis.management.zone.model.ZoneOperationModel;
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
public class ZoneOperationDaoTest {
    ZoneOperationDao zoneOperationDao = ZoneOperationDao.INSTANCE;

    @Test
    public void testInsertOrUpdate() {
        ZoneOperationModel zone = newZoneOperation();
        zoneOperationDao.insertOrUpdate(zone);
        assertZoneOperation(zone, query(zone));
        zoneOperationDao.insertOrUpdate(zone);
        assertZoneOperation(zone, query(zone));
    }

    @Test
    public void testDelete() {
        ZoneOperationModel zone = newZoneOperation();
        zoneOperationDao.insertOrUpdate(zone);
        ZoneOperationModel copy1 = query(zone);
        zoneOperationDao.delete(zone);
        Assert.assertNull(query(zone, 0));
        zoneOperationDao.insertOrUpdate(zone);
        ZoneOperationModel copy2 = query(zone);
        assertZoneOperation(copy1, copy2);
    }

    @Test
    public void testSelect() {
        ZoneOperationModel zone = newZoneOperation();
        zoneOperationDao.insertOrUpdate(zone);
        ZoneOperationModel filter = new ZoneOperationModel();
        filter.setServiceId(zone.getServiceId());
        filter.setZoneId(zone.getZoneId());
        assertZoneOperation(zone, zoneOperationDao.select(filter).get(0));
    }

    private ZoneOperationModel query(ZoneOperationModel group) {
        return query(group, 1);
    }

    private ZoneOperationModel query(ZoneOperationModel zoneOperationModel, int expected) {
        List<ZoneOperationModel> zones = zoneOperationDao.select(zoneOperationModel);
        Assert.assertEquals(expected, zones.size());
        if (expected >= 1) {
            return zones.get(0);
        }
        return null;
    }

    private final static SecureRandom random = new SecureRandom();
    public static ZoneOperationModel newZoneOperation() {
        ZoneOperationModel zoneOperation = new ZoneOperationModel();
        zoneOperation.setServiceId(new BigInteger(130, random).toString(32));
        zoneOperation.setRegionId(new BigInteger(130, random).toString(32));
        zoneOperation.setZoneId(new BigInteger(130, random).toString(32));
        zoneOperation.setOperation(new BigInteger(130, random).toString(32));
        return zoneOperation;
    }

    public static void assertZoneOperation(ZoneOperationModel expected, ZoneOperationModel actual) {
        Assert.assertEquals(expected.getServiceId(), actual.getServiceId());
        Assert.assertEquals(expected.getRegionId(), actual.getRegionId());
        Assert.assertEquals(expected.getZoneId(), actual.getZoneId());
        Assert.assertEquals(expected.getOperation(), actual.getOperation());
    }
}
