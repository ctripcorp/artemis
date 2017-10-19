package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.ResponseStatus;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetGroupTagsResponse implements HasResponseStatus {
    private ResponseStatus responseStatus;
    private GroupTags groupTags;

    @Override
    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    @Override
    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public GroupTags getGroupTags() {
        return groupTags;
    }

    public void setGroupTags(GroupTags groupTags) {
        this.groupTags = groupTags;
    }
}
