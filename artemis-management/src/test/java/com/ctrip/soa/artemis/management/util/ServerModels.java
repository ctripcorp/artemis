package com.ctrip.soa.artemis.management.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;

import com.ctrip.soa.artemis.management.dao.ServerModel;
import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServerModels {
    private final static SecureRandom random = new SecureRandom();
    public static ServerModel newServerModel() {
        final ServerModel server = new ServerModel();
        server.setRegionId(Constants.regionId);
        server.setServerId(new BigInteger(130, random).toString(32));
        server.setOperation(new BigInteger(130, random).toString(32));
        server.setOperatorId(new BigInteger(130, random).toString(32));
        server.setToken(new BigInteger(130, random).toString(32));
        return server;
    }

    public static void assertServer(final ServerModel expected, final ServerModel actual) {
        Assert.assertEquals(expected.getRegionId(), actual.getRegionId());
        Assert.assertEquals(expected.getServerId(), actual.getServerId());
        Assert.assertEquals(expected.getOperation(), actual.getOperation());
        Assert.assertEquals(expected.getOperatorId(), actual.getOperatorId());
        Assert.assertEquals(expected.getToken(), actual.getToken());
    }

    public static String id(final ServerModel serverModel) {
        return serverModel.getRegionId() + "/" + serverModel.getServerId() + "/" + serverModel.getOperation();
    }

    public static List<String> ids(final Collection<ServerModel> serverModels) {
        final List<String> ids = Lists.newArrayList();
        for (final ServerModel serverModel : serverModels) {
            ids.add(id(serverModel));
        }
        return ids;
    }
}
