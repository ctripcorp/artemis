package com.ctrip.soa.artemis.client.common;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.client.ArtemisClientManagerConfig;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.configuration.typed.dynamic.TypedDynamicProperty;
import com.google.common.base.Preconditions;

/**
 * Created by fang_j on 10/07/2016.
 */
public class AddressContext {
    private static final Logger _logger = LoggerFactory.getLogger(AddressContext.class);
    private static final String _wsPrefix = "ws://";
    private static final Pattern _httpSchema = Pattern.compile("(^http://|^https://)", Pattern.CASE_INSENSITIVE);
    private final long _createTime = System.currentTimeMillis();
    private final String _httpUrl;
    private final String _webSocketEndpoint;
    private final AtomicBoolean _available = new AtomicBoolean(false);
    private final TypedDynamicProperty<Integer> _ttl;

    public AddressContext(final String clientId, final ArtemisClientManagerConfig managerConfig) {
        this(clientId, managerConfig, StringValues.EMPTY, StringValues.EMPTY);
    }

    public AddressContext(final String clientId, final ArtemisClientManagerConfig managerConfig, final String httpUrl, final String wsEndpointSuffix) {
        Preconditions.checkArgument(!StringValues.isNullOrWhitespace(clientId), "clientId");
        Preconditions.checkArgument(managerConfig != null, "manager config");
        _ttl = managerConfig.properties().getIntProperty(clientId + ".address.context-ttl", 60 * 60 * 1000, 60 * 1000, 24 * 60 * 60 * 1000);
        if (StringValues.isNullOrWhitespace(httpUrl)) {
            _httpUrl = StringValues.EMPTY;
            _webSocketEndpoint = StringValues.EMPTY;
        }
        else {
            _httpUrl = httpUrl;
            _webSocketEndpoint = StringValues.concatPathParts(_httpSchema.matcher(httpUrl).replaceAll(_wsPrefix), wsEndpointSuffix);
            _available.set(true);
        }
    }

    public String getHttpUrl() {
        return _httpUrl;
    }

    public String customHttpUrl(final String path) {
        return StringValues.concatPathParts(getHttpUrl(), path);
    }

    public String getWebSocketEndPoint() {
        return _webSocketEndpoint;
    }

    public boolean isAavailable() {
        return _available.get();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= (_ttl.typedValue() + _createTime);
    }

    public void markUnavailable() {
        if (_available.compareAndSet(true, false)) {
            _logger.info(_httpUrl + " mark unavailable");
        }
    }
}
