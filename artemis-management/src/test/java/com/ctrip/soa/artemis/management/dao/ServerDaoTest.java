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
import com.ctrip.soa.artemis.management.util.ServerModels;
import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
@Transactional
public class ServerDaoTest {
    private final ServerDao serverDao = ServerDao.INSTANCE;
    @Test
    public void testInsert() {
        final ServerModel server1 = ServerModels.newServerModel();
        final ServerModel server2 = ServerModels.newServerModel();
        final List<ServerModel> servers = Lists.newArrayList(server1, server2);
        serverDao.insert(server1, server2);
        serverDao.insert(servers);
        for (final ServerModel server : servers) {
            final List<ServerModel> serverModels = query(server);
            Assert.assertTrue(serverModels.size() == 1);
            ServerModels.assertServer(server, serverModels.get(0));
            server.setToken(ServerModels.newServerModel().getToken());
            server.setOperatorId(ServerModels.newServerModel().getOperatorId());
        }
        serverDao.insert(servers);
        for (final ServerModel server : servers) {
            final List<ServerModel> serverModels = query(server);
            Assert.assertTrue(serverModels.size() == 1);
            ServerModels.assertServer(server, serverModels.get(0));
        }
    }

    @Test
    public void testQueryUncompletedServer() {
        final ServerModel server = ServerModels.newServerModel();
        serverDao.insert(server);
        final List<ServerModel> serverModels = serverDao.queryServer(Constants.regionId, server.getServerId());
        Assert.assertTrue(serverModels.size() == 1);
    }

    @Test
    public void testQueryUncompletedServers() {
        final List<ServerModel> servers = Lists.newArrayList(ServerModels.newServerModel(), ServerModels.newServerModel());
        final List<String> serverIds = ServerModels.ids(servers);
        serverDao.insert(servers);

        {
            final List<ServerModel> serverModels = serverDao.queryServers();
            Assert.assertTrue(serverModels.size() >= servers.size());
            final List<String> ids = ServerModels.ids(serverModels);
            Assert.assertTrue(ids.containsAll(serverIds));
        }

        {
            final List<ServerModel> serverModels = serverDao.queryServers(Constants.regionId);
            Assert.assertTrue(serverModels.size() >= servers.size());
            final List<String> ids = ServerModels.ids(serverModels);
            Assert.assertTrue(ids.containsAll(serverIds));
        }
    }

    @Test
    public void testDeleteById() {
        final ServerModel server = ServerModels.newServerModel();
        serverDao.insert(server);
        List<ServerModel> serverModels = query(server);
        Assert.assertEquals(1, serverModels.size());
        serverDao.delete(serverModels.get(0).getId());
        serverModels = query(server);
        Assert.assertEquals(0, serverModels.size());
    }

    @Test
    public void testDeleteByServer() {
        final ServerModel server1 = ServerModels.newServerModel();
        final ServerModel server2 = ServerModels.newServerModel();
        serverDao.insert(server1, server2);
        Assert.assertTrue(query(server1).size() == 1);
        Assert.assertTrue(query(server2).size() == 1);
        serverDao.delete(server1, server2);
        Assert.assertTrue(query(server1).size() == 0);
        Assert.assertTrue(query(server2).size() == 0);
    }

    @Test
    public void testDestroyServers() {
        final ServerModel server1 = ServerModels.newServerModel();
        final ServerModel server2 = ServerModels.newServerModel();
        serverDao.insert(server1, server2);
        Assert.assertTrue(query(server1).size() == 1);
        Assert.assertTrue(query(server2).size() == 1);
        List<ServerKey> serverKeys = Lists.newArrayList(new ServerKey(server1.getRegionId(), server1.getServerId()),
                new ServerKey(server2.getRegionId(), server2.getServerId()));
        serverDao.destroyServers(serverKeys);
        Assert.assertTrue(query(server1).size() == 0);
        Assert.assertTrue(query(server2).size() == 0);
    }

    private List<ServerModel> query(final ServerModel server) {
        return serverDao.query("region_id=? and server_id=? and operation=?",
                server.getRegionId(), server.getServerId(), server.getOperation());
    }
}
