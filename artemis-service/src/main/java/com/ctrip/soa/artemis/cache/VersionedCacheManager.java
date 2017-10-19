package com.ctrip.soa.artemis.cache;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.caravan.common.delegate.Action;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctrip.soa.caravan.common.delegate.Func2;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.common.value.checker.StringArgumentChecker;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class VersionedCacheManager<T, D> {

    public static final long EMPTY_VERSION = 0L;

    private static final Logger _logger = LoggerFactory.getLogger(VersionedCacheManager.class);

    private TypedProperty<Integer> _cacheCountProperty;
    private TypedProperty<Integer> _cacheRefreshInitDelayProperty;
    private TypedProperty<Integer> _cacheRefreshIntervalProperty;

    private String _managerId;
    private Func<T> _dataGenerator;
    private Func2<T, T, D> _deltaGenerator;

    private volatile long _currentVersion = EMPTY_VERSION;
    private ConcurrentSkipListMap<Long, VersionedCache<T, D>> _cacheMap;
    private ScheduledExecutorService _cacheRefreshExecutorService;

    public VersionedCacheManager(String managerId, Func<T> dataGenerator, Func2<T, T, D> deltaGenerator) {
        StringArgumentChecker.DEFAULT.check(managerId, "managerId");
        NullArgumentChecker.DEFAULT.check(dataGenerator, "dataGenerator");
        NullArgumentChecker.DEFAULT.check(deltaGenerator, "deltaGenerator");

        _managerId = managerId;
        _dataGenerator = dataGenerator;
        _deltaGenerator = deltaGenerator;

        _cacheCountProperty = ArtemisConfig.properties().getIntProperty(_managerId + ".versioned-cache.cache-count", 3, 0, 10);
        _cacheRefreshInitDelayProperty = ArtemisConfig.properties().getIntProperty(_managerId + ".versioned-cache.cache-refresh.init-delay", 60 * 1000, 0,
                5 * 60 * 1000);
        _cacheRefreshIntervalProperty = ArtemisConfig.properties().getIntProperty(_managerId + ".versioned-cache.cache-refresh.interval", 30 * 1000, 1 * 1000,
                5 * 60 * 1000);

        _cacheMap = new ConcurrentSkipListMap<>();

        final String traceId = _managerId + ".data-cache.cache-refresh";
        _cacheRefreshExecutorService = Executors.newSingleThreadScheduledExecutor();
        _cacheRefreshExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    ArtemisTraceExecutor.INSTANCE.execute(traceId, new Action() {
                        @Override
                        public void execute() {
                            updateCache();
                        }
                    });
                } catch (Throwable ex) {
                    _logger.error("updateCache failed.", ex);
                }
            }
        }, _cacheRefreshInitDelayProperty.typedValue().intValue(), _cacheRefreshIntervalProperty.typedValue().intValue(), TimeUnit.MILLISECONDS);
    }

    public VersionedData<T> get() {
        long version = _currentVersion;
        VersionedData<T> data = null;

        VersionedCache<T, D> cache = _cacheMap.get(version);
        if (cache != null)
            data = cache.getVersionedData();

        if (data == null)
            data = new VersionedData<T>(version, _dataGenerator.execute());

        return data;
    }

    public VersionedData<D> getDelta(long version) {
        VersionedCache<T, D> cache = _cacheMap.get(version);
        return cache == null ? null : cache.getVersionedDelta();
    }

    private void updateCache() {
        long version = System.currentTimeMillis();
        T data = _dataGenerator.execute();
        VersionedCache<T, D> cache = new VersionedCache<>(new VersionedData<>(version, data), null);
        _cacheMap.put(version, cache);
        while (_cacheMap.size() > _cacheCountProperty.typedValue().intValue()) {
            Entry<Long, VersionedCache<T, D>> entry = _cacheMap.pollFirstEntry();
            if (entry == null)
                break;
        }

        for (Long key : _cacheMap.keySet()) {
            if (key >= version)
                continue;

            VersionedCache<T, D> oldCachedData = _cacheMap.get(key);
            D delta = _deltaGenerator.execute(oldCachedData.getVersionedData().getData(), data);
            oldCachedData.setVersionedDelta(new VersionedData<>(version, delta));
        }

        _currentVersion = version;
    }

}
