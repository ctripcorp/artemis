package com.ctrip.soa.artemis.management.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;

import com.ctrip.soa.artemis.management.dao.InstanceModel;
import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
public class InstanceModels {
    private final static SecureRandom random = new SecureRandom();
    public static InstanceModel newInstanceModel() {
        final InstanceModel instance = new InstanceModel();
        instance.setRegionId(Constants.regionId);
        instance.setServiceId(new BigInteger(130, random).toString(32));
        instance.setInstanceId(new BigInteger(130, random).toString(32));
        instance.setOperation(new BigInteger(130, random).toString(32));
        instance.setOperatorId(new BigInteger(130, random).toString(32));
        instance.setToken(new BigInteger(130, random).toString(32));
        return instance;
    }

    public static void assertInstance(final InstanceModel expected, final InstanceModel actual) {
        Assert.assertEquals(expected.getRegionId(), actual.getRegionId());
        Assert.assertEquals(expected.getServiceId(), actual.getServiceId());
        Assert.assertEquals(expected.getInstanceId(), actual.getInstanceId());
        Assert.assertEquals(expected.getOperation(), actual.getOperation());
        Assert.assertEquals(expected.getOperatorId(), actual.getOperatorId());
        Assert.assertEquals(expected.getToken(), actual.getToken());
    }

    public static String id(final InstanceModel instanceModel) {
        return instanceModel.getRegionId() + "/" + instanceModel.getServiceId() + "/" + instanceModel.getInstanceId() + "/" + instanceModel.getOperation();
    }

    public static List<String> ids(final Collection<InstanceModel> instanceModels) {
        final List<String> ids = Lists.newArrayList();
        for (final InstanceModel instanceModel : instanceModels) {
            ids.add(id(instanceModel));
        }
        return ids;
    }
}
