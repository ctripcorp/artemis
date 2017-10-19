package com.ctrip.soa.artemis.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.caravan.common.net.NetworkInterfaceManager;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.StringArgumentChecker;
import com.ctrip.soa.caravan.configuration.ConfigurationSource;
import com.ctrip.soa.caravan.configuration.facade.ConfigurationManagers;
import com.ctrip.soa.caravan.configuration.facade.TypedDynamicCachedCorrectedProperties;
import com.ctrip.soa.caravan.configuration.source.environmentvariable.EnvironmentVariableConfigurationSource;
import com.ctrip.soa.caravan.configuration.source.properties.PropertiesConfigurationSource;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import com.ctrip.soa.caravan.configuration.typed.dynamic.cached.corrected.TypedDynamicCachedCorrectedConfigurationManager;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public final class DeploymentConfig {

    private static final String DEPLOYMENT_ENV_PROPERTY_NAME = "deployment-env";
    private static final String PROPERTIES_FILE_NAME = "META-INF/app";

    private static final String REGION_ID_PROPERTY_KEY = "region.id";
    private static final String ZONE_ID_PROPERTY_KEY = "zone.id";
    private static final String APPID_PROPERTY_KEY = "app.id";
    private static final String PORT_PROPERTY_KEY = "app.port";
    private static final String PROTOCOL_PROPERTY_KEY = "app.protocol";
    private static final String PATH_PROPERTY_KEY = "app.path";

    private static final Logger _logger = LoggerFactory.getLogger(DeploymentConfig.class);

    private static String _deploymentEnv;

    private static TypedDynamicCachedCorrectedConfigurationManager _manager;
    private static TypedDynamicCachedCorrectedProperties _properties;

    static {
        _deploymentEnv = System.getProperty(DEPLOYMENT_ENV_PROPERTY_NAME);
        _deploymentEnv = StringValues.isNullOrWhitespace(_deploymentEnv) ? null : _deploymentEnv.trim().toLowerCase();

        List<ConfigurationSource> sources = new ArrayList<>();
        sources.add(new PropertiesConfigurationSource(1, PROPERTIES_FILE_NAME));
        if (!StringValues.isNullOrWhitespace(_deploymentEnv))
            sources.add(new PropertiesConfigurationSource(2, PROPERTIES_FILE_NAME + "-" + _deploymentEnv));
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

    private static TypedProperty<String> _regionIdProperty = _properties.getStringProperty(REGION_ID_PROPERTY_KEY);
    private static TypedProperty<String> _zoneIdProperty = _properties.getStringProperty(ZONE_ID_PROPERTY_KEY);
    private static TypedProperty<String> _appIdProperty = _properties.getStringProperty(APPID_PROPERTY_KEY);
    private static TypedProperty<Integer> _portProperty = _properties.getIntProperty(PORT_PROPERTY_KEY, 8080, 1, 65535);
    private static TypedProperty<String> _protocolProperty = _properties.getStringProperty(PROTOCOL_PROPERTY_KEY, "http");
    private static TypedProperty<String> _pathProperty = _properties.getStringProperty(PATH_PROPERTY_KEY);

    private static String _regionId;
    private static String _zoneId;
    private static String _appId;
    private static String _machineName;
    private static String _ip;
    private static int _port;
    private static String _protocol;
    private static String _path;

    static {
        _regionId = _regionIdProperty.typedValue();
        _zoneId = _zoneIdProperty.typedValue();
        _appId = _appIdProperty.typedValue();
        _port = _portProperty.typedValue().intValue();
        _protocol = _protocolProperty.typedValue();
        _path = _pathProperty.typedValue();

        _machineName = NetworkInterfaceManager.INSTANCE.localhostName();
        _ip = NetworkInterfaceManager.INSTANCE.localhostIP();

        logDeploymentInfo();
    }

    private static AtomicBoolean _inited = new AtomicBoolean();

    public static void init(String regionId, String zoneId, String appId, String protocol, int port, String path) {
        StringArgumentChecker.DEFAULT.check(regionId, "region");
        StringArgumentChecker.DEFAULT.check(zoneId, "zone");
        StringArgumentChecker.DEFAULT.check(appId, "appId");
        StringArgumentChecker.DEFAULT.check(protocol, "protocol");

        if (!_inited.compareAndSet(false, true)) {
            _logger.warn("DeploymentConfig init method can be only invoked once!");
            return;
        }

        _regionId = regionId;
        _zoneId = zoneId;
        _appId = appId;

        if (port > 0)
            _port = port;

        _protocol = protocol;
        _path = path;

        logDeploymentInfo();
    }

    public static String deploymentEnv() {
        return _deploymentEnv;
    }

    public static String regionId() {
        return _regionId;
    }

    public static String zoneId() {
        return _zoneId;
    }

    public static String appId() {
        return _appId;
    }

    public static String machineName() {
        return _machineName;
    }

    public static String ip() {
        return _ip;
    }

    public static int port() {
        return _port;
    }

    public static String protocol() {
        return _protocol;
    }

    public static String path() {
        return _path;
    }

    private static void logDeploymentInfo() {
        _logger.info("DeploymentConfig is initialized. deploymentEnv: {}, regionId: {}, zoneId: {}, appId: {}, machineName: {}"
                + ", ip: {}, port: {}, protocol: {}, path: {}", _deploymentEnv, _regionId, _zoneId, _appId, _machineName, _ip, _port, _protocol, _path);
    }

    private DeploymentConfig() {

    }

}
