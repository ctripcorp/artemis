package com.ctrip.soa.artemis.cluster;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.artemis.util.ServiceNodeUtil;
import com.ctrip.soa.caravan.common.concurrent.Threads;
import com.ctrip.soa.caravan.common.delegate.Action;
import com.ctrip.soa.caravan.common.net.NetworkInterfaceManager;
import com.ctrip.soa.caravan.common.value.BooleanValues;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.configuration.dynamic.PropertyChangeEvent;
import com.ctrip.soa.caravan.configuration.dynamic.PropertyChangeListener;
import com.ctrip.soa.caravan.configuration.typed.dynamic.TypedDynamicProperty;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public final class NodeManager {

    public static final NodeManager INSTANCE = new NodeManager();

    private static final Logger _logger = LoggerFactory.getLogger(NodeManager.class);

    private TypedDynamicProperty<Boolean> _statusForceUpProperty = ArtemisConfig.properties().getBooleanProperty("artemis.service.cluster.node.status.force-up",
            false);

    private TypedDynamicProperty<Boolean> _serviceRegistryForceUpProperty = ArtemisConfig.properties()
            .getBooleanProperty("artemis.service.cluster.node.status.registry.force-up", false);

    private TypedDynamicProperty<Boolean> _serviceDiscoveryForceUpProperty = ArtemisConfig.properties()
            .getBooleanProperty("artemis.service.cluster.node.status.discovery.force-up", false);

    private TypedDynamicProperty<Boolean> _statusForceDownProperty = ArtemisConfig.properties()
            .getBooleanProperty("artemis.service.cluster.node.status.force-down." + NetworkInterfaceManager.INSTANCE.localhostIP(), false);

    private TypedDynamicProperty<Boolean> _serviceRegistryForceDownProperty = ArtemisConfig.properties()
            .getBooleanProperty("artemis.service.cluster.node.status.registry.force-down." + NetworkInterfaceManager.INSTANCE.localhostIP(), false);

    private TypedDynamicProperty<Boolean> _serviceDiscoveryForceDownProperty = ArtemisConfig.properties()
            .getBooleanProperty("artemis.service.cluster.node.status.discovery.force-down." + NetworkInterfaceManager.INSTANCE.localhostIP(), false);

    private TypedDynamicProperty<Integer> _initSyncIntervalProperty = ArtemisConfig.properties()
            .getIntProperty("artemis.service.cluster.node.init.sync-interval", 30, 1000, 600 * 1000);

    private TypedDynamicProperty<Boolean> _allowRegistryFromOtherZoneProperty = ArtemisConfig.properties()
            .getBooleanProperty("artemis.service.registry.allow-from-other-zone", false);

    private TypedDynamicProperty<Boolean> _allowDiscoveryFromOtherZoneProperty = ArtemisConfig.properties()
            .getBooleanProperty("artemis.service.discovery.allow-from-other-zone", false);

    private ServiceNodeStatus _nodeStatus;

    private List<NodeInitializer> _initializers = new ArrayList<>();

    private NodeManager() {

    }

    public ServiceNodeStatus nodeStatus() {
        return _nodeStatus;
    }

    public void init(List<NodeInitializer> initializers) {
        _nodeStatus = ServiceNodeUtil.newUnknownNodeStatus(ClusterManager.INSTANCE.localNode());
        _nodeStatus.setStatus(ServiceNodeStatus.Status.STARTING);
        updateNodeStatus();

        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void onChange(PropertyChangeEvent event) {
                updateNodeStatus();
            }
        };

        _statusForceUpProperty.addChangeListener(listener);
        _serviceRegistryForceUpProperty.addChangeListener(listener);
        _serviceDiscoveryForceUpProperty.addChangeListener(listener);
        _statusForceDownProperty.addChangeListener(listener);
        _serviceRegistryForceDownProperty.addChangeListener(listener);
        _serviceDiscoveryForceDownProperty.addChangeListener(listener);
        _allowRegistryFromOtherZoneProperty.addChangeListener(listener);
        _allowDiscoveryFromOtherZoneProperty.addChangeListener(listener);

        _initializers.add(RegistryReplicationInitializer.INSTANCE);
        if (initializers != null)
            _initializers.addAll(initializers);

        initAsync();
    }

    private void initAsync() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (ServiceNodeUtil.isUp(_nodeStatus))
                        return;

                    try {
                        ArtemisTraceExecutor.INSTANCE.execute("artemis.service.cluster.node.init.sync-data", new Action() {
                            @Override
                            public void execute() {
                                if (ServiceNodeUtil.isDown(_nodeStatus))
                                    return;

                                executeInitializers();
                            }
                        });
                    } catch (Throwable ex) {
                        _logger.error("Exception happen in NodeManage init.", ex);
                    }

                    Threads.sleep(_initSyncIntervalProperty.typedValue().intValue());
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void updateNodeStatus() {
        if (_statusForceUpProperty.typedValue().booleanValue()) {
            _nodeStatus.setStatus(ServiceNodeStatus.Status.UP);
            _nodeStatus.setCanServiceDiscovery(true);
            _nodeStatus.setCanServiceRegistry(true);
            _logger.warn("Node status is force up by config.");
        }

        if (_statusForceDownProperty.typedValue().booleanValue()) {
            _nodeStatus.setStatus(ServiceNodeStatus.Status.DOWN);
            _logger.warn("Node status is force down by config.");
        }

        if (_serviceRegistryForceUpProperty.typedValue().booleanValue()) {
            _nodeStatus.setCanServiceRegistry(true);
            _logger.warn("Node registry status is force up by config.");
        }

        if (_serviceRegistryForceDownProperty.typedValue().booleanValue()) {
            _nodeStatus.setCanServiceRegistry(false);
            _logger.warn("Node registry status is force down by config.");
        }

        if (_serviceDiscoveryForceUpProperty.typedValue().booleanValue()) {
            _nodeStatus.setCanServiceDiscovery(true);
            _logger.warn("Node status discovery is force up by config.");
        }

        if (_serviceDiscoveryForceDownProperty.typedValue().booleanValue()) {
            _nodeStatus.setCanServiceDiscovery(false);
            _logger.warn("Node status discovery is force down by config.");
        }

        _nodeStatus.setAllowRegistryFromOtherZone(_allowRegistryFromOtherZoneProperty.typedValue().booleanValue());
        _nodeStatus.setAllowDiscoveryFromOtherZone(_allowDiscoveryFromOtherZoneProperty.typedValue().booleanValue());
    }

    private void executeInitializers() {
        boolean discoveryInitSuccess = true;
        if (!BooleanValues.isFalse(_nodeStatus.isCanServiceDiscovery())) {
            discoveryInitSuccess = executeInitializers(NodeInitializer.TargetType.DISCOVERY);
            _logger.info("Discovery initializers execute result: " + discoveryInitSuccess);
        }

        if (!BooleanValues.isTrue(_nodeStatus.isCanServiceRegistry())) {
            boolean success = executeInitializers(NodeInitializer.TargetType.REGISTRY);
            _logger.info("Registry initializers execute result: " + discoveryInitSuccess);
            if (success)
                _nodeStatus.setCanServiceRegistry(true);
        }

        if (!BooleanValues.isTrue(_nodeStatus.isCanServiceDiscovery()) && discoveryInitSuccess && BooleanValues.isTrue(_nodeStatus.isCanServiceRegistry()))
            _nodeStatus.setCanServiceDiscovery(true);

        if (!ServiceNodeUtil.isUp(_nodeStatus) && !ServiceNodeUtil.isDown(_nodeStatus) && BooleanValues.isTrue(_nodeStatus.isCanServiceRegistry())
                && BooleanValues.isTrue(_nodeStatus.isCanServiceDiscovery()))
            _nodeStatus.setStatus(ServiceNodeStatus.Status.UP);

        _logger.info("NodeManager inited. NodeStatus: " + _nodeStatus);
    }

    private boolean executeInitializers(NodeInitializer.TargetType target) {
        NullArgumentChecker.DEFAULT.check(target, "target");

        boolean success = true;
        for (NodeInitializer initializer : _initializers) {
            if (!target.equals(initializer.target()))
                continue;

            try {
                boolean itemSucess = initializer.initialize();
                success = success && itemSucess;
            } catch (Throwable ex) {
                success = false;
                _logger.error("Initializer failed. Target: " + initializer, ex);
            }
        }

        return success;
    }
    
}
