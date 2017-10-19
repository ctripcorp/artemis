package com.ctrip.soa.artemis.config;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.soa.caravan.common.net.NetworkInterfaceManager;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.configuration.ConfigurationSource;
import com.ctrip.soa.caravan.configuration.facade.ConfigurationManagers;
import com.ctrip.soa.caravan.configuration.facade.ConfigurationSources;
import com.ctrip.soa.caravan.configuration.facade.TypedDynamicCachedCorrectedProperties;
import com.ctrip.soa.caravan.configuration.source.environmentvariable.EnvironmentVariableConfigurationSource;
import com.ctrip.soa.caravan.configuration.source.properties.PropertiesConfigurationSource;
import com.ctrip.soa.caravan.configuration.typed.dynamic.cached.corrected.TypedDynamicCachedCorrectedConfigurationManager;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public final class ArtemisConfig {

    private static final String PROPERTIES_FILE_NAME = "artemis";

    private static final String[] CASCADED_FACTORS = new String[] { NetworkInterfaceManager.INSTANCE.localhostIP() };

    private static TypedDynamicCachedCorrectedConfigurationManager _manager;
    private static TypedDynamicCachedCorrectedProperties _properties;

    static {
        List<ConfigurationSource> sources = new ArrayList<>();
        sources.add(new PropertiesConfigurationSource(1, PROPERTIES_FILE_NAME));
        if (!StringValues.isNullOrWhitespace(DeploymentConfig.deploymentEnv()))
            sources.add(new PropertiesConfigurationSource(2, PROPERTIES_FILE_NAME + "-" + DeploymentConfig.deploymentEnv()));

        for (int i = 0; i < sources.size(); i++) {
            sources.set(i, ConfigurationSources.newCascadedSource(sources.get(i), CASCADED_FACTORS));
        }

        sources.add(new EnvironmentVariableConfigurationSource(3));

        _manager = ConfigurationManagers.newTypedDynamicCachedCorrectedManager(sources.toArray(new ConfigurationSource[0]));
        _properties = new TypedDynamicCachedCorrectedProperties(_manager);
    }

    public static TypedDynamicCachedCorrectedConfigurationManager manager() {
        return _manager;
    }

    public static TypedDynamicCachedCorrectedProperties properties() {
        return _properties;
    }

}
