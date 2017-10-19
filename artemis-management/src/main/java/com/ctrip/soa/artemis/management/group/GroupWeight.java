package com.ctrip.soa.artemis.management.group;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupWeight extends Group {
    private Integer weight;

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "GroupWeight{" +
                "weight=" + weight +
                '}';
    }
}
