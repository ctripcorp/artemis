package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.management.common.OperationContext;

/**
 * Created by fang_j on 10/07/2016.
 */
public class DeleteGroupTagsRequest extends OperationContext {
    private Long groupId;
    private String tag;
    private String value;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DeleteGroupTagsRequest{" +
                "groupId=" + groupId +
                ", tag='" + tag + '\'' +
                ", value='" + value + '\'' +
                "} " + super.toString();
    }
}
