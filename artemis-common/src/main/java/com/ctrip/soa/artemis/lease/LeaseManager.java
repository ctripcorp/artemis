package com.ctrip.soa.artemis.lease;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.common.value.checker.StringArgumentChecker;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import com.ctrip.soa.caravan.common.delegate.Action;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class LeaseManager<T> {

    private static final Logger _logger = LoggerFactory.getLogger(LeaseManager.class);

    private String _managerId;

    private ConcurrentHashMap<T, Lease<T>> _leaseCache;
    private TypedProperty<Integer> _dataInitCapacityProperty;
    private TypedProperty<Integer> _leaseTtlProperty;

    private TypedProperty<Integer> _cleanTaskThreadCountProperty;
    private TypedProperty<Integer> _cleanTaskInitDelayProperty;
    private TypedProperty<Integer> _cleanTaskRunIntervalProperty;
    private ScheduledExecutorService _cleanTaskExecutorService;

    private LeaseUpdateSafeChecker _leaseUpdateSafeChecker;

    private List<LeaseCleanEventListener<T>> _leaseCleanEventListeners = new ArrayList<>();

    public LeaseManager(String managerId) {
        StringArgumentChecker.DEFAULT.check(managerId, "managerId");
        _managerId = managerId;

        initConfig();

        _leaseCache = new ConcurrentHashMap<>(_dataInitCapacityProperty.typedValue().intValue(), 0.9f);
        scheduleCleanTask();

        _leaseUpdateSafeChecker = new LeaseUpdateSafeChecker(this);

        _logger.info("LeaseManager '" + managerId + "' is inited.");
    }

    public String managerId() {
        return _managerId;
    }

    public LeaseUpdateSafeChecker leaseUpdateSafeChecker() {
        return _leaseUpdateSafeChecker;
    }

    public synchronized void addLeaseCleanEventListener(LeaseCleanEventListener<T> listener) {
        NullArgumentChecker.DEFAULT.check(listener, "listener");
        _leaseCleanEventListeners.add(listener);
    }

    public Lease<T> register(T data) {
        NullArgumentChecker.DEFAULT.check(data, "data");

        Lease<T> lease = new Lease<T>(this, data, _leaseTtlProperty);
        _leaseCache.put(data, lease);
        return lease;
    }

    public Lease<T> get(T data) {
        NullArgumentChecker.DEFAULT.check(data, "data");

        return _leaseCache.get(data);
    }

    public boolean hasLeaseOf(T data) {
        Lease<T> lease = get(data);
        return lease != null && !lease.isEvicted();
    }

    protected void initConfig() {
        _dataInitCapacityProperty = ArtemisConfig.properties().getIntProperty(_managerId + ".lease-manager.data.init-capacity", 50 * 1000, 10 * 1000,
                1000 * 1000);
        _cleanTaskThreadCountProperty = ArtemisConfig.properties().getIntProperty(_managerId + ".lease-manager.clean-task.thread-count", 2, 1, 10);
        _cleanTaskInitDelayProperty = ArtemisConfig.properties().getIntProperty(_managerId + ".lease-manager.clean-task.init-delay", 1000, 0, 10 * 1000);
        _cleanTaskRunIntervalProperty = ArtemisConfig.properties().getIntProperty(_managerId + ".lease-manager.clean-task.run-interval", 1000, 100, 5000);
        _leaseTtlProperty = ArtemisConfig.properties().getIntProperty(_managerId + ".lease-manager.lease.ttl", 20 * 1000, 10 * 1000, 7 * 24 * 60 * 60 * 1000);
    }

    private void scheduleCleanTask() {
        _cleanTaskExecutorService = Executors.newScheduledThreadPool(_cleanTaskThreadCountProperty.typedValue().intValue(),
                new ThreadFactoryBuilder().setDaemon(true).setNameFormat(threadNameFormat()).build());
        _cleanTaskExecutorService.scheduleAtFixedRate(new Runnable() {

            private String _traceKey = managerId() + ".lease-manager.clean";

            @Override
            public void run() {
                try {
                    ArtemisTraceExecutor.INSTANCE.execute(_traceKey, new Action() {
                        @Override
                        public void execute() {
                            clean();
                        }
                    });
                } catch (Throwable ex) {
                    _logger.error("Clean leases failed", ex);
                }
            }
        }, _cleanTaskInitDelayProperty.typedValue().intValue(), _cleanTaskRunIntervalProperty.typedValue().intValue(), TimeUnit.MILLISECONDS);
    }

    private void clean() {
        List<Lease<T>> cleaned = new ArrayList<>();
        for (Lease<T> lease : _leaseCache.values()) {
            if (!lease.tryLock())
                continue;

            try {
                if (!lease.isEvicted()) {
                    if (!_leaseUpdateSafeChecker.isSafe())
                        continue;

                    if (!lease.isExpired())
                        continue;
                }

                Lease<T> value = _leaseCache.remove(lease.data());
                if (value == null)
                    continue;

                if (value.creationTime() > lease.creationTime()) {
                    _leaseCache.putIfAbsent(lease.data(), value);
                    continue;
                }

                cleaned.add(lease);
            } finally {
                lease.releaseLock();
            }
        }

        if (cleaned.isEmpty())
            return;

        for (LeaseCleanEventListener<T> listener : _leaseCleanEventListeners) {
            listener.onClean(cleaned);
        }

        _logger.info("{} leases were cleaned.", cleaned.size());
    }

    protected String threadNameFormat() {
        return _managerId + ".lease-manager.%d";
    }

}