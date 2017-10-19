package com.ctrip.soa.artemis;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface HasResponseStatus {

    ResponseStatus getResponseStatus();

    void setResponseStatus(ResponseStatus responseStatus);
}
