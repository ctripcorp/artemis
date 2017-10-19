package com.ctrip.soa.artemis.util;

import com.ctrip.soa.artemis.ErrorCodes;
import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.cluster.NodeManager;
import com.ctrip.soa.artemis.cluster.ServiceNode;
import com.ctrip.soa.artemis.cluster.ServiceNodeStatus;
import com.ctrip.soa.caravan.common.value.BooleanValues;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public final class ServiceNodeUtil {

    public static boolean isUp(String status) {
        return ServiceNodeStatus.Status.UP.equals(status);
    }

    public static boolean isUp(ServiceNodeStatus nodeStatus) {
        if (nodeStatus == null)
            return false;

        return isUp(nodeStatus.getStatus());
    }

    public static boolean isDown(String status) {
        return ServiceNodeStatus.Status.DOWN.equals(status);
    }

    public static boolean isDown(ServiceNodeStatus nodeStatus) {
        if (nodeStatus == null)
            return false;

        return isDown(nodeStatus.getStatus());
    }

    public static boolean canServiceRegistry(ServiceNodeStatus nodeStatus) {
        if (nodeStatus == null)
            return false;

        if (isUp(nodeStatus))
            return true;

        if (isDown(nodeStatus))
            return false;

        return BooleanValues.isTrue(nodeStatus.isCanServiceRegistry());
    }

    public static boolean canServiceDiscovery(ServiceNodeStatus nodeStatus) {
        if (nodeStatus == null)
            return false;

        if (isUp(nodeStatus))
            return true;

        if (isDown(nodeStatus))
            return false;

        return BooleanValues.isTrue(nodeStatus.isCanServiceDiscovery());
    }

    public static ServiceNodeStatus newUnknownNodeStatus(ServiceNode node) {
        return new ServiceNodeStatus(node, ServiceNodeStatus.Status.UNKNOWN, null, null, null, null);
    }
    
    public static boolean checkCurrentNode(HasResponseStatus response) {
        try {
            checkCurrentNode();
            return true;
        } catch (Exception e) {
            response.setResponseStatus(ResponseStatusUtil.newFailStatus(e.getMessage(), ErrorCodes.SERVICE_UNAVAILABLE));
            return false;
        }
    }
    
    public static void checkCurrentNode() throws Exception {
        ServiceNodeStatus nodeStatus = NodeManager.INSTANCE.nodeStatus();
        
        if (!ServiceNodeUtil.isUp(nodeStatus)) {
            throw new Exception("Serivce is not in up state. Current status: " + nodeStatus);
        }
    }

    private ServiceNodeUtil() {

    }

}
