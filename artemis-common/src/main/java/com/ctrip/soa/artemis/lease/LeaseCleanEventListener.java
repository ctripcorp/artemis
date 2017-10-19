package com.ctrip.soa.artemis.lease;

import java.util.List;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface LeaseCleanEventListener<T> {

    void onClean(List<Lease<T>> cleaned);

}
