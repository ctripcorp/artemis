package com.ctrip.soa.artemis.management.group;

import java.util.Map;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupTags {
    private Long groupId;
    private Map<String, String> tags;

    public GroupTags() {
    }

    public GroupTags(Long groupId, Map<String, String> tags) {
        this.groupId = groupId;
        this.tags = tags;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}
