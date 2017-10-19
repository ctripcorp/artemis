package com.ctrip.soa.artemis.management;

import com.ctrip.soa.artemis.ErrorCodes;
import com.ctrip.soa.artemis.HasResponseStatus;
import com.ctrip.soa.artemis.management.group.*;
import com.ctrip.soa.artemis.management.group.model.*;
import com.ctrip.soa.artemis.management.group.util.*;
import com.ctrip.soa.artemis.ratelimiter.ArtemisRateLimiterManager;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.ctrip.soa.artemis.util.ServiceNodeUtil;
import com.ctrip.soa.caravan.common.concurrent.collect.circularbuffer.timebucket.TimeBufferConfig;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctrip.soa.caravan.configuration.util.RangePropertyConfig;
import com.ctrip.soa.caravan.util.ratelimiter.RateLimiter;
import com.ctrip.soa.caravan.util.ratelimiter.RateLimiterConfig;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupServiceImpl implements GroupService {
    private static volatile GroupServiceImpl instance;

    public static GroupServiceImpl getInstance() {
        if (instance == null) {
            synchronized (GroupServiceImpl.class) {
                if (instance == null)
                    instance = new GroupServiceImpl();
            }
        }

        return instance;
    }

    private GroupServiceImpl() {
    }

    private static final Logger logger = LoggerFactory.getLogger(ManagementServiceImpl.class);
    private final GroupRepository groupRepository = GroupRepository.getInstance();
    private final RateLimiter rateLimiter = ArtemisRateLimiterManager.Instance.getRateLimiter("artemis.service.management.group",
            new RateLimiterConfig(true, new RangePropertyConfig<Long>(30L, 1L, 1000L), new TimeBufferConfig(10 * 1000, 1000)));

    @Override
    public InsertRouteRulesResponse insertRouteRules(final InsertRouteRulesRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.insert.route-rules", new Func<InsertRouteRulesResponse>() {
            @Override
            public InsertRouteRulesResponse execute() {
                InsertRouteRulesResponse response = new InsertRouteRulesResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }

                try {
                    groupRepository.insertRouteRules(request, ServiceRouteRules.newRouteRuleModels(request.getRouteRules()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "insert route-rules failed.", response);
                }
            }
        });
    }

    @Override
    public UpdateRouteRulesResponse updateRouteRules(final UpdateRouteRulesRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.update.route-rules", new Func<UpdateRouteRulesResponse>() {
            @Override
            public UpdateRouteRulesResponse execute() {
                UpdateRouteRulesResponse response = new UpdateRouteRulesResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }

                try {
                    groupRepository.updateRouteRules(
                            request,
                            ServiceRouteRules.newRouteRuleModels(request.getRouteRules()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "update route-rules failed.", response);
                }
            }
        });
    }

    @Override
    public DeleteRouteRulesResponse deleteRouteRules(final DeleteRouteRulesRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.delete.route-rules", new Func<DeleteRouteRulesResponse>() {
            @Override
            public DeleteRouteRulesResponse execute() {
                DeleteRouteRulesResponse response = new DeleteRouteRulesResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }
                try {
                    groupRepository.deleteRouteRules(
                            request,
                            request.getRouteRuleIds());
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "delete route-rules failed.", response);
                }
            }
        });
    }

    @Override
    public GetAllRouteRulesResponse getAllRouteRules(final GetAllRouteRulesRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.get.all-route-rules", new Func<GetAllRouteRulesResponse>() {
            @Override
            public GetAllRouteRulesResponse execute() {
                GetAllRouteRulesResponse response = new GetAllRouteRulesResponse();
                if (!check(request, response)) {
                    return response;
                }
                try {
                    response.setRouteRules(groupRepository.getAllRouteRules(request.getRegionId()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "get all-route-rules failed.", response);
                }
            }
        });
    }

    @Override
    public GetRouteRulesResponse getRouteRules(final GetRouteRulesRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.get.route-rules", new Func<GetRouteRulesResponse>() {
            @Override
            public GetRouteRulesResponse execute() {
                GetRouteRulesResponse response = new GetRouteRulesResponse();
                if (rateLimiter.isRateLimited("get.route-rules")) {
                    response.setResponseStatus(ResponseStatusUtil.RATE_LIMITED_STATUS);
                    return response;
                }
                if (!check(request, response)) {
                    return response;
                }
                try {
                    response.setRouteRules(groupRepository.getRouteRules(new RouteRuleModel(
                            request.getRouteRuleId(), request.getServiceId(), request.getName(), request.getStatus())));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "get route-rules failed.", response);
                }
            }
        });
    }

    @Override
    public InsertRouteRuleGroupsResponse insertRouteRuleGroups(final InsertRouteRuleGroupsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.insert.route-rule-groups", new Func<InsertRouteRuleGroupsResponse>() {
            @Override
            public InsertRouteRuleGroupsResponse execute() {
                InsertRouteRuleGroupsResponse response = new InsertRouteRuleGroupsResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }
                try {
                    groupRepository.insertServiceRouteRuleGroups(
                            request,
                            RouteRuleGroups.newRouteRuleGroupModels(request.getRouteRuleGroups()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "insert route-rule-groups failed.", response);
                }
            }
        });
    }

    @Override
    public UpdateRouteRuleGroupsResponse updateRouteRuleGroups(final UpdateRouteRuleGroupsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.update.route-rule-groups", new Func<UpdateRouteRuleGroupsResponse>() {
            @Override
            public UpdateRouteRuleGroupsResponse execute() {
                UpdateRouteRuleGroupsResponse response = new UpdateRouteRuleGroupsResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }
                try {
                    groupRepository.updateServiceRouteRuleGroups(
                            request,
                            RouteRuleGroups.newRouteRuleGroupModels(request.getRouteRuleGroups()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "update route-rule-groups failed.", response);
                }
            }
        });
    }

    @Override
    public DeleteRouteRuleGroupsResponse deleteRouteRuleGroups(final DeleteRouteRuleGroupsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.delete.route-rule-groups", new Func<DeleteRouteRuleGroupsResponse>() {
            @Override
            public DeleteRouteRuleGroupsResponse execute() {
                DeleteRouteRuleGroupsResponse response = new DeleteRouteRuleGroupsResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }
                try {
                    groupRepository.deleteServiceRouteRuleGroups(
                            request,
                            request.getRouteRuleGroupIds());
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "delete route-rule-groups failed.", response);
                }
            }
        });
    }

    @Override
    public GetAllRouteRuleGroupsResponse getAllRouteRuleGroups(final GetAllRouteRuleGroupsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.get.all-route-rule-group", new Func<GetAllRouteRuleGroupsResponse>() {
            @Override
            public GetAllRouteRuleGroupsResponse execute() {
                GetAllRouteRuleGroupsResponse response = new GetAllRouteRuleGroupsResponse();
                if (!check(request, response)) {
                    return response;
                }
                try {
                    response.setRouteRuleGroups(groupRepository.getAllRouteRuleGroups(request.getRegionId()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "get all route-rule-group failed.", response);
                }
            }
        });
    }

    @Override
    public GetRouteRuleGroupsResponse getRouteRuleGroups(final GetRouteRuleGroupsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.get.route-rule-groups", new Func<GetRouteRuleGroupsResponse>() {
            @Override
            public GetRouteRuleGroupsResponse execute() {
                GetRouteRuleGroupsResponse response = new GetRouteRuleGroupsResponse();
                if (rateLimiter.isRateLimited("get.route-rule-groups")) {
                    response.setResponseStatus(ResponseStatusUtil.RATE_LIMITED_STATUS);
                    return response;
                }
                if (!check(request, response)) {
                    return response;
                }
                try {
                    response.setRouteRuleGroups(groupRepository.getRouteRuleGroups(new RouteRuleGroupModel(request.getRouteRuleGroupId(), request.getRouteRuleId(), request.getGroupId())));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "get route-rule-groups failed.", response);
                }
            }
        });
    }

    @Override
    public ReleaseRouteRuleGroupsResponse releaseRouteRuleGroups(final ReleaseRouteRuleGroupsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.release.route-rule-groups", new Func<ReleaseRouteRuleGroupsResponse>() {
            @Override
            public ReleaseRouteRuleGroupsResponse execute() {
                ReleaseRouteRuleGroupsResponse response = new ReleaseRouteRuleGroupsResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }
                try {
                    groupRepository.releaseServiceRouteRuleGroups(
                            request,
                            RouteRuleGroups.newRouteRuleGroupModels(request.getRouteRuleGroups()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "get route-rule-groups failed.", response);
                }
            }
        });
    }

    @Override
    public InsertGroupsResponse insertGroups(final InsertGroupsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.insert.groups", new Func<InsertGroupsResponse>() {
            @Override
            public InsertGroupsResponse execute() {
                InsertGroupsResponse response = new InsertGroupsResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }

                try {
                    groupRepository.insertGroups(
                            request,
                            Groups.newGroupModels(request.getGroups()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "insert groups failed.", response);
                }
            }
        });
    }

    @Override
    public UpdateGroupsResponse updateGroups(final UpdateGroupsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.update.groups", new Func<UpdateGroupsResponse>() {
            @Override
            public UpdateGroupsResponse execute() {
                UpdateGroupsResponse response = new UpdateGroupsResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }

                try {
                    groupRepository.updateGroups(
                            request,
                            Groups.newGroupModels(request.getGroups()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "update groups failed.", response);
                }
            }
        });
    }

    @Override
    public DeleteGroupsResponse deleteGroups(final DeleteGroupsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.delete.group", new Func<DeleteGroupsResponse>() {
            @Override
            public DeleteGroupsResponse execute() {
                DeleteGroupsResponse response = new DeleteGroupsResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }

                try {
                    groupRepository.deleteGroups(
                            request,
                            request.getGroupIds());
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "delete group failed.", response);
                }
            }
        });
    }

    @Override
    public GetAllGroupsResponse getAllGroups(final GetAllGroupsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.get.all-group", new Func<GetAllGroupsResponse>() {
            @Override
            public GetAllGroupsResponse execute() {
                GetAllGroupsResponse response = new GetAllGroupsResponse();
                if (!check(request, response)) {
                    return response;
                }
                try {
                    response.setGroups(groupRepository.getAllGroups(request.getRegionId()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "get all group failed.", response);
                }
            }
        });
    }

    @Override
    public GetGroupsResponse getGroups(final GetGroupsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.get.groups", new Func<GetGroupsResponse>() {
            @Override
            public GetGroupsResponse execute() {
                GetGroupsResponse response = new GetGroupsResponse();
                if (rateLimiter.isRateLimited("get.groups")) {
                    response.setResponseStatus(ResponseStatusUtil.RATE_LIMITED_STATUS);
                    return response;
                }
                if (!check(request, response)) {
                    return response;
                }
                try {
                    response.setGroups(
                            groupRepository.getGroups(new GroupModel(request.getServiceId(),
                            request.getRegionId(), request.getZoneId(), request.getName(), request.getAppId(), null, request.getStatus())));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "get groups failed.", response);
                }
            }
        });
    }

    @Override
    public OperateGroupOperationsResponse operateGroupOperations(final OperateGroupOperationsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.operate-group-operations", new Func<OperateGroupOperationsResponse>() {
            @Override
            public OperateGroupOperationsResponse execute() {
                OperateGroupOperationsResponse response = new OperateGroupOperationsResponse();
                if (!ServiceNodeUtil.checkCurrentNode(response)) {
                    return response;
                }

                if (request == null) {
                    response.setResponseStatus(ResponseStatusUtil.newFailStatus("request is null", ErrorCodes.BAD_REQUEST));
                    return response;
                }

                try {
                    groupRepository.operateGroupOperations(
                            request,
                            GroupOperationsUtil.newGroupOperationModels(request.getGroupOperationsList()),
                            request.isOperationComplete());
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "operate group operation failed.", response);
                }
            }
        });
    }

    @Override
    public GetAllGroupOperationsResponse getAllGroupOperations(final GetAllGroupOperationsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.get.all-group-operations", new Func<GetAllGroupOperationsResponse>() {
            @Override
            public GetAllGroupOperationsResponse execute() {
                GetAllGroupOperationsResponse response = new GetAllGroupOperationsResponse();
                if (!check(request, response)) {
                    return response;
                }

                try {
                    response.setAllGroupOperations(groupRepository.getAllGroupOperations(request.getRegionId()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "get all group operations failed.", response);
                }
            }
        });
    }

    @Override
    public GetGroupOperationsResponse getGroupOperations(final GetGroupOperationsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.get.group-operations", new Func<GetGroupOperationsResponse>() {
            @Override
            public GetGroupOperationsResponse execute() {
                GetGroupOperationsResponse response = new GetGroupOperationsResponse();
                if (!check(request, response)) {
                    return response;
                }

                try {
                    response.setGroupOperations(groupRepository.getGroupOperations(request.getGroupId()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "get group operations failed.", response);
                }
            }
        });
    }

    @Override
    public InsertGroupTagsResponse insertGroupTags(final InsertGroupTagsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.insert.group-tags", new Func<InsertGroupTagsResponse>() {
            @Override
            public InsertGroupTagsResponse execute() {
                InsertGroupTagsResponse response = new InsertGroupTagsResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }

                try {
                    groupRepository.insertGroupTags(GroupTagsUtil.newGroupTags(request.getGroupTagsList()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "insert group tag failed.", response);
                }
            }
        });
    }

    @Override
    public UpdateGroupTagsResponse updateGroupTags(final UpdateGroupTagsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.update.group-tags", new Func<UpdateGroupTagsResponse>() {
            @Override
            public UpdateGroupTagsResponse execute() {
                UpdateGroupTagsResponse response = new UpdateGroupTagsResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }

                try {
                    groupRepository.updateGroupTags(GroupTagsUtil.newGroupTags(request.getGroupTagsList()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "update group tag failed.", response);
                }
            }
        });
    }

    @Override
    public DeleteGroupTagsResponse deleteGroupTags(final DeleteGroupTagsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.delete.group-tags", new Func<DeleteGroupTagsResponse>() {
            @Override
            public DeleteGroupTagsResponse execute() {
                DeleteGroupTagsResponse response = new DeleteGroupTagsResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }

                try {
                    groupRepository.deleteGroupTags(new GroupTagModel(request.getGroupId(), request.getTag(), request.getValue()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "delete group-tags failed.", response);
                }
            }
        });
    }

    @Override
    public GetGroupTagsResponse getGroupTags(final GetGroupTagsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.get.group-tags", new Func<GetGroupTagsResponse>() {
            @Override
            public GetGroupTagsResponse execute() {
                GetGroupTagsResponse response = new GetGroupTagsResponse();
                if (!check(request, response)) {
                    return response;
                }

                try {
                    response.setGroupTags(groupRepository.getGroupTags(request.getGroupId()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "get group-tags failed.", response);
                }
            }
        });
    }

    @Override
    public GetAllGroupTagsResponse getAllGroupTags(final GetAllGroupTagsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.get.all-group-tags", new Func<GetAllGroupTagsResponse>() {
            @Override
            public GetAllGroupTagsResponse execute() {
                GetAllGroupTagsResponse response = new GetAllGroupTagsResponse();
                if (!check(request, response)) {
                    return response;
                }

                try {
                    response.setAllGroupTags(groupRepository.getAllGroupTags(request.getRegionId()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "get all-group-tags failed.", response);
                }
            }
        });
    }

    @Override
    public CreateRouteRuleResponse createRouteRule(final CreateRouteRuleRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.get.create-route-rule", new Func<CreateRouteRuleResponse>() {
            @Override
            public CreateRouteRuleResponse execute() {
                CreateRouteRuleResponse response = new CreateRouteRuleResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }

                try {
                    groupRepository.createRouteRules(
                            request, request.getRouteRule().getServiceId(), Lists.newArrayList(new RouteRuleInfo(request.getRouteRule(), request.getGroups())));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "create route-rule failed.", response);
                }
            }
        });
    }

    @Override
    public OperateGroupOperationResponse operateGroupOperation(final OperateGroupOperationRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.operate.group-operation", new Func<OperateGroupOperationResponse>() {
            @Override
            public OperateGroupOperationResponse execute() {
                OperateGroupOperationResponse response = new OperateGroupOperationResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }

                try {
                    GroupModel group = new GroupModel(request.getServiceId(), request.getRegionId(), request.getZoneId(), request.getName(),
                            request.getAppId(), request.getDescription(), request.getStatus());

                    groupRepository.operateGroupOperation(
                            request,
                            group, request.isOperationComplete());
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "operate group operation failed.", response);
                }
            }
        });
    }

    @Override
    public InsertGroupInstancesResponse insertGroupInstances(final InsertGroupInstancesRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.insert.group-instances", new Func<InsertGroupInstancesResponse>() {
            @Override
            public InsertGroupInstancesResponse execute() {
                InsertGroupInstancesResponse response = new InsertGroupInstancesResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }

                try {
                    groupRepository.insertGroupInstances(request, GroupInstances.newGroupInstanceModels(request.getGroupInstances()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "insert group instances failed.", response);
                }
            }
        });
    }

    @Override
    public DeleteGroupInstancesResponse deleteGroupInstances(final DeleteGroupsInstancesRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.delete.group-instances", new Func<DeleteGroupInstancesResponse>() {
            @Override
            public DeleteGroupInstancesResponse execute() {
                DeleteGroupInstancesResponse response = new DeleteGroupInstancesResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }

                try {
                    groupRepository.deleteGroupInstances(request, request.getGroupInstanceIds());
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex, "delete group instances failed.", response);
                }
            }
        });
    }

    @Override
    public GetGroupInstancesResponse getGroupInstances(final GetGroupInstancesRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.get.group-instances", new Func<GetGroupInstancesResponse>() {
            @Override
            public GetGroupInstancesResponse execute() {
                GetGroupInstancesResponse response = new GetGroupInstancesResponse();
                if (!check(request, response)) {
                    return response;
                }

                try {
                    response.setGroupInstances(groupRepository.getGroupInstances(new GroupInstanceModel(request.getGroupId(), request.getInstanceId())));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    return response;
                } catch (Throwable ex) {
                    return processException(ex,"get group instances failed.", response);
                }
            }
        });
    }

    @Override
    public OperationResponse insertServiceInstances(final InsertServiceInstancesRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.insert.service-instances", new Func<OperationResponse>() {
            @Override
            public OperationResponse execute() {
                OperationResponse response = new OperationResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }

                try {
                    groupRepository.insertServiceInstances(request, ServiceInstances.newServiceInstanceModels(request.getServiceInstances()));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex,"insert service instances failed.", response);
                }
            }
        });
    }

    @Override
    public OperationResponse deleteServiceInstances(final DeleteServiceInstancesRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.delete.service-instances", new Func<OperationResponse>() {
            @Override
            public OperationResponse execute() {
                OperationResponse response = new OperationResponse();
                if (!updateCheck(request, response)) {
                    return response;
                }

                try {
                    groupRepository.deleteServiceInstances(request, request.getServiceInstanceIds());
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    groupRepository.waitForPeerSync();
                    return response;
                } catch (Throwable ex) {
                    return processException(ex,"delete service instances failed.", response);
                }
            }
        });
    }

    @Override
    public GetServiceInstancesResponse getServiceInstances(final GetServiceInstancesRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.get.service-instances", new Func<GetServiceInstancesResponse>() {
            @Override
            public GetServiceInstancesResponse execute() {
                GetServiceInstancesResponse response = new GetServiceInstancesResponse();
                if (!check(request, response)) {
                    return response;
                }

                try {
                    response.setServiceInstances(groupRepository.getServiceInstances(new ServiceInstanceModel(request.getServiceId(), request.getInstanceId())));
                    response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
                    return response;
                } catch (Throwable ex) {
                    return processException(ex,"get service instances failed.", response);
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

    private <T extends  HasResponseStatus> T processException(Throwable ex, String errorMessage, T response) {
        logger.error(errorMessage, ex);
        response.setResponseStatus(ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.INTERNAL_SERVICE_ERROR));
        return response;
    }
}
