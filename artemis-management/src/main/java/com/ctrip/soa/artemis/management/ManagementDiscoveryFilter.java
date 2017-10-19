package com.ctrip.soa.artemis.management;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.discovery.DiscoveryConfig;
import com.ctrip.soa.artemis.discovery.DiscoveryFilter;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ManagementDiscoveryFilter implements DiscoveryFilter {

    private static final Logger logger = LoggerFactory.getLogger(ManagementDiscoveryFilter.class);

    private static ManagementDiscoveryFilter _instance;

    public static ManagementDiscoveryFilter getInstance() {
        if (_instance == null) {
            synchronized (ManagementDiscoveryFilter.class) {
                if (_instance == null)
                    _instance = new ManagementDiscoveryFilter();
            }
        }

        return _instance;
    }

    private ManagementRepository _managementRepository = ManagementRepository.getInstance();

    private ManagementDiscoveryFilter() {

    }

    @Override
    public void filter(Service service, DiscoveryConfig discoveryConfig) {
        if (service == null)
            return;
        service.setInstances(removeDownInstances(service.getInstances()));
        service.setLogicInstances(removeDownInstances(service.getLogicInstances()));
    }

    private List<Instance> removeDownInstances(List<Instance> instances) {
        List<Instance> result = new ArrayList<>();
        if (!CollectionValues.isNullOrEmpty(instances)) {
            for (Instance instance : instances) {
                if (_managementRepository.isInstanceDown(instance)) {
                    String message = String.format("Instance %s removed by management discovery filter.", instance.toString());
                    logger.info(message);
                    continue;
                }
                result.add(instance);
            }
        }
        return result;
    }

}
