package com.ctrip.soa.artemis.management.group.util;

import com.ctrip.soa.artemis.management.group.GroupTags;
import com.ctrip.soa.artemis.management.group.model.GroupTagModel;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.common.value.MapValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupTagsUtil {
    public static List<GroupTagModel> newGroupTags(GroupTags groupTags) {
        List<GroupTagModel> tags = Lists.newArrayList();
        if (groupTags == null || groupTags.getGroupId() == null || MapValues.isNullOrEmpty(groupTags.getTags())) {
            return tags;
        }
        Long groupId = groupTags.getGroupId();

        for (Map.Entry<String,String> entry : groupTags.getTags().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringValues.isNullOrWhitespace(key) || StringValues.isNullOrWhitespace(value)) {
                continue;
            }
            tags.add(new GroupTagModel(groupId, key, value));
        }

        return tags;
    }

    public static List<GroupTagModel> newGroupTags(List<GroupTags> groupTagsList) {
        List<GroupTagModel> tags = Lists.newArrayList();
        if (CollectionValues.isNullOrEmpty(groupTagsList)) {
            return tags;
        }

        for (GroupTags groupTags : groupTagsList) {
            if (groupTags == null) {
                continue;
            }

            tags.addAll(newGroupTags(groupTags));
        }

        return tags;
    }
}
