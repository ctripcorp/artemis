package com.ctrip.soa.artemis.management;

import com.ctrip.soa.artemis.cluster.NodeInitializer;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ManagementInitializer implements NodeInitializer {

    public static final ManagementInitializer INSTANCE = new ManagementInitializer();

    private ManagementInitializer() {

    }

    @Override
    public TargetType target() {
        return TargetType.DISCOVERY;
    }

    @Override
    public boolean initialize() {
        return ManagementRepository.getInstance().isLastRefreshSuccess()
                && GroupRepository.getInstance().isLastRefreshSuccess();
    }

}
