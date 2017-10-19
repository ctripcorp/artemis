package com.ctrip.soa.artemis.lease;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.metric.ArtemisMetricManagers;
import com.ctrip.soa.artemis.metric.MetricNames;
import com.ctrip.soa.caravan.common.concurrent.collect.circularbuffer.timebucket.CounterBuffer;
import com.ctrip.soa.caravan.common.concurrent.collect.circularbuffer.timebucket.TimeBufferConfig;
import com.ctrip.soa.caravan.common.metric.EventMetric;
import com.ctrip.soa.caravan.common.metric.MetricConfig;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.common.value.corrector.ValueCorrector;
import com.ctrip.soa.caravan.common.value.parser.IntegerParser;
import com.ctrip.soa.caravan.configuration.dynamic.PropertyChangeEvent;
import com.ctrip.soa.caravan.configuration.dynamic.PropertyChangeListener;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import com.ctrip.soa.caravan.configuration.typed.dynamic.TypedDynamicProperty;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThread;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThreadConfig;
import com.google.common.collect.ImmutableMap;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class LeaseUpdateSafeChecker {

    private static final Logger _logger = LoggerFactory.getLogger(LeaseUpdateSafeChecker.class);
    private static final String LEASE_UPDATE_IDENTITY = "lease_update";

    private TypedDynamicProperty<Boolean> _enabledProperty;
    private TypedDynamicProperty<Integer> _timeWindowProperty;
    private TypedProperty<Integer> _percentageThresholdProperty;
    private TypedProperty<Integer> _maxCountThresholdProperty;
    private TypedProperty<Integer> _maxCountResetIntervalProperty;
    private volatile CounterBuffer<String> _counterBuffer;
    private volatile long _maxCount;
    private volatile long _maxCountLastUpdateTime;
    private volatile boolean _isSafe = true;
    private DynamicScheduledThread _safeCheckThread;

    private EventMetric _eventMetric;

    private LeaseManager<?> _leaseManager;

    private String _safeCheckerId;

    private ValueCorrector<Integer> _timeWindowCorrector = new ValueCorrector<Integer>() {
        @Override
        public Integer correct(Integer value) {
            if (value == null || value < 10 * 1000)
                return 10 * 1000;

            if (value > 5 * 60 * 1000)
                return 5 * 60 * 1000;

            int m = value % 1000;
            if (m != 0)
                value = value - m;

            return value;
        }
    };

    public LeaseUpdateSafeChecker(LeaseManager<?> leaseManager) {
        NullArgumentChecker.DEFAULT.check(leaseManager, "leaseManager");
        _leaseManager = leaseManager;
        _safeCheckerId = _leaseManager.managerId() + ".lease-manager.lease-update-safe-checker";

        Map<String, String> metadata = ImmutableMap.of(MetricNames.METRIC_NAME_KEY_DISTRIBUTION, _safeCheckerId + ".event.distribution");
        _eventMetric = ArtemisMetricManagers.DEFAULT.eventMetricManager().getMetric(_safeCheckerId + ".event", new MetricConfig(metadata));

        initConfig();

        scheduleCheckTask();
    }

    public long maxCount() {
        return _maxCount;
    }

    public long maxCountLastUpdateTime() {
        return _maxCountLastUpdateTime;
    }

    public long countLastTimeWindow() {
        return _counterBuffer.getCount(LEASE_UPDATE_IDENTITY);
    }

    public boolean isSafe() {
        return _isSafe || !isEnabled();
    }

    public boolean isEnabled() {
        return _enabledProperty.typedValue().booleanValue();
    }

    protected void markUpdate() {
        _counterBuffer.incrementCount(LEASE_UPDATE_IDENTITY);
    }

    private void initConfig() {
        _enabledProperty = ArtemisConfig.properties().getBooleanProperty(_safeCheckerId + ".enabled", true);
        _timeWindowProperty = ArtemisConfig.properties().getProperty(_safeCheckerId + ".time-window", IntegerParser.DEFAULT, _timeWindowCorrector);
        _percentageThresholdProperty = ArtemisConfig.properties().getIntProperty(_safeCheckerId + ".percentage-threshold", 85, 50, 100);
        _maxCountThresholdProperty = ArtemisConfig.properties().getIntProperty(_safeCheckerId + ".max-count-threshold", 50, 0, 1000 * 1000);
        _maxCountResetIntervalProperty = ArtemisConfig.properties().getIntProperty(_safeCheckerId + ".max-count-reset-interval", 10 * 60 * 1000, 1 * 60 * 1000,
                24 * 60 * 60 * 1000);

        _timeWindowProperty.addChangeListener(new PropertyChangeListener() {
            @Override
            public void onChange(PropertyChangeEvent event) {
                resetLeaseUpdateCounterBuffer();
            }
        });

        _enabledProperty.addChangeListener(new PropertyChangeListener() {
            @Override
            public void onChange(PropertyChangeEvent event) {
                resetMaxCount();
            }
        });
    }

    private void scheduleCheckTask() {
        resetLeaseUpdateCounterBuffer();
        DynamicScheduledThreadConfig dynamicScheduledThreadConfig = new DynamicScheduledThreadConfig(ArtemisConfig.properties(),
                new RangePropertyConfig<Integer>(1000, 100, 60 * 1000), new RangePropertyConfig<Integer>(1000, 100, 60 * 1000));
        _safeCheckThread = new DynamicScheduledThread(_safeCheckerId, new Runnable() {
            @Override
            public void run() {
                try {
                    safeCheck();
                } catch (Throwable ex) {
                    _logger.error("safe check failed.", ex);
                }
            }
        }, dynamicScheduledThreadConfig);
        _safeCheckThread.setDaemon(true);
        _safeCheckThread.start();
    }

    private void resetLeaseUpdateCounterBuffer() {
        int timeWindow = _timeWindowProperty.typedValue();
        _counterBuffer = new CounterBuffer<>(new TimeBufferConfig(timeWindow, 1000));
        resetMaxCount();
        _logger.info("counterBuffer updated. TimeWindow: " + timeWindow);
    }

    private void safeCheck() {
        long leaseUpdateCountLastTimeWindow = countLastTimeWindow();
        long maxCount = _maxCount;
        if (leaseUpdateCountLastTimeWindow > maxCount) {
            updateMaxCount(leaseUpdateCountLastTimeWindow);
            return;
        }

        if (maxCount < _maxCountThresholdProperty.typedValue().intValue())
            return;

        if (maxCount <= 0)
            return;

        long percentage = leaseUpdateCountLastTimeWindow * 100 / maxCount;
        if (percentage < _percentageThresholdProperty.typedValue().intValue()) {
            _isSafe = false;
            _eventMetric.addEvent("unsafe");
            String errorMessage = "Lease update count is too low! Maybe something bad happen! Renewal pencentage is: " + percentage;
            if (_enabledProperty.typedValue().booleanValue())
                _logger.error(errorMessage);
            else
                _logger.warn(errorMessage);

            return;
        }

        _isSafe = true;
        _eventMetric.addEvent("safe");

        if (System.currentTimeMillis() - _maxCountLastUpdateTime > _maxCountResetIntervalProperty.typedValue().intValue()) {
            _logger.warn("Lease Manager lease update max count is changed from {} to {} for {} ms update", _maxCount, leaseUpdateCountLastTimeWindow,
                    _maxCountResetIntervalProperty.typedValue().intValue());
            updateMaxCount(leaseUpdateCountLastTimeWindow);
        }
    }

    private void resetMaxCount() {
        updateMaxCount(0);
    }

    private void updateMaxCount(long maxCount) {
        _logger.info("Lease Manager lease update max count is changed from {} to {}", _maxCount, maxCount);
        _maxCount = maxCount;
        _maxCountLastUpdateTime = System.currentTimeMillis();
    }

}
