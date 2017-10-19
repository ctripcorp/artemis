package com.ctrip.soa.artemis.client.registry;

import com.ctrip.soa.artemis.config.RestPaths;
import com.ctrip.soa.artemis.registry.HeartbeatRequest;
import com.ctrip.soa.artemis.registry.HeartbeatResponse;
import com.ctrip.soa.artemis.registry.RegisterRequest;
import com.ctrip.soa.artemis.registry.RegisterResponse;
import com.ctrip.soa.artemis.registry.RegistryService;
import com.ctrip.soa.artemis.registry.UnregisterRequest;
import com.ctrip.soa.artemis.registry.UnregisterResponse;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.configuration.facade.TypedDynamicCachedCorrectedProperties;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;
import com.ctrip.soa.caravan.util.net.apache.DynamicPoolingHttpClientProvider;
import com.ctrip.soa.caravan.util.net.apache.HttpRequestExecutors;
import com.google.common.base.Preconditions;

/**
 * Created by fang_j on 10/07/2016.
 */
public class RegistryServiceClient implements RegistryService{
    private final DynamicPoolingHttpClientProvider _clientProvider;
    private final String _url;
    public RegistryServiceClient(final TypedDynamicCachedCorrectedProperties properties,
            final String url) {
        Preconditions.checkArgument(properties != null, "properties");
        Preconditions.checkArgument(!StringValues.isNullOrWhitespace(url), "url");
        _clientProvider = new DynamicPoolingHttpClientProvider(
                "artemis.client.registry-service", properties,
                new RangePropertyConfig<Integer>(100, 20, 5 * 1000),
                new RangePropertyConfig<Integer>(20, 5, 2 * 1000),
                new RangePropertyConfig<Integer>(100, 10, 60 * 1000),
                new RangePropertyConfig<Integer>(10, 5, 20),
                new RangePropertyConfig<Integer>(30, 15, 100));
        _url = url;
    }

    @Override
    public RegisterResponse register(final RegisterRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.client.registry-service.register", new Func<RegisterResponse>() {
            @Override
            public RegisterResponse execute() {
                final String requestUrl = StringValues.concatPathParts(_url, RestPaths.REGISTRY_REGISTER_FULL_PATH);
                return HttpRequestExecutors.executeGzippedJson(_clientProvider.get(), requestUrl,
                        request, RegisterResponse.class);
            }
        });
    }

    @Override
    public HeartbeatResponse heartbeat(final HeartbeatRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.client.registry-service.heartbeat", new Func<HeartbeatResponse>() {
            @Override
            public HeartbeatResponse execute() {
                final String requestUrl = StringValues.concatPathParts(_url, RestPaths.REGISTRY_HEARTBEAT_FULL_PATH);
                return HttpRequestExecutors.executeGzippedJson(_clientProvider.get(), requestUrl,
                        request, HeartbeatResponse.class);
            }
        });

    }

    @Override
    public UnregisterResponse unregister(final UnregisterRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.client.registry-service.unregister", new Func<UnregisterResponse>() {
            @Override
            public UnregisterResponse execute() {
                final String requestUrl = StringValues.concatPathParts(_url, RestPaths.REGISTRY_UNREGISTER_FULL_PATH);
                return HttpRequestExecutors.executeGzippedJson(_clientProvider.get(), requestUrl,
                        request, UnregisterResponse.class);
            }
        });
    }
}
