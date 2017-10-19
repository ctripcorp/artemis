package com.ctrip.soa.artemis.status;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;

import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.artemis.config.RestPaths;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.NullArgumentChecker;
import com.ctrip.soa.caravan.configuration.typed.TypedProperty;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;
import com.ctrip.soa.caravan.util.net.apache.DynamicPoolingHttpClientProvider;
import com.ctrip.soa.caravan.util.net.apache.HttpRequestExecutors;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class StatusServiceClient implements StatusService {

    private static DynamicPoolingHttpClientProvider _clientProvider = new DynamicPoolingHttpClientProvider("artemis.service.registry.status",
            ArtemisConfig.properties(), new RangePropertyConfig<Integer>(100, 20, 5 * 1000), new RangePropertyConfig<Integer>(20, 10, 100),
            new RangePropertyConfig<Integer>(100, 20, 5 * 1000), new RangePropertyConfig<Integer>(5, 1, 10), new RangePropertyConfig<Integer>(200, 20, 500));

    private static TypedProperty<Integer> _getLeasesSocketTimeoutProperty = ArtemisConfig.properties()
            .getIntProperty("artemis.service.registry.status.get-leases.client.socket-timeout", 10 * 1000, 100, 300 * 1000);

    private static TypedProperty<Integer> _getClusterNodeStatusSocketTimeoutProperty = ArtemisConfig.properties()
            .getIntProperty("artemis.service.registry.status.get-cluster-node.client.socket-timeout", 200, 100, 10 * 1000);

    private String _serviceUrl;

    public StatusServiceClient(String serviceUrl) {
        NullArgumentChecker.DEFAULT.check(serviceUrl, "serviceUrl");
        _serviceUrl = serviceUrl;
    }

    @Override
    public GetClusterNodeStatusResponse getClusterNodeStatus(final GetClusterNodeStatusRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.client.status.get-cluster-node", new Func<GetClusterNodeStatusResponse>() {

            @Override
            public GetClusterNodeStatusResponse execute() {
                String requestUrl = StringValues.concatPathParts(_serviceUrl, RestPaths.STATUS_NODE_FULL_PATH);
                return HttpRequestExecutors.executeGzippedJson(_clientProvider.get(), requestUrl, request, GetClusterNodeStatusResponse.class);
            }

        });
    }

    @Override
    public GetClusterStatusResponse getClusterStatus(final GetClusterStatusRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.client.status.get-cluster", new Func<GetClusterStatusResponse>() {

            @Override
            public GetClusterStatusResponse execute() {
                String requestUrl = StringValues.concatPathParts(_serviceUrl, RestPaths.STATUS_CLUSTER_FULL_PATH);
                RequestConfig config = RequestConfig.custom().setSocketTimeout(_getClusterNodeStatusSocketTimeoutProperty.typedValue().intValue()).build();
                return HttpRequestExecutors.executeJson(_clientProvider.get(), requestUrl, HttpPost.METHOD_NAME, null, config, request, true,
                        GetClusterStatusResponse.class);
            }

        });
    }

    @Override
    public GetLeasesStatusResponse getLeasesStatus(final GetLeasesStatusRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.client.status.get-leases", new Func<GetLeasesStatusResponse>() {

            @Override
            public GetLeasesStatusResponse execute() {
                String requestUrl = StringValues.concatPathParts(_serviceUrl, RestPaths.STATUS_LEASES_FULL_PATH);
                RequestConfig config = RequestConfig.custom().setSocketTimeout(_getLeasesSocketTimeoutProperty.typedValue().intValue()).build();
                return HttpRequestExecutors.executeJson(_clientProvider.get(), requestUrl, HttpPost.METHOD_NAME, null, config, request, true,
                        GetLeasesStatusResponse.class);
            }

        });
    }
    
    @Override
    public GetLeasesStatusResponse getLegacyLeasesStatus(final GetLeasesStatusRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.client.status.get-legacy-leases", new Func<GetLeasesStatusResponse>() {

            @Override
            public GetLeasesStatusResponse execute() {
                String requestUrl = StringValues.concatPathParts(_serviceUrl, RestPaths.STATUS_LEGACY_LEASES_FULL_PATH);
                RequestConfig config = RequestConfig.custom().setSocketTimeout(_getLeasesSocketTimeoutProperty.typedValue().intValue()).build();
                return HttpRequestExecutors.executeJson(_clientProvider.get(), requestUrl, HttpPost.METHOD_NAME, null, config, request, true,
                        GetLeasesStatusResponse.class);
            }

        });
    }

    @Override
    public GetConfigStatusResponse getConfigStatus(final GetConfigStatusRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.client.status.get-config", new Func<GetConfigStatusResponse>() {

            @Override
            public GetConfigStatusResponse execute() {
                String requestUrl = StringValues.concatPathParts(_serviceUrl, RestPaths.STATUS_CONFIG_FULL_PATH);
                return HttpRequestExecutors.executeGzippedJson(_clientProvider.get(), requestUrl, request, GetConfigStatusResponse.class);
            }

        });
    }

    @Override
    public GetDeploymentStatusResponse getDeploymentStatus(final GetDeploymentStatusRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.client.status.get-deployment", new Func<GetDeploymentStatusResponse>() {

            @Override
            public GetDeploymentStatusResponse execute() {
                String requestUrl = StringValues.concatPathParts(_serviceUrl, RestPaths.STATUS_DEPLOYMENT_FULL_PATH);
                return HttpRequestExecutors.executeGzippedJson(_clientProvider.get(), requestUrl, request, GetDeploymentStatusResponse.class);
            }

        });
    }

}
