package com.ctrip.soa.artemis.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.soa.artemis.management.instance.InstanceOperations;
import com.ctrip.soa.artemis.management.server.ServerOperations;
import com.ctrip.soa.artemis.management.zone.ZoneKey;
import com.ctrip.soa.artemis.util.InstanceChanges;
import com.ctrip.soa.caravan.common.value.MapValues;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.InstanceChange;
import com.ctrip.soa.artemis.InstanceKey;
import com.ctrip.soa.artemis.ServerKey;
import com.ctrip.soa.artemis.Service;
import com.ctrip.soa.artemis.cluster.NodeManager;
import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.discovery.DiscoveryConfig;
import com.ctrip.soa.artemis.discovery.DiscoveryFilter;
import com.ctrip.soa.artemis.lease.Lease;
import com.ctrip.soa.artemis.management.dao.InstanceDao;
import com.ctrip.soa.artemis.management.dao.InstanceLogDao;
import com.ctrip.soa.artemis.management.dao.InstanceLogModel;
import com.ctrip.soa.artemis.management.dao.InstanceModel;
import com.ctrip.soa.artemis.management.dao.ServerDao;
import com.ctrip.soa.artemis.management.dao.ServerLogDao;
import com.ctrip.soa.artemis.management.dao.ServerLogModel;
import com.ctrip.soa.artemis.management.dao.ServerModel;
import com.ctrip.soa.artemis.registry.RegistryRepository;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.artemis.util.ServiceNodeUtil;
import com.ctrip.soa.caravan.common.concurrent.Threads;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctrip.soa.caravan.common.value.ArrayValues;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThread;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThreadConfig;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ManagementRepository {

    private static final Logger _logger = LoggerFactory.getLogger(ManagementRepository.class);

    private static ManagementRepository _instance;

    public static ManagementRepository getInstance() {
        if (_instance == null) {
            synchronized (ManagementRepository.class) {
                if (_instance == null)
                    _instance = new ManagementRepository();
            }
        }

        return _instance;
    }

    public static InstanceKey toInstanceKey(InstanceModel instance) {
        if (instance == null)
            return InstanceKey.EMPTY;

        return new InstanceKey(instance.getRegionId(), instance.getServiceId(), instance.getInstanceId());
    }

    public static ServerKey toServerKey(ServerModel server) {
        if (server == null)
            return ServerKey.EMPTY;

        return new ServerKey(server.getRegionId(), server.getServerId());
    }

    private interface KeyChange {
        String NEW = "new";
        String DELETE = "delete";
    }

    private TypedProperty<Integer> _managementDBSyncWaitTimeProperty = ArtemisConfig.properties().getIntProperty("artemis.management.db-sync.wait-time",
            2 * 1000, 0, 60 * 1000);

    private InstanceDao _instanceDao = InstanceDao.INSTANCE;
    private ServerDao _serverDao = ServerDao.INSTANCE;
    private InstanceLogDao _instanceLogDao = InstanceLogDao.INSTANCE;
    private ServerLogDao _serverLogDao = ServerLogDao.INSTANCE;

    private RegistryRepository _registryRepository = RegistryRepository.getInstance();
    private GroupRepository groupRepository = GroupRepository.getInstance();
    private ZoneRepository zoneRepository = ZoneRepository.getInstance();

    private volatile Map<InstanceKey, InstanceOperations> _allInstanceOperationsMap = new HashMap<>();
    private volatile Map<ServerKey, ServerOperations> _allServerOperationsMap = new HashMap<>();

    private volatile boolean _lastRefreshSuccess;
    private volatile long _lastRefreshTime;

    private DynamicScheduledThread _cacheRefresher;
    private final List<DiscoveryFilter> filters = Lists.newArrayList();

    private ManagementRepository() {
        DynamicScheduledThreadConfig dynamicScheduledThreadConfig = new DynamicScheduledThreadConfig(ArtemisConfig.properties(),
                new RangePropertyConfig<Integer>(0, 0, 10 * 1000), new RangePropertyConfig<Integer>(1000, 200, 60 * 1000));
        final String cacheRefreshKey = "artemis.management.data.cache-refresher";
        _cacheRefresher = new DynamicScheduledThread(cacheRefreshKey, new Runnable() {
            @Override
            public void run() {
                _lastRefreshTime = System.currentTimeMillis();
                _lastRefreshSuccess = ArtemisTraceExecutor.INSTANCE.execute(cacheRefreshKey, new Func<Boolean>() {
                    @Override
                    public Boolean execute() {
                        return refreshCache();
                    }
                });
            }
        }, dynamicScheduledThreadConfig);
        _cacheRefresher.setDaemon(true);
        _cacheRefresher.start();
    }

    public synchronized void addFilter(DiscoveryFilter... filters) {
        if (ArrayValues.isNullOrEmpty(filters)) {
            return;
        }

        for (DiscoveryFilter filter : filters) {
            if (filter == null) {
                continue;
            }

            this.filters.add(filter);
        }

    }

    public boolean isLastRefreshSuccess() {
        return _lastRefreshSuccess;
    }

    public long lastRefreshTime() {
        return _lastRefreshTime;
    }

    public boolean isInstanceDown(Instance instance) {
        if (getInstanceOperations(InstanceKey.of(instance)) != null)
            return true;

        if (isServerDown(ServerKey.of(instance)))
            return true;

        if (zoneRepository.isZoneDown(ZoneKey.of(instance)))
            return true;

        return groupRepository.isInstanceDown(instance);
    }

    public boolean isServerDown(ServerKey serverKey) {
        return getServerOperations(serverKey) != null;
    }

    public InstanceOperations getInstanceOperations(InstanceKey instanceKey) {
        NullArgumentChecker.DEFAULT.check(instanceKey, "instanceKey");
        return _allInstanceOperationsMap.get(instanceKey);
    }

    public ServerOperations getServerOperations(ServerKey serverKey) {
        NullArgumentChecker.DEFAULT.check(serverKey, "serverKey");
        return _allServerOperationsMap.get(serverKey);
    }

    public void insertServer(ServerModel server) {
        _serverDao.insert(server);
        _serverLogDao.insert(ServerLogModel.of(server, false));
    }

    public void deleteServer(ServerModel server) {
        _serverDao.delete(server);
        _serverLogDao.insert(ServerLogModel.of(server, true));
    }

    public void insertInstance(InstanceModel instance) {
        insertInstances(Lists.newArrayList(instance));
    }

    public void deleteInstance(InstanceModel instance) {
        deleteInstances(Lists.newArrayList(instance));
    }

    public void insertInstances(List<InstanceModel> instances) {
        _instanceDao.insert(instances);
        _instanceLogDao.insert(InstanceLogModel.of(instances, false));
    }

    public void deleteInstances(List<InstanceModel> instances) {
        _instanceDao.delete(instances);
        _instanceLogDao.insert(InstanceLogModel.of(instances, true));
    }

    public void destroyServers(List<ServerKey> serverKeys) {
        _serverDao.destroyServers(serverKeys);
        _instanceDao.destroyServers(serverKeys);
    }

    public List<InstanceModel> queryInstances(String regionId, List<String> serviceIds) {
        return _instanceDao.queryInstances(regionId, serviceIds);
    }

    public List<InstanceOperations> getAllInstanceOperations() {
        return new ArrayList<>(_allInstanceOperationsMap.values());
    }

    public List<InstanceOperations> getAllInstanceOperations(String regionId) {
        if (StringValues.isNullOrWhitespace(regionId))
            return getAllInstanceOperations();

        List<InstanceOperations> result = new ArrayList<>();
        for (InstanceOperations instanceOperations : _allInstanceOperationsMap.values()) {
            if (!regionId.equals(instanceOperations.getInstanceKey().getRegionId()))
                continue;

            result.add(instanceOperations);
        }

        return result;
    }

    public List<ServerOperations> getAllServerOperations() {
        return new ArrayList<>(_allServerOperationsMap.values());
    }

    public List<ServerOperations> getAllServerOperations(String regionId) {
        if (StringValues.isNullOrWhitespace(regionId))
            return getAllServerOperations();

        List<ServerOperations> result = new ArrayList<>();
        for (ServerOperations serverOperations : _allServerOperationsMap.values()) {
            if (!regionId.equals(serverOperations.getServerKey().getRegionId()))
                continue;

            result.add(serverOperations);
        }

        return result;
    }

    public List<Service> getAllServices() {
        List<Service> services = _registryRepository.getServices();
        for (Service service : services) {
            setServiceInstances(service);
            this.filterService(service, new DiscoveryConfig(service.getServiceId()));
        }

        return services;
    }

    public Service getService(final String serviceId) {
        if (StringValues.isNullOrWhitespace(serviceId)) {
            return null;
        }

        Service service = _registryRepository.getService(serviceId);
        setServiceInstances(service);

        if (service == null) {
            service = new Service(serviceId);
        }

        this.filterService(service, new DiscoveryConfig(serviceId));
        return service;
    }

    private void setServiceInstances(final Service service) {
        if (service == null) {
            return;
        }

        final String serviceId = service.getServiceId();
        if (StringValues.isNullOrWhitespace(serviceId)) {
            return;
        }

        Collection<Lease<Instance>> leases = _registryRepository.getLeases(serviceId);
        if (CollectionValues.isNullOrEmpty(leases)) {
            return;
        }

        List<Instance> instances = new ArrayList<>();
        for (Lease<Instance> lease : leases) {
            Instance instance = lease.data().clone();
            instances.add(instance);

            boolean isDown = isInstanceDown(instance);
            instance.setStatus(isDown ? Instance.Status.DOWN : Instance.Status.UP);

            Map<String, String> instanceMetadata = instance.getMetadata();
            if (instanceMetadata == null) {
                instanceMetadata = new HashMap<>();
                instance.setMetadata(instanceMetadata);
            }

            instanceMetadata.put("creationTime", String.valueOf(lease.creationTime()));
            instanceMetadata.put("renewalTime", String.valueOf(lease.renewalTime()));
            instanceMetadata.put("ttl", String.valueOf(lease.ttl()));
        }

        service.setInstances(instances);
    }

    public void waitForPeerSync() {
        Threads.sleep(_managementDBSyncWaitTimeProperty.typedValue());
    }

    private boolean refreshCache() {
        Map<String, List<InstanceKey>> instanceKeyChangeMap;
        try {
            instanceKeyChangeMap = refreshInstanceOperationsCache();
        } catch (Throwable ex) {
            _logger.error("Instance operations cache refresh failed", ex);
            return false;
        }

        Map<String, List<ServerKey>> serverKeyChangeMap;
        try {
            serverKeyChangeMap = refreshServerOperationsCache();
        } catch (Throwable ex) {
            _logger.error("Server operations cache refresh failed", ex);
            return false;
        }

        if (!ServiceNodeUtil.canServiceDiscovery(NodeManager.INSTANCE.nodeStatus()))
            return true;

        Set<InstanceChange> instanceChanges = generateInstanceChanges(_registryRepository.getInstances(), instanceKeyChangeMap, serverKeyChangeMap);
        Set<InstanceChange> logicalInstanceChanges = generateInstanceChanges(groupRepository.getLogicalInstances(), instanceKeyChangeMap, serverKeyChangeMap);
        Map<String, InstanceChange> reloadServices = Maps.newHashMap();
        for (InstanceChange instanceChange : logicalInstanceChanges) {
            if (instanceChange.getInstance() == null || StringValues.isNullOrWhitespace(instanceChange.getInstance().getServiceId())) {
                continue;
            }
            String serviceId = instanceChange.getInstance().getServiceId();
            if (!reloadServices.containsKey(serviceId)) {
                reloadServices.put(serviceId, InstanceChanges.newReloadInstanceChange(serviceId));
            }
        }
        for (InstanceChange instanceChange : instanceChanges) {
            if (instanceChange.getInstance() == null || StringValues.isNullOrWhitespace(instanceChange.getInstance().getServiceId())) {
                continue;
            }
            if (!reloadServices.containsKey(instanceChange.getInstance().getServiceId())) {
                _registryRepository.addInstanceChange(instanceChange);
            }
        }
        for (InstanceChange instanceChange : reloadServices.values()) {
            _registryRepository.addInstanceChange(instanceChange);
        }

        return true;
    }

    private Map<String, List<InstanceKey>> refreshInstanceOperationsCache() {
        HashMap<InstanceKey, InstanceOperations> allInstanceOperationsMap = new HashMap<>();
        List<InstanceModel> instances = _instanceDao.queryInstances();
        if (instances == null)
            instances = new ArrayList<>();

        for (InstanceModel instance : instances) {
            if (instance == null)
                continue;

            if (StringValues.isNullOrWhitespace(instance.getOperation()))
                continue;

            InstanceKey instanceKey = toInstanceKey(instance);
            InstanceOperations instanceOperations = allInstanceOperationsMap.get(instanceKey);
            if (instanceOperations == null) {
                instanceOperations = new InstanceOperations(instanceKey, new ArrayList<String>());
                allInstanceOperationsMap.put(instanceKey, instanceOperations);
            }

            Set<String> uniqueOperations = new HashSet<>(instanceOperations.getOperations());
            uniqueOperations.add(instance.getOperation());
            instanceOperations.setOperations(new ArrayList<>(uniqueOperations));
        }

        Set<InstanceKey> oldKeys = _allInstanceOperationsMap.keySet();
        _allInstanceOperationsMap = allInstanceOperationsMap;
        return generateKeyChange(oldKeys, _allInstanceOperationsMap.keySet());
    }

    private Map<String, List<ServerKey>> refreshServerOperationsCache() {
        HashMap<ServerKey, ServerOperations> allServerOperationsMap = new HashMap<>();
        List<ServerModel> servers = _serverDao.queryServers();
        if (servers == null)
            servers = new ArrayList<>();

        for (ServerModel server : servers) {
            if (server == null)
                continue;

            if (StringValues.isNullOrWhitespace(server.getOperation()))
                continue;

            ServerKey serverKey = toServerKey(server);
            ServerOperations serverOperations = allServerOperationsMap.get(serverKey);
            if (serverOperations == null) {
                serverOperations = new ServerOperations(serverKey, new ArrayList<String>());
                allServerOperationsMap.put(serverKey, serverOperations);
            }

            Set<String> uniqueOperations = new HashSet<>(serverOperations.getOperations());
            uniqueOperations.add(server.getOperation());
            serverOperations.setOperations(new ArrayList<>(uniqueOperations));
        }

        Set<ServerKey> oldKeys = _allServerOperationsMap.keySet();
        _allServerOperationsMap = allServerOperationsMap;
        return generateKeyChange(oldKeys, _allServerOperationsMap.keySet());
    }

    private <T> Map<String, List<T>> generateKeyChange(Set<T> oldKeys, Set<T> newKeys) {
        Map<String, List<T>> keyChangeMap = new HashMap<>();

        List<T> removed = new ArrayList<>();
        keyChangeMap.put(KeyChange.DELETE, removed);
        for (T serverKey : oldKeys) {
            if (newKeys.contains(serverKey))
                continue;

            removed.add(serverKey);
        }

        List<T> added = new ArrayList<>();
        keyChangeMap.put(KeyChange.NEW, added);
        for (T serverKey : newKeys) {
            if (oldKeys.contains(serverKey))
                continue;

            added.add(serverKey);
        }

        return keyChangeMap;
    }

    private Set<InstanceChange> generateInstanceChanges(Map<InstanceKey, Instance> instancesMap, Map<String, List<InstanceKey>> instanceKeyChangeMap, Map<String, List<ServerKey>> serverKeyChangeMap) {
        Set<InstanceChange> changes = new HashSet<>();
        if (MapValues.isNullOrEmpty(instancesMap)) {
            return changes;
        }

        ListMultimap<String, Instance> serverInstancesMap = ArrayListMultimap.create();
        for (Instance instance : instancesMap.values()) {
            serverInstancesMap.put(instance.getIp(), instance);
        }

        for (ServerKey serverKey : serverKeyChangeMap.get(KeyChange.DELETE)) {
            List<Instance> serverInstances = serverInstancesMap.get(serverKey.getServerId());
            if (serverInstances == null)
                continue;

            for (Instance instance : serverInstances) {
                changes.add(new InstanceChange(instance, InstanceChange.ChangeType.NEW));
            }
        }

        for (InstanceKey instanceKey : instanceKeyChangeMap.get(KeyChange.DELETE)) {
            Instance instance = instancesMap.get(instanceKey);
            if (instance == null)
                continue;

            changes.add(new InstanceChange(instance, InstanceChange.ChangeType.NEW));
        }

        for (ServerKey serverKey : serverKeyChangeMap.get(KeyChange.NEW)) {
            List<Instance> serverInstances = serverInstancesMap.get(serverKey.getServerId());
            if (serverInstances == null)
                continue;

            for (Instance instance : serverInstances) {
                changes.add(new InstanceChange(instance, InstanceChange.ChangeType.DELETE));
            }
        }

        for (InstanceKey instanceKey : instanceKeyChangeMap.get(KeyChange.NEW)) {
            Instance instance = instancesMap.get(instanceKey);
            if (instance == null)
                continue;

            changes.add(new InstanceChange(instance, InstanceChange.ChangeType.DELETE));
        }

        return changes;
    }

    private void filterService(Service service, DiscoveryConfig discoveryConfig) {
        if (service == null) {
            return;
        }

        for (DiscoveryFilter filter : filters) {
            try {
                filter.filter(service, discoveryConfig);
            } catch (Throwable ex) {
                _logger.error("Failed to execute filter " + filter, ex);
            }
        }
    }

}
