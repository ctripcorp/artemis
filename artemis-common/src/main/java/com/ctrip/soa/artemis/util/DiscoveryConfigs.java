package com.ctrip.soa.artemis.util;

import com.ctrip.soa.artemis.discovery.DiscoveryConfig;
import com.ctrip.soa.caravan.common.value.checker.ValueCheckers;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by fang_j on 10/07/2016.
 */
public class DiscoveryConfigs {
    public static final String APP_ID = "appid";
    public static final String SUB_ENV = "subenv";

    public static String getAppId(DiscoveryConfig config) {
        return get(config, APP_ID);
    }

    public static String getSubEnv(DiscoveryConfig config) {
        return get(config, SUB_ENV);
    }

    public static void setAppId(DiscoveryConfig config, String value) {
        set(config, APP_ID, value);
    }

    public static void setSubEnv(DiscoveryConfig config, String value) {
        set(config, SUB_ENV, value);
    }

    private static String get(DiscoveryConfig config, String key) {
        if (config == null) {
            return null;
        }
        
        if (config.getDiscoveryData() == null) {
            return null;
        }
        return config.getDiscoveryData().get(key);
    }

    private static void set(DiscoveryConfig config, String key, String value) {
        ValueCheckers.notNull(config, "config");
        ValueCheckers.notNull(key, "key");
        ValueCheckers.notNullOrWhiteSpace(value, "value");
        Map<String, String> data = config.getDiscoveryData();
        if (data == null) {
            data = Maps.newHashMap();
            config.setDiscoveryData(data);
        }
        data.put(key, value);
    }
}
