package com.ctrip.soa.artemis.management.canary;

import com.ctrip.soa.artemis.ErrorCodes;
import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.management.GroupRepository;
import com.ctrip.soa.artemis.management.group.model.GroupModel;
import com.ctrip.soa.artemis.management.group.model.RouteRuleGroupModel;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.ctrip.soa.artemis.util.ServiceGroups;
import com.ctrip.soa.artemis.util.ServiceNodeUtil;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fang_j on 10/07/2016.
 */
public class CanaryServiceImpl implements CanaryService {
    private static final Logger logger = LoggerFactory.getLogger(CanaryServiceImpl.class);
    private static CanaryServiceImpl instance;

    public static CanaryServiceImpl getInstance() {
        if (instance == null) {
            synchronized (CanaryServiceImpl.class) {
                if (instance == null) {
                    instance = new CanaryServiceImpl();
                }
            }
        }
        return instance;
    }

    private final GroupRepository groupRepository = GroupRepository.getInstance();
    @Override
    public UpdateCanaryIPsResponse updateCanaryIPs(final UpdateCanaryIPsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.canary.update.canary-ips", new Func<UpdateCanaryIPsResponse>() {
            @Override
            public UpdateCanaryIPsResponse execute() {
                UpdateCanaryIPsResponse response = new UpdateCanaryIPsResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }
                try {
                    GroupModel group = CanaryServices.generateCanaryGroup(request.getServiceId(), request.getAppId());
                    Long routeRuleId = groupRepository.generateRouteRule(CanaryServices.generateCanaryRouteRule(request.getServiceId())).getRouteRuleId();
                    Long groupId = groupRepository.generateGroup(group).getGroupId();
                    groupRepository.generateRouteRuleGroup(new RouteRuleGroupModel(null, routeRuleId, groupId, ServiceGroups.DEFAULT_WEIGHT_VALUE));
                    groupRepository.updateGroupInstances(request, groupId, Sets.newHashSet(request.getCanaryIps()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    String errorMessage = "update canary ips";
                    logger.error(errorMessage, ex);
                    response.setResponseStatus(ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.INTERNAL_SERVICE_ERROR));
                    return response;
                }
            }
        });
    }

    private boolean check(Object request, HasResponseStatus response) {
        if (request == null) {
            response.setResponseStatus(ResponseStatusUtil.newFailStatus("request is null", ErrorCodes.BAD_REQUEST));
            return false;
        }
        return true;
    }

    private boolean updateCheck(Object request, HasResponseStatus response) {
        return ServiceNodeUtil.checkCurrentNode(response) && check(request, response);
    }
}