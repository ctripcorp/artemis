package com.ctrip.soa.artemis.metric;

import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.caravan.common.metric.AuditMetricManager;
import com.ctrip.soa.caravan.common.metric.EventMetricManager;
import com.ctrip.soa.caravan.common.metric.StatusMetricManager;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class ArtemisMetricManagers {

    private static final Logger _logger = LoggerFactory.getLogger(ArtemisMetricManagers.class);

    public static final ArtemisMetricManagers DEFAULT = createDefault();

    private static ArtemisMetricManagers createDefault() {
        TypedProperty<String> providerProperty = ArtemisConfig.properties().getStringProperty("artemis.metric.default.managers-provider");

        ArtemisMetricManagersProvider provider = null;
        String providerType = StringValues.trim(providerProperty.typedValue());
        if (!StringValues.isNullOrWhitespace(providerType)) {
            try {
                Class<?> clazz = Class.forName(providerType);
                provider = (ArtemisMetricManagersProvider) clazz.newInstance();
            } catch (Throwable ex) {
                _logger.error("Init metric managers provider failed.", ex);
            }
        }

        if (provider == null)
            provider = NullArtemisMetricManagersProvider.INSTANCE;

        _logger.info("Inited defualt ArtemisMetricManagers with provider " + provider.getClass().getName());
        return new ArtemisMetricManagers(provider);
    }

    private ArtemisMetricManagersProvider _provider;

    public ArtemisMetricManagers(ArtemisMetricManagersProvider provider) {
        NullArgumentChecker.DEFAULT.check(provider, "provider");
        _provider = provider;
    }

    public EventMetricManager eventMetricManager() {
        return _provider.getEventMetricManager();
    }

    public AuditMetricManager valueMetricManager() {
        return _provider.getValueMetricManager();
    }

    public StatusMetricManager<Double> getStatusMetricManager() {
        return _provider.getStatusMetricManager();
    }
}
