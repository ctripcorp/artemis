package com.ctrip.soa.artemis.web;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
@Named
@Singleton
public class JsonSerializationHack {

    @Inject
    private CustomObjectMapper objectMapper;

    @Inject
    private RequestMappingHandlerAdapter adapter;

    @Inject
    public void init() {
        for (HttpMessageConverter<?> converter : adapter.getMessageConverters()) {
            if (!(converter instanceof MappingJackson2HttpMessageConverter))
                continue;

            ((MappingJackson2HttpMessageConverter) converter).setObjectMapper(objectMapper);
        }
    }

}
