package com.ctrip.soa.artemis.management.dao;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServerLogModel extends ServerModel {
    private boolean complete;
    private String extensions;

    public ServerLogModel() {
    }

    public ServerLogModel(String regionId, String serverId, String operation, String operatorId) {
        super(regionId, serverId, operation, operatorId, null);
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(final boolean complete) {
        this.complete = complete;
    }
    public String getExtensions() {
        return extensions;
    }
    public void setExtensions(final String extensions) {
        this.extensions = extensions;
    }

    public static ServerLogModel of(final ServerModel server, final boolean isComplete) {
        if (server == null) {
            return null;
        }
        final ServerLogModel log = new ServerLogModel();
        log.setRegionId(server.getRegionId());
        log.setServerId(server.getServerId());
        log.setOperation(server.getOperation());
        log.setOperatorId(server.getOperatorId());
        log.setToken(server.getToken());
        log.setComplete(isComplete);
        log.setExtensions("{}");
        return log;
    }

    public static List<ServerLogModel> of(final List<ServerModel> servers, final boolean isComplete) {
        if (CollectionUtils.isEmpty(servers)) {
            return Lists.newArrayList();
        }
        final List<ServerLogModel> logs = Lists.newArrayList();
        for (final ServerModel server : servers) {
            logs.add(ServerLogModel.of(server, isComplete));
        }
        return logs;
    }
}
