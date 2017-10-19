package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.dao.DataConfig;
import com.ctrip.soa.artemis.management.group.model.ServiceInstanceModel;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.ValueCheckers;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServiceInstanceDao {
    public static final ServiceInstanceDao INSTANCE = new ServiceInstanceDao();

    private ServiceInstanceDao() {
    }

    public List<ServiceInstanceModel> query() {
        final String sql = "SELECT service_id, instance_id, ip, machine_name, metadata, port, protocol, region_id, zone_id, healthy_check_url, url, group_id from service_instance";
        return DataConfig.jdbcTemplate().query(sql, new RowMapper<ServiceInstanceModel>() {
            @Override
            public ServiceInstanceModel mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                ServiceInstanceModel serviceInstance = new ServiceInstanceModel();
                serviceInstance.setServiceId(rs.getString(1));
                serviceInstance.setInstanceId(rs.getString(2));
                serviceInstance.setIp(rs.getString(3));
                serviceInstance.setMachineName(rs.getString(4));
                serviceInstance.setMetadata(rs.getString(5));
                serviceInstance.setPort(rs.getInt(6));
                serviceInstance.setProtocol(rs.getString(7));
                serviceInstance.setRegionId(rs.getString(8));
                serviceInstance.setZoneId(rs.getString(9));
                serviceInstance.setHealthCheckUrl(rs.getString(10));
                serviceInstance.setUrl(rs.getString(11));
                serviceInstance.setGroupId(rs.getString(12));
                return serviceInstance;
            }
        });
    }

    protected void delete(final Long... ids) {
        delete(Lists.newArrayList(ids));
    }

    protected void delete(final List<Long> ids) {
        ValueCheckers.notNullOrEmpty(ids, "ids");
        DataConfig.jdbcTemplate().batchUpdate("delete from service_instance where id = ?", new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return ids.size();
            }

            @Override
            public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                final Long id = ids.get(i);
                if (id == null) {
                    ps.setLong(1, 0L);
                } else {
                    ps.setLong(1, id);
                }
            }
        });
    }

    protected void deleteByFilters(final List<ServiceInstanceModel> filters) {
        checkInsertOrUpdateArgument(filters);
        DataConfig.jdbcTemplate().batchUpdate("delete from service_instance where service_id = ? and instance_id=?", new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return filters.size();
            }

            @Override
            public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                final ServiceInstanceModel filter = filters.get(i);
                ps.setString(1, filter.getServiceId());
                ps.setString(2, filter.getInstanceId());
            }
        });
    }

    protected void insertOrUpdate(final ServiceInstanceModel... groupInstances) {
        insertOrUpdate(Lists.newArrayList(groupInstances));
    }

    protected void insertOrUpdate(final List<ServiceInstanceModel> models) {
        checkInsertOrUpdateArgument(models);
        DataConfig.jdbcTemplate().batchUpdate("insert into service_instance (service_id, instance_id, ip, machine_name, metadata, port, protocol, region_id, zone_id, healthy_check_url, url, description, group_id) values (?,?,?,?,?,?,?,?,?,?,?,?,?) on duplicate key " +
                        "update  ip=?, machine_name=?, metadata=?, port=?, protocol=?, region_id=?, zone_id=?, healthy_check_url=?, url=?, description=?, group_id=?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ServiceInstanceModel model = models.get(i);
                        Object[] params = new Object[] {model.getServiceId(), model.getInstanceId(),
                                model.getIp(), model.getMachineName(), model.getMetadata(), model.getPort(), model.getProtocol(),model.getRegionId(), model.getZoneId(), model.getHealthCheckUrl(), model.getUrl(), model.getDescription(),model.getGroupId(),
                                model.getIp(), model.getMachineName(), model.getMetadata(), model.getPort(), model.getProtocol(),model.getRegionId(), model.getZoneId(), model.getHealthCheckUrl(), model.getUrl(), model.getDescription(), model.getGroupId()};
                        for (int index = 0; index < params.length; index++) {
                            ps.setObject(index + 1, params[index]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return models.size();
                    }
                });
    }

    public List<ServiceInstanceModel> select(ServiceInstanceModel filter) {
        ValueCheckers.notNull(filter, "filter");
        Map<String, String> conditions = Maps.newHashMap();
        conditions.put("service_id=?", filter.getServiceId());
        conditions.put("instance_id=?", filter.getInstanceId());

        Set<String> removed = Sets.newHashSet();
        for (String key : conditions.keySet()) {
            if (StringValues.isNullOrWhitespace(conditions.get(key))) {
                removed.add(key);
            }
        }
        conditions.keySet().removeAll(removed);
        return query(Joiner.on(" and ").join(conditions.keySet()), Lists.newArrayList(conditions.values()));
    }

    public List<ServiceInstanceModel> select(List<Long> groupIds) {
        if (CollectionValues.isNullOrEmpty(groupIds)) {
            return Lists.newArrayList();
        }
        return query(String.format("id in (%s)", Joiner.on(',').join(groupIds)));
    }

    protected List<ServiceInstanceModel> query(final String condition, final String... args) {
        return query(condition, Lists.newArrayList(args));
    }

    protected List<ServiceInstanceModel> query(final String condition, final List<String> args) {
        final String sql = "SELECT id, service_id, instance_id, ip, machine_name, metadata, port, protocol, region_id, zone_id, healthy_check_url,url, description, group_id, create_time,DataChange_LastTime from service_instance";
        return DataConfig.jdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(final Connection conn) throws SQLException {
                final PreparedStatement ps;
                if (StringValues.isNullOrWhitespace(condition)) {
                    ps = conn.prepareStatement(sql);
                } else {
                    ps = conn.prepareStatement(Joiner.on(" ").join(sql, "where", condition));
                    for (int i = 0; i < args.size(); i++) {
                        ps.setString(i + 1, args.get(i));
                    }
                }
                return ps;
            }

        }, new RowMapper<ServiceInstanceModel>() {
            @Override
            public ServiceInstanceModel mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                ServiceInstanceModel serviceInstance = new ServiceInstanceModel();
                serviceInstance.setId(rs.getLong(1));
                serviceInstance.setServiceId(rs.getString(2));
                serviceInstance.setInstanceId(rs.getString(3));
                serviceInstance.setIp(rs.getString(4));
                serviceInstance.setMachineName(rs.getString(5));
                serviceInstance.setMetadata(rs.getString(6));
                serviceInstance.setPort(rs.getInt(7));
                serviceInstance.setProtocol(rs.getString(8));
                serviceInstance.setRegionId(rs.getString(9));
                serviceInstance.setZoneId(rs.getString(10));
                serviceInstance.setHealthCheckUrl(rs.getString(11));
                serviceInstance.setUrl(rs.getString(12));
                serviceInstance.setDescription(rs.getString(13));
                serviceInstance.setGroupId(rs.getString(14));
                serviceInstance.setCreateTime(rs.getTimestamp(15));
                serviceInstance.setUpdateTime(rs.getTimestamp(16));
                return serviceInstance;
            }
        });
    }

    protected void checkInsertOrUpdateArgument(List<ServiceInstanceModel> models) {
        ValueCheckers.notNullOrEmpty(models, "models");
        for (ServiceInstanceModel model : models) {
            ValueCheckers.notNull(model, "serviceInstance");
            ValueCheckers.notNull(model.getServiceId(), "serviceInstance.serviceId");
            ValueCheckers.notNullOrWhiteSpace(model.getInstanceId(), "serviceInstance.instanceId");
            ValueCheckers.notNullOrWhiteSpace(model.getUrl(), "serviceInstance.url");
            ValueCheckers.notNullOrWhiteSpace(model.getIp(), "serviceInstance.ip");
            ValueCheckers.notNullOrWhiteSpace(model.getMachineName(), "serviceInstance.machineName");
            ValueCheckers.notNullOrWhiteSpace(model.getMetadata(), "serviceInstance.metadata");
            ValueCheckers.notNullOrWhiteSpace(model.getProtocol(), "serviceInstance.protocol");
            ValueCheckers.notNullOrWhiteSpace(model.getRegionId(), "serviceInstance.regionId");
            ValueCheckers.notNullOrWhiteSpace(model.getZoneId(), "serviceInstance.zoneId");
            ValueCheckers.notNullOrWhiteSpace(model.getHealthCheckUrl(), "serviceInstance.healthCheckUrl");
            ValueCheckers.notNullOrWhiteSpace(model.getGroupId(), "serviceInstance.groupId");
        }
    }
}
