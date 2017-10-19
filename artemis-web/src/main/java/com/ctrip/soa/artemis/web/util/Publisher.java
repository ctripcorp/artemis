package com.ctrip.soa.artemis.web.util;

import com.ctrip.soa.artemis.InstanceChange;

/**
 * Created by fang_j on 10/07/2016.
 */
public interface Publisher {
    public boolean publish(InstanceChange instanceChange);

}
