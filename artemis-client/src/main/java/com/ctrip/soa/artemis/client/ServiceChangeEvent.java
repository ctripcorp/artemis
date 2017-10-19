package com.ctrip.soa.artemis.client;

import com.ctrip.soa.artemis.Service;

/**
 * Created by fang_j on 10/07/2016.
 */
public interface ServiceChangeEvent {

    String changeType();

    Service changedService();

}