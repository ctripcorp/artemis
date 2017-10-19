package com.ctrip.soa.artemis.cluster;

import java.util.List;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class GetServiceNodesResponse implements HasResponseStatus {

    private List<ServiceNode> _nodes;
    private ResponseStatus _responseStatus;

    public GetServiceNodesResponse() {

    }

    public GetServiceNodesResponse(List<ServiceNode> nodes, ResponseStatus responseStatus) {
        _nodes = nodes;
        _responseStatus = responseStatus;
    }

    public List<ServiceNode> getNodes() {
        return _nodes;
    }

    public void setNodes(List<ServiceNode> _nodes) {
        this._nodes = _nodes;
    }

    @Override
    public ResponseStatus getResponseStatus() {
        return _responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus status) {
        _responseStatus = status;
    }

}
