package com.ctrip.soa.artemis.cluster;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface NodeInitializer {

    public enum TargetType {
        REGISTRY, DISCOVERY
    }

    TargetType target();

    boolean initialize();

}
