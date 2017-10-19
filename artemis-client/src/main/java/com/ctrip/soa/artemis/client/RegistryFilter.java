package com.ctrip.soa.artemis.client;

import java.util.List;

import com.ctrip.soa.artemis.Instance;

/**
 * Created by fang_j on 10/07/2016.
 */
public interface RegistryFilter {
    
    String getRegistryFilterId();
    
    void filter(final List<Instance> instances);
    
}