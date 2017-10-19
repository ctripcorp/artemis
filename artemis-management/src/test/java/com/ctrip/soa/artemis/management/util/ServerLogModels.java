package com.ctrip.soa.artemis.management.util;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.junit.Assert;

import com.ctrip.soa.artemis.management.dao.ServerLogModel;
import com.ctrip.soa.artemis.management.log.ServerOperationLog;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServerLogModels {
    private final static SecureRandom random = new SecureRandom();
    public static ServerLogModel newServerLogModel() {
        final ServerLogModel log = new ServerLogModel();
        log.setRegionId(Constants.regionId);
        log.setServerId(new BigInteger(130, random).toString(32));
        log.setOperation(new BigInteger(130, random).toString(32));
        log.setOperatorId(new BigInteger(130, random).toString(32));
        log.setToken(new BigInteger(130, random).toString(32));
        log.setExtensions(new BigInteger(130, random).toString(32));
        log.setComplete(random.nextBoolean());
        return log;
    }

    public static void assertServerLog(final ServerLogModel expected, final ServerOperationLog actual) {
        Assert.assertEquals(expected.getRegionId(), actual.getRegionId());
        Assert.assertEquals(expected.getServerId(), actual.getServerId());
        Assert.assertEquals(expected.getOperation(), actual.getOperation());
        Assert.assertEquals(expected.getOperatorId(), actual.getOperatorId());
        Assert.assertEquals(expected.getToken(), actual.getToken());
        Assert.assertEquals(expected.getExtensions(), actual.getExtensions());
        Assert.assertEquals(expected.isComplete(), actual.isComplete());
    }
}
