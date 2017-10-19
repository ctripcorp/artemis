package com.ctrip.soa.artemis.web.websocket;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by fang_j on 10/07/2016.
 */
public class DelayItem<T> implements Delayed {
    private final long time;
    private final T item;

    public DelayItem(final T item, long timeout, TimeUnit unit) {
        this.item = item;
        time = System.nanoTime() + TimeUnit.NANOSECONDS.convert(timeout, unit);
    }

    public T item() {
        return item;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(time - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    @SuppressWarnings("all")
    public int compareTo(Delayed o) {
        if (o == this)
            return 0;

        if (o == null) {
            return 1;
        }

        long diff = 0;

        if (o instanceof DelayItem) {
            DelayItem<?> x = (DelayItem<?>) o;
            diff = time - x.time;
        } else {
            diff = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        }
        return diff == 0 ? 0 : (diff < 0 ? -1 : 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DelayItem))
            return false;

        DelayItem<?> delayItem = (DelayItem<?>) o;

        if (time != delayItem.time)
            return false;
        return item != null ? item.equals(delayItem.item) : delayItem.item == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (time ^ (time >>> 32));
        result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }
}
