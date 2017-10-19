package com.ctrip.soa.artemis.management.group.util;

import com.ctrip.soa.caravan.common.delegate.Func1;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by fang_j on 2017/3/20.
 */
class Converts {
    public static <T, V> List<V> convert(List<T> values, Func1<T, V> converter) {
        List<V> res = Lists.newArrayList();
        if (CollectionValues.isNullOrEmpty(values)) {
            return res;
        }
        for (T value : values) {
            if (value == null) {
                continue;
            }
            res.add(converter.execute(value));
        }
        return res;
    }
}
