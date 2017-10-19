package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.dao.DataConfig;
import com.ctrip.soa.artemis.management.group.log.ServiceInstanceLog;
import com.ctrip.soa.artemis.management.group.model.ServiceInstanceLogModel;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.ValueCheckers;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;

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
public class ServiceInstanceLogDao {
    public static final ServiceInstanceLogDao INSTANCE = new ServiceInstanceLogDao();

    private ServiceInstanceLogDao() {}

    public List<ServiceInstanceLog> select(ServiceInstanceLogModel filter) {
        ValueCheckers.notNull(filter, "filter");
        Map<String, Object> conditions = Maps.newHashMap();
        conditions.put("service_id=?", filter.getServiceId());
        conditions.put("instance_id=?", filter.getInstanceId());
        conditions.put("operation=?", filter.getOperation());
        conditions.put("operator_id=?", filter.getOperatorId());

        Set<String> removed = Sets.newHashSet();
        for (String key : conditions.keySet()) {
            Object value = conditions.get(key);
            if (value == null) {
                removed.add(key);
                continue;
            }
            if (value instanceof String) {
                if (StringValues.isNullOrWhitespace((String) value)) {
                    removed.add(key);
                }
            }
        }
        conditions.keySet().removeAll(removed);
        return query(Joiner.on(" and ").join(conditions.keySet()), Lists.newArrayList(conditions.values()));
    }

    public List<ServiceInstanceLog> query(final String condition, final List<Object> args) {
        final String sql = "select id, service_id, instance_id, ip, machine_name, metadata, port, protocol, region_id, zone_id, healthy_check_url, url, group_id, operation, operator_id, token, create_time, datachange_lasttime from service_instance_log";
        return DataConfig.jdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(final Connection conn) throws SQLException {
                final PreparedStatement ps;
                if (StringValues.isNullOrWhitespace(condition)) {
                    ps = conn.prepareStatement(sql);
                } else {
                    ps = conn.prepareStatement(Joiner.on(" ").join(sql, "where", condition));
                    for (int i = 0; i < args.size(); i++) {
                        ps.setObject(i + 1, args.get(i));
                    }
                }
                return ps;
            }

        }, new RowMapper<ServiceInstanceLog>() {
            @Override
            public ServiceInstanceLog mapRow(final ResultSet rs, final int arg1) throws SQLException {
                final ServiceInstanceLog log = new ServiceInstanceLog();
                log.setId(rs.getLong(1));
                log.setServiceId(rs.getString(2));
                log.setInstanceId(rs.getString(3));
                log.setIp(rs.getString(4));
                log.setMachineName(rs.getString(5));
                log.setMetadata(rs.getString(6));
                log.setPort(rs.getInt(7));
                log.setProtocol(rs.getString(8));
                log.setRegionId(rs.getString(9));
                log.setZoneId(rs.getString(10));
                log.setHealthCheckUrl(rs.getString(11));
                log.setUrl(rs.getString(12));
                log.setGroupId(rs.getString(13));
                log.setOperation(rs.getString(14));
                log.setOperatorId(rs.getString(15));
                log.setToken(rs.getString(16));
                log.setCreateTime(rs.getTimestamp(17));
                log.setUpdateTime(rs.getTimestamp(18));
                return log;
            }
        });
    }

    protected void insert(final ServiceInstanceLogModel... logs) {
        if ((logs == null) || (logs.length == 0)) {
            return;
        }
        this.insert(Lists.newArrayList(logs));
    }

    protected void insert(final List<ServiceInstanceLogModel> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        DataConfig.jdbcTemplate().batchUpdate(
                "insert into service_instance_log (service_id, instance_id, ip, machine_name, metadata, port, protocol, region_id, zone_id, healthy_check_url, url, group_id, operation, operator_id, token) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public int getBatchSize() {
                        return logs.size();
                    }

                    @Override
                    public void setValues(final PreparedStatement ps, final int index) throws SQLException {
                        final ServiceInstanceLogModel log = logs.get(index);
                        Object[] params = new Object[] {log.getServiceId(), log.getInstanceId(),
                                log.getIp(), log.getMachineName(), log.getMetadata(), log.getPort(), log.getProtocol(), log.getRegionId(), log.getZoneId(), log.getHealthCheckUrl(), log.getUrl(), log.getGroupId(),
                                log.getOperation(), log.getOperatorId(), log.getToken()};
                        for (int i = 0; i < params.length; i++) {
                            ps.setObject(i + 1, params[i]);
                        }
                    }
                });
    }
}
