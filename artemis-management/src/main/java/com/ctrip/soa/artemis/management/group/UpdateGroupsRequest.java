package com.ctrip.soa.artemis.management.group;

import com.ctrip.soa.artemis.management.common.OperationContext;
import com.ctrip.soa.artemis.management.group.Group;

import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class UpdateGroupsRequest extends OperationContext {
    private List<Group> groups;

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "UpdateGroupsRequest{" +
                "groups=" + groups +
                "} " + super.toString();
    }
}
