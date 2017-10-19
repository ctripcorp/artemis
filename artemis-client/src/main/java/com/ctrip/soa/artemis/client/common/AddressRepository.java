package com.ctrip.soa.artemis.client.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.ctrip.soa.artemis.client.ArtemisClientManagerConfig;
import com.ctrip.soa.artemis.cluster.GetServiceNodesRequest;
import com.ctrip.soa.artemis.cluster.GetServiceNodesResponse;
import com.ctrip.soa.artemis.cluster.ServiceNode;
import com.ctrip.soa.artemis.config.DeploymentConfig;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.configuration.typed.dynamic.cached.TypedDynamicCachedProperty;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThread;
import com.ctrip.soa.caravan.util.concurrent.DynamicScheduledThreadConfig;
import com.ctrip.soa.caravan.util.net.apache.DynamicPoolingHttpClientProvider;
import com.ctrip.soa.caravan.util.net.apache.HttpRequestExecutors;
import com.google.common.base.Preconditions;

/**
 * Created by fang_j on 10/07/2016.
 */
public class AddressRepository {
    private static final Logger _logger = LoggerFactory.getLogger(AddressRepository.class);
    private final TypedDynamicCachedProperty<String> _domainUrl;
    private final AtomicReference<List<String>> _avlSvcUrls = new AtomicReference<List<String>>();
    private final DynamicPoolingHttpClientProvider _clientProvider;
    private final String _path;
    private final GetServiceNodesRequest _request;
    private final DynamicScheduledThread _addressesPoller;
    public AddressRepository(final String clientId, final ArtemisClientManagerConfig managerConfig, final String path) {
        Preconditions.checkArgument(!StringValues.isNullOrWhitespace(clientId), "clientId");
        Preconditions.checkArgument(managerConfig != null, "manager config");
        Preconditions.checkArgument(!StringValues.isNullOrWhitespace(path), "path");
        _clientProvider = new DynamicPoolingHttpClientProvider(
                clientId + ".address.http-client", managerConfig.properties(),
                new RangePropertyConfig<Integer>(1000, 20, 5 * 1000),
                new RangePropertyConfig<Integer>(20, 5, 2 * 1000),
                new RangePropertyConfig<Integer>(20 * 1000, 10, 60 * 1000),
                new RangePropertyConfig<Integer>(20, 20, 100),
                new RangePropertyConfig<Integer>(60, 60, 300));
        _path = path;
        _request = new GetServiceNodesRequest(DeploymentConfig.regionId(), DeploymentConfig.zoneId());
        _domainUrl = managerConfig.properties().getStringProperty(clientId + ".service.domain.url", "");
        final DynamicScheduledThreadConfig dynamicScheduledThreadConfig = new DynamicScheduledThreadConfig(managerConfig.properties(),
                new RangePropertyConfig<Integer>(20, 0, 200), new RangePropertyConfig<Integer>(5 * 60 * 1000, 5 * 60 * 1000, 30 * 60 * 1000));
        _addressesPoller = new DynamicScheduledThread(clientId + ".address-repository", new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }, dynamicScheduledThreadConfig);
        refresh();
        _addressesPoller.setDaemon(true);
        _addressesPoller.start();
    }

    public String get() {
        final List<String> addressList = _avlSvcUrls.get();
        if ( CollectionUtils.isEmpty(addressList)) {
            return _domainUrl.typedValue();
        }
        return addressList.get(ThreadLocalRandom.current().nextInt(addressList.size()));
    }

    protected void refresh() {
        try {
            _logger.info("start refresh service urls");
            String domainUrl = _domainUrl.typedValue();
            if (StringValues.isNullOrWhitespace(domainUrl)) {
                _logger.error("domain url should not be null or empty for artemis client");
                return;
            }
            final List<String> urls = getUrlsFromService(domainUrl);
            if (!CollectionUtils.isEmpty(urls)) {
                _avlSvcUrls.set(urls);
            }
        }
        catch (final Throwable e) {
            _logger.warn("refesh service urls failed", e);
        }
        finally {
            _logger.info("end refresh service urls");
        }
    }

    private List<String> getUrlsFromService(final String url) {
        final List<String> addressList = new ArrayList<String>();
        try {
            if (StringValues.isNullOrWhitespace(url)) {
                return addressList;
            }

            final String requestUrl = StringValues.concatPathParts(url, _path);
            final GetServiceNodesResponse response = HttpRequestExecutors.executeGzippedJson(_clientProvider.get(), requestUrl,
                    _request, GetServiceNodesResponse.class);
            if (CollectionUtils.isEmpty(response.getNodes())) {
                return addressList;
            }

            final Set<String> newAddressList = new HashSet<String>();
            for (final ServiceNode node : response.getNodes()) {
                if ((node != null) && !StringValues.isNullOrWhitespace(node.getUrl())) {
                    String address = node.getUrl();
                    address = StringValues.trimEnd(address, '/');
                    if (StringValues.isNullOrWhitespace(address)) {
                        continue;
                    }
                    newAddressList.add(address);
                }
            }
            addressList.addAll(newAddressList);
        } catch (final Throwable e) {
            _logger.error("reset address from service failed", e);
        }
        return addressList;
    }
}