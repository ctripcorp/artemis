package com.ctrip.soa.artemis.web;

import javax.inject.Named;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
@Named
public class CustomObjectMapper extends ObjectMapper {

    private static final long serialVersionUID = 1L;

    public CustomObjectMapper() {
        configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    }

}
