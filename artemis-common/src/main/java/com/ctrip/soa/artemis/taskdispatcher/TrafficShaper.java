
package com.ctrip.soa.artemis.taskdispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctrip.soa.caravan.common.value.ConcurrentHashMapValues;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.common.value.checker.StringArgumentChecker;
import com.ctrip.soa.caravan.common.value.converter.MapValueConverter;
import com.ctrip.soa.caravan.common.value.converter.ValueConverter;
import com.ctrip.soa.caravan.common.value.corrector.DefaultValueCorrector;
import com.ctrip.soa.caravan.common.value.corrector.MapValueCorrector;
import com.ctrip.soa.caravan.common.value.corrector.PipelineCorrector;
import com.ctrip.soa.caravan.common.value.corrector.RangeCorrector;
import com.ctrip.soa.caravan.common.value.corrector.ValueCorrector;
import com.ctrip.soa.caravan.common.value.parser.IntegerParser;
import com.ctrip.soa.caravan.common.value.parser.MapParser;
import com.ctrip.soa.caravan.common.value.parser.ValueParser;
import com.ctrip.soa.caravan.configuration.typed.dynamic.TypedDynamicProperty;

class TrafficShaper {

    private static final String IDENTITY_FORMAT = ".traffic-shaper";

    private static final int DEFAULT_FAIL_DELAY = 10;

    private static final int MAX_FAIL_DELAY = 10 * 1000;

    private String _trafficShaperId;

    private ValueConverter<Map<String, String>, Map<TaskErrorCode, String>> _failDelayValueConverter = createValueConverter();
    private ValueParser<Map<TaskErrorCode, Integer>> _failDelayValueParser = createValueParser();
    private ValueCorrector<Map<TaskErrorCode, Integer>> _failDelayValueCorrector = createValueCorrector();
    private TypedDynamicProperty<Map<TaskErrorCode, Integer>> _failDelayProperty;

    private ConcurrentHashMap<TaskErrorCode, AtomicLong> _lastFailTimeMap = new ConcurrentHashMap<>();

    public TrafficShaper(String dispatchId) {
        StringArgumentChecker.DEFAULT.check(dispatchId, "dispatchId");

        _trafficShaperId = dispatchId + IDENTITY_FORMAT;

        _failDelayProperty = ArtemisConfig.manager().getProperty(_trafficShaperId + ".fail-delay", _failDelayValueParser, _failDelayValueCorrector);
    }

    public void markFail(TaskErrorCode errorCode) {
        NullArgumentChecker.DEFAULT.check(errorCode, "errorCode");

        AtomicLong lastResultTime = ConcurrentHashMapValues.getOrAdd(_lastFailTimeMap, errorCode, new Func<AtomicLong>() {
            @Override
            public AtomicLong execute() {
                return new AtomicLong();
            }
        });
        lastResultTime.set(System.currentTimeMillis());
    }

    public int transmissionDelay() {
        for (TaskErrorCode errorCode : _lastFailTimeMap.keySet()) {
            AtomicLong lastResultTime = _lastFailTimeMap.get(errorCode);
            long time = lastResultTime.get();
            if (time == 0)
                continue;

            int failDelay = getFailDelay(errorCode);
            long delay = System.currentTimeMillis() - time;
            if (delay >= 0 && delay < failDelay)
                return (int) (failDelay - delay);

            lastResultTime.set(0);
        }

        return 0;
    }

    private int getFailDelay(TaskErrorCode errorCode) {
        Map<TaskErrorCode, Integer> typedValue = _failDelayProperty.typedValue();
        Integer delay = typedValue.get(errorCode);
        return delay == null ? 0 : delay.intValue();
    }

    private ValueConverter<Map<String, String>, Map<TaskErrorCode, String>> createValueConverter() {
        return new ValueConverter<Map<String, String>, Map<TaskErrorCode, String>>() {
            @Override
            public Map<TaskErrorCode, String> convert(Map<String, String> source) {
                Map<TaskErrorCode, String> errorCodeMap = new HashMap<TaskErrorCode, String>();
                for (Map.Entry<String, String> item : source.entrySet()) {
                    errorCodeMap.put(TaskErrorCode.valueOf(item.getKey()), item.getValue());
                }

                return errorCodeMap;
            }
        };
    }

    private ValueParser<Map<TaskErrorCode, Integer>> createValueParser() {
        return new ValueParser<Map<TaskErrorCode, Integer>>() {
            @Override
            public Map<TaskErrorCode, Integer> parse(String value) {
                Map<String, String> mapValue = MapParser.DEFAULT.parse(value);
                Map<TaskErrorCode, String> mapValue2 = _failDelayValueConverter.convert(mapValue);
                return new MapValueConverter<TaskErrorCode, String, Integer>(new ValueConverter<String, Integer>() {
                    @Override
                    public Integer convert(String source) {
                        return IntegerParser.DEFAULT.parse(source);
                    }
                }).convert(mapValue2);
            }
        };
    }

    private ValueCorrector<Map<TaskErrorCode, Integer>> createValueCorrector() {
        PipelineCorrector<Integer> corrector = new PipelineCorrector<>(new DefaultValueCorrector<>(DEFAULT_FAIL_DELAY),
                new RangeCorrector<>(0, MAX_FAIL_DELAY));
        return new PipelineCorrector<Map<TaskErrorCode, Integer>>(new DefaultValueCorrector<Map<TaskErrorCode, Integer>>(new HashMap<TaskErrorCode, Integer>()),
                new MapValueCorrector<TaskErrorCode, Integer>(corrector));
    }

}
