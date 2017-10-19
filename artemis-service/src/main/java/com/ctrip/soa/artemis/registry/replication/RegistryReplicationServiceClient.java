package com.ctrip.soa.artemis.registry.replication;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;

import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.config.RestPaths;
import com.ctrip.soa.artemis.registry.replication.GetServicesRequest;
import com.ctrip.soa.artemis.registry.replication.GetServicesResponse;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.artemis.registry.HeartbeatRequest;
import com.ctrip.soa.artemis.registry.HeartbeatResponse;
import com.ctrip.soa.artemis.registry.RegisterRequest;
import com.ctrip.soa.artemis.registry.RegisterResponse;
import com.ctrip.soa.artemis.registry.UnregisterRequest;
import com.ctrip.soa.artemis.registry.UnregisterResponse;
import com.ctrip.soa.caravan.util.net.apache.HttpRequestExecutors;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;
import com.ctrip.soa.caravan.util.net.apache.DynamicPoolingHttpClientProvider;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class RegistryReplicationServiceClient implements RegistryReplicationService {

    private static DynamicPoolingHttpClientProvider _clientProvider = new DynamicPoolingHttpClientProvider(
            "artemis.service.registry.replication", ArtemisConfig.properties(),
            new RangePropertyConfig<Integer>(50, 20, 1 * 1000), new RangePropertyConfig<Integer>(20, 10, 100),
            new RangePropertyConfig<Integer>(200, 50, 5 * 1000), new RangePropertyConfig<Integer>(200, 10, 1000),
            new RangePropertyConfig<Integer>(1000, 100, 5 * 1000));

    private static TypedProperty<Integer> _heartbeatSocketTimeoutProperty = ArtemisConfig.properties()
            .getIntProperty("artemis.service.registry.replication.heartbeat.client.socket-timeout", 200, 50, 5 * 1000);

    private static TypedProperty<Integer> _getApplicationsSocketTimeoutProperty = ArtemisConfig.properties()
            .getIntProperty("artemis.service.registry.replication.get-applications.client.socket-timeout", 2000, 100,
                    60 * 1000);

    private String _serviceUrl;

    public RegistryReplicationServiceClient(String serviceUrl) {
        _serviceUrl = serviceUrl;
    }

    @Override
    public HeartbeatResponse heartbeat(final HeartbeatRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.client.registry.replication.heartbeat",
                new Func<HeartbeatResponse>() {
                    @Override
                    public HeartbeatResponse execute() {
                        String requestUrl = StringValues.concatPathParts(_serviceUrl,
                                RestPaths.REPLICATION_REGISTRY_HEARTBEAT_FULL_PATH);
                        RequestConfig config = RequestConfig.custom()
                                .setSocketTimeout(_heartbeatSocketTimeoutProperty.typedValue().intValue()).build();
                        return HttpRequestExecutors.executeJson(_clientProvider.get(), requestUrl, HttpPost.METHOD_NAME,
                                null, config, request, true, HeartbeatResponse.class);
                    }
                });
    }

    @Override
    public RegisterResponse register(final RegisterRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.client.registry.replication.register",
                new Func<RegisterResponse>() {
                    @Override
                    public RegisterResponse execute() {
                        String requestUrl = StringValues.concatPathParts(_serviceUrl,
                                RestPaths.REPLICATION_REGISTRY_REGISTER_FULL_PATH);
                        return HttpRequestExecutors.executeGzippedJson(_clientProvider.get(), requestUrl, request,
                                RegisterResponse.class);
                    }
                });
    }

    @Override
    public UnregisterResponse unregister(final UnregisterRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.client.registry.replication.unregister",
                new Func<UnregisterResponse>() {
                    @Override
                    public UnregisterResponse execute() {
                        String requestUrl = StringValues.concatPathParts(_serviceUrl,
                                RestPaths.REPLICATION_REGISTRY_UNREGISTER_FULL_PATH);
                        return HttpRequestExecutors.executeGzippedJson(_clientProvider.get(), requestUrl, request,
                                UnregisterResponse.class);
                    }
                });
    }

    @Override
    public GetServicesResponse getServices(final GetServicesRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.client.registry.replication.get-applications",
                new Func<GetServicesResponse>() {
                    @Override
                    public GetServicesResponse execute() {
                        String requestUrl = StringValues.concatPathParts(_serviceUrl,
                                RestPaths.REPLICATION_REGISTRY_GET_SERVICES_FULL_PATH);
                        RequestConfig config = RequestConfig.custom()
                                .setSocketTimeout(_getApplicationsSocketTimeoutProperty.typedValue().intValue())
                                .build();
                        return HttpRequestExecutors.executeJson(_clientProvider.get(), requestUrl, HttpPost.METHOD_NAME,
                                null, config, request, true, GetServicesResponse.class);
                    }
                });
    }

}
