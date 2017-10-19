package com.ctrip.soa.artemis.trace;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.configuration.Property;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import com.ctrip.soa.caravan.common.trace.NullTraceFactory;
import com.ctrip.soa.caravan.common.trace.Trace;
import com.ctrip.soa.caravan.common.trace.TraceFactory;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public final class ArtemisTraceFactory implements TraceFactory {

    private static final Logger _logger = LoggerFactory.getLogger(ArtemisTraceFactory.class);

    public static final TraceFactory INSTANCE = new ArtemisTraceFactory();

    private TypedProperty<Boolean> _enableTraceProperty = ArtemisConfig.properties()
            .getBooleanProperty("artemis.trace.enabled", false);

    private Property _factoryClassProperty = ArtemisConfig.properties()
            .getStringProperty("artemis.trace.factory-class");

    private TraceFactory _factory = NullTraceFactory.INSTANCE;

    private ArtemisTraceFactory() {
        String factoryClass = _factoryClassProperty.value();
        if (StringValues.isNullOrWhitespace(factoryClass)) {
            _logger.info("No trace factory class is configured.");
            return;
        }

        try {
            Class<?> clazz = Class.forName(factoryClass.trim());
            _factory = (TraceFactory) clazz.newInstance();
            _logger.info("Init TraceFactory for class " + factoryClass + " succeeded.");
        } catch (Throwable ex) {
            _logger.error("Init TraceFactory for class " + factoryClass + " failed.", ex);
        }
    }

    @Override
    public Trace newTrace(String identity) {
        return traceFactory().newTrace(identity);
    }

    @Override
    public Trace newTrace(String identity, Map<String, String> data) {
        return traceFactory().newTrace(identity, data);
    }

    private TraceFactory traceFactory() {
        return _enableTraceProperty.typedValue().booleanValue() ? _factory : NullTraceFactory.INSTANCE;
    }

}
