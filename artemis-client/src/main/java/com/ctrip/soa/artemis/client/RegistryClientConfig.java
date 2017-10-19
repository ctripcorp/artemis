package com.ctrip.soa.artemis.client;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
public class RegistryClientConfig {    
    private final List<RegistryFilter> registryFilters;
    
    public RegistryClientConfig() {
        this.registryFilters = Lists.newArrayList();
    }
    
    public RegistryClientConfig(List<RegistryFilter> registryFilters) {
        Preconditions.checkArgument(registryFilters != null, "registry filters");
        this.registryFilters = registryFilters;
    }
    
    public List<RegistryFilter> getRegistryFilters() {
        return registryFilters;
    }
}
