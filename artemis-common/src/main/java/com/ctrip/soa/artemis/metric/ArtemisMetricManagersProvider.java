package com.ctrip.soa.artemis.metric;

import com.ctrip.soa.caravan.common.metric.AuditMetricManager;
import com.ctrip.soa.caravan.common.metric.EventMetricManager;
import com.ctrip.soa.caravan.common.metric.StatusMetricManager;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface ArtemisMetricManagersProvider {

    EventMetricManager getEventMetricManager();

    AuditMetricManager getValueMetricManager();

    StatusMetricManager<Double> getStatusMetricManager();
}
