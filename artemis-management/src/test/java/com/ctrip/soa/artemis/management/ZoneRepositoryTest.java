package com.ctrip.soa.artemis.management;

import com.ctrip.soa.artemis.config.DeploymentConfig;
import com.ctrip.soa.artemis.management.common.OperationContext;
import com.ctrip.soa.artemis.management.zone.ZoneKey;
import com.ctrip.soa.artemis.management.zone.ZoneOperations;
import com.ctrip.soa.artemis.management.zone.dao.ZoneOperationDaoTest;
import com.ctrip.soa.artemis.management.zone.model.ZoneOperationModel;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
@Transactional
public class ZoneRepositoryTest {
    private ZoneRepository zoneRepository = ZoneRepository.getInstance();
    private  List<ZoneOperationModel> zoneOperations = Lists.newArrayList(ZoneOperationDaoTest.newZoneOperation(),
            ZoneOperationDaoTest.newZoneOperation(), ZoneOperationDaoTest.newZoneOperation());
    private OperationContext operationContext = new OperationContext();
    @Before
    public void setUp() throws Exception {
        zoneRepository.refreshCache();
        zoneRepository.stopRefresh();
        operationContext.setOperation("UP");
        operationContext.setOperatorId("zoneRepository");
        operationContext.setToken("zoneRepository");
        operationContext.setReason("zoneRepository test");
    }

    @Test
    public void testRefreshCache() {
        zoneRepository.operateGroupOperations(operationContext, zoneOperations, false);
        int allZoneOperationsSize = zoneRepository.getAllZoneOperations(DeploymentConfig.regionId()).size();
        zoneRepository.refreshCache();
        Assert.assertEquals(allZoneOperationsSize + zoneOperations.size(), zoneRepository.getAllZoneOperations(DeploymentConfig.regionId()).size());
        for (ZoneOperationModel model : zoneOperations) {
            ZoneKey zoneKey = new ZoneKey(model.getRegionId(), model.getServiceId(), model.getZoneId());
            ZoneOperations zoneOperations = zoneRepository.getZoneOperations(zoneKey);
            Assert.assertEquals(zoneKey, zoneOperations.getZoneKey());
            Assert.assertEquals(1, zoneOperations.getOperations().size());
            Assert.assertEquals(model.getOperation(), zoneOperations.getOperations().get(0));

            Assert.assertEquals(1, zoneRepository.getServiceZoneOperations(model.getServiceId()).size());
            zoneOperations = zoneRepository.getServiceZoneOperations(model.getServiceId()).get(0);
            Assert.assertEquals(zoneKey, zoneOperations.getZoneKey());
            Assert.assertEquals(1, zoneOperations.getOperations().size());
            Assert.assertEquals(model.getOperation(), zoneOperations.getOperations().get(0));
        }


        zoneRepository.operateGroupOperations(operationContext, zoneOperations, true);
        zoneRepository.refreshCache();
        Assert.assertEquals(allZoneOperationsSize, zoneRepository.getAllZoneOperations(DeploymentConfig.regionId()).size());
        for (ZoneOperationModel model : zoneOperations) {
            ZoneKey zoneKey = new ZoneKey(model.getRegionId(), model.getServiceId(), model.getZoneId());
            Assert.assertNull(zoneRepository.getZoneOperations(zoneKey));
            Assert.assertEquals(0, zoneRepository.getServiceZoneOperations(model.getServiceId()).size());
        }
    }
}
