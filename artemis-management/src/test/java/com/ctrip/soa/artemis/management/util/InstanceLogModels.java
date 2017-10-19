package com.ctrip.soa.artemis.management.util;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.junit.Assert;

import com.ctrip.soa.artemis.management.dao.InstanceLogModel;
import com.ctrip.soa.artemis.management.log.InstanceOperationLog;

/**
 * Created by fang_j on 10/07/2016.
 */
public class InstanceLogModels {
    private final static SecureRandom random = new SecureRandom();
    public static InstanceLogModel newInstanceLogModel() {
        final InstanceLogModel instance = new InstanceLogModel();
        instance.setRegionId(Constants.regionId);
        instance.setServiceId(new BigInteger(130, random).toString(32));
        instance.setInstanceId(new BigInteger(130, random).toString(32));
        instance.setOperation(new BigInteger(130, random).toString(32));
        instance.setOperatorId(new BigInteger(130, random).toString(32));
        instance.setToken(new BigInteger(130, random).toString(32));
        instance.setExtensions(new BigInteger(130, random).toString(32));
        instance.setComplete(random.nextBoolean());
        return instance;
    }

    public static void assertInstanceLog(final InstanceLogModel expected, final InstanceOperationLog actual) {
        Assert.assertEquals(expected.getRegionId(), actual.getRegionId());
        Assert.assertEquals(expected.getServiceId(), actual.getServiceId());
        Assert.assertEquals(expected.getInstanceId(), actual.getInstanceId());
        Assert.assertEquals(expected.getOperation(), actual.getOperation());
        Assert.assertEquals(expected.getOperatorId(), actual.getOperatorId());
        Assert.assertEquals(expected.getToken(), actual.getToken());
        Assert.assertEquals(expected.getExtensions(), actual.getExtensions());
        Assert.assertEquals(expected.isComplete(), actual.isComplete());
    }
}
