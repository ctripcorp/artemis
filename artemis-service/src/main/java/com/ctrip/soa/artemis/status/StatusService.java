package com.ctrip.soa.artemis.status;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public interface StatusService {

    GetClusterNodeStatusResponse getClusterNodeStatus(GetClusterNodeStatusRequest request);

    GetClusterStatusResponse getClusterStatus(GetClusterStatusRequest request);

    GetLeasesStatusResponse getLeasesStatus(GetLeasesStatusRequest request);

    GetLeasesStatusResponse getLegacyLeasesStatus(GetLeasesStatusRequest request);

    GetConfigStatusResponse getConfigStatus(GetConfigStatusRequest request);

    GetDeploymentStatusResponse getDeploymentStatus(GetDeploymentStatusRequest request);

}
