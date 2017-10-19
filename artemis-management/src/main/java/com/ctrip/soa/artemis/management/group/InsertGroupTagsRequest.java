package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.management.common.OperationContext;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class InsertGroupTagsRequest extends OperationContext {
    private List<GroupTags> groupTagsList;

    public List<GroupTags> getGroupTagsList() {
        return groupTagsList;
    }

    public void setGroupTagsList(List<GroupTags> groupTagsList) {
        this.groupTagsList = groupTagsList;
    }

    @Override
    public String toString() {
        return "InsertGroupTagsRequest{" +
                "groupTagsList=" + groupTagsList +
                "} " + super.toString();
    }
}
