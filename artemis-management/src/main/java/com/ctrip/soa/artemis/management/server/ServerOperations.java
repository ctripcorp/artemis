package com.ctrip.soa.artemis.management.server;

import java.util.List;

import com.ctrip.soa.artemis.ServerKey;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServerOperations {

    private ServerKey serverKey;
    private List<String> operations;

    public ServerOperations() {

    }

    public ServerOperations(ServerKey serverKey, List<String> operations) {
        this.serverKey = serverKey;
        this.operations = operations;
    }

    public ServerKey getServerKey() {
        return serverKey;
    }

    public void setServerKey(ServerKey serverKey) {
        this.serverKey = serverKey;
    }

    public List<String> getOperations() {
        return operations;
    }

    public void setOperations(List<String> operations) {
        this.operations = operations;
    }

}
