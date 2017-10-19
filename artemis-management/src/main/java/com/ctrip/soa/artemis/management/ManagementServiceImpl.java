package com.ctrip.soa.artemis.management;

import java.util.List;

import com.ctrip.soa.artemis.management.instance.*;
import com.ctrip.soa.artemis.management.server.*;
import com.ctrip.soa.caravan.common.value.StringValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.soa.artemis.ErrorCodes;
import com.ctrip.soa.artemis.management.dao.InstanceModel;
import com.ctrip.soa.artemis.management.dao.ServerModel;
import com.ctrip.soa.artemis.management.util.CheckResult;
import com.ctrip.soa.artemis.management.util.ModelUtil;
import com.ctrip.soa.artemis.trace.ArtemisTraceExecutor;
import com.ctrip.soa.artemis.util.ResponseStatusUtil;
import com.ctrip.soa.artemis.util.ServiceNodeUtil;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctrip.soa.caravan.util.serializer.JacksonJsonSerializer;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ManagementServiceImpl implements ManagementService {

    private static final Logger logger = LoggerFactory.getLogger(ManagementServiceImpl.class);

    private static ManagementServiceImpl instance;

    public static ManagementServiceImpl getInstance() {
        if (instance == null) {
            synchronized (ManagementServiceImpl.class) {
                if (instance == null)
                    instance = new ManagementServiceImpl();
            }
        }

        return instance;
    }

    private final ManagementRepository managementRepository = ManagementRepository.getInstance();
    private final GroupRepository groupRepository = GroupRepository.getInstance();

    private ManagementServiceImpl() {

    }
    
    @Override
    public OperateInstanceResponse operateInstance(final OperateInstanceRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.operate-instance", new Func<OperateInstanceResponse>() {
            @Override
            public OperateInstanceResponse execute() {
                return operateInstanceImpl(request);
            }
        });
    }
    
    @Override
    public OperateServerResponse operateServer(final OperateServerRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.operate-server", new Func<OperateServerResponse>() {
            @Override
            public OperateServerResponse execute() {
                return operateServerImpl(request);
            }
        });
    }
    
    @Override
    public GetInstanceOperationsResponse getInstanceOperations(final GetInstanceOperationsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.instance-operations", new Func<GetInstanceOperationsResponse>() {
            @Override
            public GetInstanceOperationsResponse execute() {
                return getInstanceOperationsImpl(request);
            }
        });
    }
    
    public GetServerOperationsResponse getServerOperations(final GetServerOperationsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.server-operations", new Func<GetServerOperationsResponse>() {
            @Override
            public GetServerOperationsResponse execute() {
                return getServerOperationsImpl(request);
            }
        });
    }
    
    @Override
    public GetAllInstanceOperationsResponse getAllInstanceOperations(
            final GetAllInstanceOperationsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.all-instance-operations", new Func<GetAllInstanceOperationsResponse>() {
            @Override
            public GetAllInstanceOperationsResponse execute() {
                return getAllInstanceOperationsImpl(request);
            }
        });
    }

    @Override
    public GetAllServerOperationsResponse getAllServerOperations(
            final GetAllServerOperationsRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.all-server-operations", new Func<GetAllServerOperationsResponse>() {
            @Override
            public GetAllServerOperationsResponse execute() {
                return getAllServerOperationsImpl(request);
            }
        });
    }

    @Override
    public IsInstanceDownResponse isInstanceDown(final IsInstanceDownRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.instance-down", new Func<IsInstanceDownResponse>() {
            @Override
            public IsInstanceDownResponse execute() {
                return isInstanceDownImpl(request);
            }
        });
    }

    @Override
    public IsServerDownResponse isServerDown(final IsServerDownRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.server-down", new Func<IsServerDownResponse>() {
            @Override
            public IsServerDownResponse execute() {
                return isServerDownImpl(request);
            }
        });
    }

    @Override
    public GetServicesResponse getServices(final GetServicesRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.services", new Func<GetServicesResponse>() {
            @Override
            public GetServicesResponse execute() {
                return getServicesImpl(request);
            }
        });
    }
    
    @Override
    public GetServiceResponse getService(final GetServiceRequest request) {
        return ArtemisTraceExecutor.INSTANCE.execute("artemis.service.management.service", new Func<GetServiceResponse>() {
            @Override
            public GetServiceResponse execute() {
                return getServiceImpl(request);
            }
        });
    }

    private OperateInstanceResponse operateInstanceImpl(OperateInstanceRequest request) {
        OperateInstanceResponse response = new OperateInstanceResponse();

        if (!ServiceNodeUtil.checkCurrentNode(response)) {
            return response;
        }
        
        if (request == null) {
            response.setResponseStatus(ResponseStatusUtil.newFailStatus("request is null", ErrorCodes.BAD_REQUEST));
            return response;
        }

        InstanceModel instance = ModelUtil.newInstance(request.getInstanceKey(), request.getOperation(), request.getOperatorId(), request.getToken());
        CheckResult checkResult = ModelUtil.check(instance);
        if (!checkResult.isValid()) {
            response.setResponseStatus(ResponseStatusUtil.newFailStatus(checkResult.errorMessage(), ErrorCodes.BAD_REQUEST));
            return response;
        }

        try {
            if (request.isOperationComplete())
                managementRepository.deleteInstance(instance);
            else
                managementRepository.insertInstance(instance);

            managementRepository.waitForPeerSync();
            response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
            return response;
        } catch (Throwable ex) {
            String errorMessage = "Update instance failed.";
            logger.error(errorMessage, ex);
            response.setResponseStatus(ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.INTERNAL_SERVICE_ERROR));
            return response;
        }
    }

    private OperateServerResponse operateServerImpl(OperateServerRequest request) {
        OperateServerResponse response = new OperateServerResponse();

        if (!ServiceNodeUtil.checkCurrentNode(response)) {
            return response;
        }
        
        if (request == null) {
            response.setResponseStatus(ResponseStatusUtil.newFailStatus("request is null", ErrorCodes.BAD_REQUEST));
            return response;
        }

        ServerModel server = ModelUtil.newServer(request.getServerKey(), request.getOperation(), request.getOperatorId(), request.getToken());
        CheckResult checkResult = ModelUtil.check(server);
        if (!checkResult.isValid()) {
            response.setResponseStatus(ResponseStatusUtil.newFailStatus(checkResult.errorMessage(), ErrorCodes.BAD_REQUEST));
            return response;
        }

        try {
            if (request.isOperationComplete())
                managementRepository.deleteServer(server);
            else
                managementRepository.insertServer(server);

            managementRepository.waitForPeerSync();
            response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
            return response;
        } catch (Throwable ex) {
            String errorMessage = "Update server failed.";
            logger.error(errorMessage, ex);
            response.setResponseStatus(ResponseStatusUtil.newFailStatus(errorMessage, ErrorCodes.INTERNAL_SERVICE_ERROR));
            return response;
        }
    }

    private GetInstanceOperationsResponse getInstanceOperationsImpl(GetInstanceOperationsRequest request) {
        GetInstanceOperationsResponse response = new GetInstanceOperationsResponse();

        if (request == null) {
            response.setResponseStatus(ResponseStatusUtil.newFailStatus("request is null", ErrorCodes.BAD_REQUEST));
            return response;
        }

        if (request.getInstanceKey() == null) {
            response.setResponseStatus(ResponseStatusUtil.newFailStatus("request.instanceKey is null", ErrorCodes.BAD_REQUEST));
            return response;
        }

        InstanceOperations instanceOperations = managementRepository.getInstanceOperations(request.getInstanceKey());
        response.setOperations(instanceOperations);
        response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
        return response;
    }

    public GetServerOperationsResponse getServerOperationsImpl(GetServerOperationsRequest request) {
        GetServerOperationsResponse response = new GetServerOperationsResponse();

        if (request == null) {
            response.setResponseStatus(ResponseStatusUtil.newFailStatus("request is null", ErrorCodes.BAD_REQUEST));
            return response;
        }

        if (request.getServerKey() == null) {
            response.setResponseStatus(ResponseStatusUtil.newFailStatus("request.serverKey is null", ErrorCodes.BAD_REQUEST));
            return response;
        }

        ServerOperations serverOperations = managementRepository.getServerOperations(request.getServerKey());
        response.setOperations(serverOperations);
        response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
        return response;
    }

    private GetAllInstanceOperationsResponse getAllInstanceOperationsImpl(GetAllInstanceOperationsRequest request) {
        GetAllInstanceOperationsResponse response = new GetAllInstanceOperationsResponse();
        String regionId = request == null ? null : request.getRegionId();
        List<InstanceOperations> allInstanceOperations = managementRepository.getAllInstanceOperations(regionId);
        response.setAllInstanceOperations(allInstanceOperations);
        response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
        return response;
    }

    private GetAllServerOperationsResponse getAllServerOperationsImpl(GetAllServerOperationsRequest request) {
        GetAllServerOperationsResponse response = new GetAllServerOperationsResponse();
        String regionId = request == null ? null : request.getRegionId();
        List<ServerOperations> allServerOperations = managementRepository.getAllServerOperations(regionId);
        response.setAllServerOperations(allServerOperations);
        response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
        return response;
    }

    private IsInstanceDownResponse isInstanceDownImpl(IsInstanceDownRequest request) {
        IsInstanceDownResponse response = new IsInstanceDownResponse();

        if (request == null) {
            response.setResponseStatus(ResponseStatusUtil.newFailStatus("request is null", ErrorCodes.BAD_REQUEST));
            return response;
        }

        if (request.getInstance() == null) {
            response.setResponseStatus(ResponseStatusUtil.newFailStatus("request.instance is null", ErrorCodes.BAD_REQUEST));
            return response;
        }

        response.setDown(managementRepository.isInstanceDown(request.getInstance()));
        response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
        return response;
    }

    private IsServerDownResponse isServerDownImpl(IsServerDownRequest request) {
        IsServerDownResponse response = new IsServerDownResponse();

        if (request == null) {
            response.setResponseStatus(ResponseStatusUtil.newFailStatus("request is null", ErrorCodes.BAD_REQUEST));
            return response;
        }

        if (request.getServerKey() == null) {
            response.setResponseStatus(ResponseStatusUtil.newFailStatus("request.serverKey null", ErrorCodes.BAD_REQUEST));
            return response;
        }

        response.setDown(managementRepository.isServerDown(request.getServerKey()));
        response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
        return response;
    }

    private GetServicesResponse getServicesImpl(GetServicesRequest request) {
        GetServicesResponse response = new GetServicesResponse(managementRepository.getAllServices(), System.currentTimeMillis(),
                ResponseStatusUtil.SUCCESS_STATUS);
        return response;
    }
    
    private GetServiceResponse getServiceImpl(GetServiceRequest request) {
        GetServiceResponse response = new GetServiceResponse();
        try {
            if (request == null || StringValues.isNullOrWhitespace(request.getServiceId())) {
                return new GetServiceResponse(null, ResponseStatusUtil.newFailStatus("Request is null or request.servcieId is null.", ErrorCodes.BAD_REQUEST));
            }
            final String serviceId = request.getServiceId();
            response.setService(managementRepository.getService(serviceId));
            response.setGroups(groupRepository.getServiceInstanceGroups(serviceId));
            response.setResponseStatus(ResponseStatusUtil.SUCCESS_STATUS);
        } catch (Throwable ex) {
            logger.error("GetService failed. Request: " + JacksonJsonSerializer.INSTANCE.serialize(request), ex);
            response.setResponseStatus(ResponseStatusUtil.newFailStatus(ex.getMessage(), ErrorCodes.INTERNAL_SERVICE_ERROR));
        }
        return response;
    }
}
