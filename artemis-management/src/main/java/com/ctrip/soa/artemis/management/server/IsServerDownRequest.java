package com.ctrip.soa.artemis.management.server;

import com.ctrip.soa.artemis.ServerKey;

/**
 * Created by fang_j on 10/07/2016.
 */
public class IsServerDownRequest {

    private ServerKey serverKey;

    public ServerKey getServerKey() {
        return serverKey;
    }

    public void setServerKey(ServerKey serverKey) {
        this.serverKey = serverKey;
    }

}
