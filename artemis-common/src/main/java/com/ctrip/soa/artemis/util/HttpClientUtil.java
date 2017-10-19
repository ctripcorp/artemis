package com.ctrip.soa.artemis.util;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.HttpHostConnectException;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class HttpClientUtil {

    public static boolean isHostDisconnected(Throwable ex) {
        return isException(ex, HttpHostConnectException.class);
    }

    public static boolean isSocketError(Throwable ex) {
        return isException(ex, SocketException.class);
    }

    public static boolean isSocketTimeout(Throwable ex) {
        return isException(ex, SocketTimeoutException.class);
    }

    public static boolean isRemoteHostUnavailable(Throwable ex) {
        return isHostDisconnected(ex) || isSocketError(ex);
    }

    private static boolean isException(Throwable ex, Class<?> clazz) {
        if (ex == null)
            return false;

        if (clazz.isInstance(ex))
            return true;

        return isException(ex.getCause(), clazz);
    }

    private HttpClientUtil() {

    }

}
