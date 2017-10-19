package com.ctrip.soa.artemis.client.discovery;

import com.ctrip.soa.artemis.client.ServiceChangeEvent;
import com.ctrip.soa.artemis.client.ServiceChangeListener;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class DefaultServiceChangeListener implements ServiceChangeListener
{
    private final List<ServiceChangeEvent> _serviceChangeEvents = Lists.newArrayList();

    public List<ServiceChangeEvent> getServiceChangeEvents() {
        return _serviceChangeEvents;
    }

    @Override
    public void onChange(final ServiceChangeEvent event) {
        _serviceChangeEvents.add(event);
    }
}
