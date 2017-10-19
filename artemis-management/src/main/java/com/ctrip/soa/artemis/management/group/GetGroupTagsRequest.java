package com.ctrip.soa.artemis.management.group;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GetGroupTagsRequest {
    private Long tagId;
    private Long groupId;
    private String tagKey;

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    @Override
    public String toString() {
        return "GetGroupTagsRequest{" +
                "tagId=" + tagId +
                ", groupId=" + groupId +
                ", tagKey='" + tagKey + '\'' +
                '}';
    }
}
