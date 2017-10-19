package com.ctrip.soa.artemis.client.common;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.ctrip.soa.caravan.common.concurrent.Threads;
import com.ctrip.soa.caravan.common.metric.EventMetric;
import com.ctrip.soa.caravan.common.metric.EventMetricManager;
import com.ctrip.soa.caravan.common.metric.MetricConfig;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import com.ctrip.soa.caravan.configuration.typed.dynamic.TypedDynamicProperty;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;
import com.ctrip.soa.caravan.util.net.apache.DynamicPoolingHttpClientProvider;
import com.ctrip.soa.caravan.util.net.apache.HttpRequestExecutors;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ArtemisHttpClient {
    protected final Logger _logger = LoggerFactory.getLogger(this.getClass());
    private final DynamicPoolingHttpClientProvider _clientProvider;
    private final AddressManager _addressManager;
    private final TypedDynamicProperty<Integer> _httpClientRetryTimes;
    private final TypedProperty<Integer> _retryInterval;
    private final String _distributionMetricName;
    protected final EventMetricManager _eventMetricManager;

    public ArtemisHttpClient(final ArtemisClientConfig config, final String httpClientId) {
        Preconditions.checkArgument(config != null, "config");
        Preconditions.checkArgument(!StringValues.isNullOrWhitespace(httpClientId), "httpClientId");
        _clientProvider = new DynamicPoolingHttpClientProvider(httpClientId + ".http-client", config.properties(),
                new RangePropertyConfig<Integer>(100, 20, 5 * 1000), new RangePropertyConfig<Integer>(20, 5, 2 * 1000),
                new RangePropertyConfig<Integer>(100, 10, 60 * 1000), new RangePropertyConfig<Integer>(10, 5, 20),
                new RangePropertyConfig<Integer>(30, 15, 100));
        _addressManager = config.addressManager();
        _httpClientRetryTimes = config.properties().getIntProperty(httpClientId + ".http-client.retry-times", 5, 1, 10);
        _retryInterval = config.properties().getIntProperty(httpClientId + ".http-client.retry-interval", 100, 0, 1000);
        _distributionMetricName = config.key("http-response.status-code");
        _eventMetricManager = config.eventMetricManager();
    }

    public <T extends HasResponseStatus> T request(final String path, final Object request, final Class<T> clazz) {
        final int retryTimes = _httpClientRetryTimes.typedValue().intValue();
        ResponseStatus responseStatus = null;
        for (int i = 0; i < retryTimes; i++) {
            AddressContext context = null;
            try {
                context = _addressManager.getContext();
                String requestUrl = context.customHttpUrl(path);
                T response = HttpRequestExecutors.executeGzippedJson(_clientProvider.get(), requestUrl, request, clazz);
                if (response == null || response.getResponseStatus() == null)
                    throw new RuntimeException("Got null response or null response status.");

                responseStatus = response.getResponseStatus();
                boolean isServiceDown = ResponseStatusUtil.isServiceDown(responseStatus);
                boolean isRerunnable = ResponseStatusUtil.isRerunnable(responseStatus);
                if (!(isServiceDown || isRerunnable))
                    return response;

                if (isServiceDown)
                    context.markUnavailable();

                _logger.info("get response failed, but can be retried. at turn: " + (i + 1) + ". responseStatus: " + responseStatus);
            } catch (final Throwable e) {
                if (context != null)
                    context.markUnavailable();

                if (i < retryTimes - 1) {
                    _logger.info("get response failed in this turn: " + (i + 1), e);
                } else {
                    _logger.error("与 SOA 注册中心通信时发生错误", e);
                    throw e;
                }
            }

            Threads.sleep(_retryInterval.typedValue());
        }

        throw new RuntimeException("Got failed response: " + responseStatus);
    }

    protected void logEvent(final String service, final String operation) {
        logEvent(null, service, operation);
    }

    protected void logEvent(final ResponseStatus status, final String service, final String operation) {
        final String metricId = _distributionMetricName + "|" + service + "|" + operation;
        final Map<String, String> metadata = Maps.newHashMap();
        metadata.put("metric_name_distribution", _distributionMetricName);
        metadata.put("service", service);
        metadata.put("operation", operation);
        final EventMetric metric = _eventMetricManager.getMetric(metricId, new MetricConfig(metadata));
        if (status == null) {
            metric.addEvent("null");
        } else {
            metric.addEvent(status.getErrorCode());
        }
    }
}