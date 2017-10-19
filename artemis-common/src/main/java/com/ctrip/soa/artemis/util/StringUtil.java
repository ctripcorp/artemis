package com.ctrip.soa.artemis.util;

import com.ctrip.soa.caravan.common.net.NetworkInterfaceManager;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public final class StringUtil {

    private StringUtil() {

    }

    public static boolean hasLocalhost(String s) {
        if (s == null)
            return false;

        return s.indexOf(NetworkInterfaceManager.INSTANCE.localhostIP()) != -1;
    }

}
