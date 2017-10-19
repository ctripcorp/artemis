package com.ctrip.soa.artemis.util;

import java.util.Comparator;
import java.util.Objects;

import com.ctrip.soa.artemis.InstanceChange;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class InstanceChangeComparator implements Comparator<InstanceChange> {

    public static final InstanceChangeComparator DEFAULT = new InstanceChangeComparator();

    @Override
    public int compare(InstanceChange o1, InstanceChange o2) {
        if (Objects.equals(o1, o2))
            return 0;

        if (o1 == null)
            return -1;

        if (o2 == null)
            return 1;

        if (o1.getChangeTime() <= o2.getChangeTime())
            return -1;

        return 1;
    }

}
