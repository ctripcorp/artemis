package com.ctrip.soa.artemis.client.registry;

import java.util.Set;

import org.springframework.util.CollectionUtils;

import com.ctrip.soa.artemis.Instance;
import com.ctrip.soa.artemis.client.common.ArtemisClientConfig;
import com.ctrip.soa.artemis.client.common.ArtemisHttpClient;
import com.ctrip.soa.artemis.config.RestPaths;
import com.ctrip.soa.artemis.registry.RegisterRequest;
import com.ctrip.soa.artemis.registry.RegisterResponse;
import com.ctrip.soa.artemis.registry.UnregisterRequest;
import com.ctrip.soa.artemis.registry.UnregisterResponse;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ArtemisRegistryHttpClient extends ArtemisHttpClient {
    public ArtemisRegistryHttpClient(final ArtemisClientConfig config) {
        super(config, config.key("registry"));
    }

    public void register(final Set<Instance> instances) {
        try {
            Preconditions.checkArgument(!CollectionUtils.isEmpty(instances), "instances");
            final RegisterRequest request = new RegisterRequest(Lists.newArrayList(instances));
            final RegisterResponse response = this.request(RestPaths.REGISTRY_REGISTER_FULL_PATH, request, RegisterResponse.class);
            if (ResponseStatusUtil.isFail(response.getResponseStatus())) {
                _logger.error("register instances failed. Response:" + JacksonJsonSerializer.INSTANCE.serialize(response));
            } else if (ResponseStatusUtil.isPartialFail(response.getResponseStatus())) {
                _logger.warn("register instances patial failed. Response:" + JacksonJsonSerializer.INSTANCE.serialize(response));
            }
            logEvent(response.getResponseStatus(), "registry", "register");
        } catch (final Throwable e) {
            _logger.warn("register instances failed", e);
            logEvent("registry", "register");
        }
    }

    public void unregister(final Set<Instance> instances) {
        try {
            Preconditions.checkArgument(!CollectionUtils.isEmpty(instances), "instances");
            final UnregisterRequest request = new UnregisterRequest(Lists.newArrayList(instances));
            final UnregisterResponse response = this.request(RestPaths.REGISTRY_UNREGISTER_FULL_PATH, request, UnregisterResponse.class);
            if (ResponseStatusUtil.isFail(response.getResponseStatus())) {
                _logger.error("unregister instances failed. Response:" + JacksonJsonSerializer.INSTANCE.serialize(response));
            } else if (ResponseStatusUtil.isPartialFail(response.getResponseStatus())) {
                _logger.warn("unregister instances patial failed. Response:" + JacksonJsonSerializer.INSTANCE.serialize(response));
            }
            logEvent(response.getResponseStatus(), "registry", "unregister");
        } catch (final Throwable e) {
            _logger.warn("unregister instances failed", e);
            logEvent("registry", "unregister");
        }
    }
}