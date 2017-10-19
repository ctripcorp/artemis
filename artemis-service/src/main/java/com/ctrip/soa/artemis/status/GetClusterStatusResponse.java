package com.ctrip.soa.artemis.status;

import java.util.List;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;
import com.ctrip.soa.artemis.cluster.ServiceNodeStatus;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class GetClusterStatusResponse implements HasResponseStatus {

    private List<ServiceNodeStatus> _nodesStatus;
    private int _nodeCount;
    private ResponseStatus _responseStatus;

    public GetClusterStatusResponse() {

    }

    public GetClusterStatusResponse(int nodeCount, List<ServiceNodeStatus> nodesStatus, ResponseStatus responseStatus) {
        setNodeCount(nodeCount);
        _nodesStatus = nodesStatus;
        _responseStatus = responseStatus;
    }

    public int getNodeCount() {
        return _nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        _nodeCount = nodeCount;
    }

    @Override
    public ResponseStatus getResponseStatus() {
        return _responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        _responseStatus = responseStatus;
    }

    public List<ServiceNodeStatus> getNodesStatus() {
        return _nodesStatus;
    }

    public void setNodesStatus(List<ServiceNodeStatus> nodesStatus) {
        _nodesStatus = nodesStatus;
    }

}
