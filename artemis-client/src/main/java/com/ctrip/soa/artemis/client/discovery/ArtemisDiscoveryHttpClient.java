package com.ctrip.soa.artemis.client.discovery;

import java.util.List;

import com.ctrip.soa.artemis.ResponseStatus;
import org.springframework.util.CollectionUtils;

import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.client.common.ArtemisClientConfig;
import com.ctrip.soa.artemis.client.common.ArtemisHttpClient;
import com.ctrip.soa.artemis.config.DeploymentConfig;
import com.ctrip.soa.artemis.config.RestPaths;
import com.ctrip.soa.artemis.discovery.DiscoveryConfig;
import com.ctrip.soa.artemis.discovery.LookupRequest;
import com.ctrip.soa.artemis.discovery.LookupResponse;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ArtemisDiscoveryHttpClient extends ArtemisHttpClient {
    public ArtemisDiscoveryHttpClient(final ArtemisClientConfig config) {
        super(config, config.key("discovery"));
    }

    public Service getService(final DiscoveryConfig discoveryConfig) {
        Preconditions.checkArgument(discoveryConfig != null, "discoveryConfig");
        final List<Service> services = getServices(Lists.newArrayList(discoveryConfig));
        if (services.size() > 0) {
            return services.get(0);
        }
        throw new RuntimeException("not found any service by discoveryConfig:" + discoveryConfig);
    }

    public List<Service> getServices(final List<DiscoveryConfig> discoveryConfigs) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(discoveryConfigs), "discoveryConfigs should not be null or empty");

        final LookupRequest request = new LookupRequest(discoveryConfigs, DeploymentConfig.regionId(), DeploymentConfig.zoneId());
        final LookupResponse response = this.request(RestPaths.DISCOVERY_LOOKUP_FULL_PATH, request, LookupResponse.class);
        ResponseStatus status = response.getResponseStatus();
        logEvent(status, "discovery", "lookup");
        if (ResponseStatusUtil.isSuccess(status))
            return response.getServices();

        throw new RuntimeException("lookup services failed. " + status);
    }
}