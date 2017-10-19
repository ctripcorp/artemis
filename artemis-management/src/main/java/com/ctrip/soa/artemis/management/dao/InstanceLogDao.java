package com.ctrip.soa.artemis.management.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.soa.caravan.common.value.checker.ValueCheckers;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;

import com.ctrip.soa.artemis.management.log.InstanceOperationLog;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
public class InstanceLogDao {
    public static final InstanceLogDao INSTANCE = new InstanceLogDao();

    private InstanceLogDao() {}

    public List<InstanceOperationLog> select(InstanceLogModel filter, Boolean complete) {
        ValueCheckers.notNull(filter, "filter");
        Map<String, String> conditions = Maps.newHashMap();
        conditions.put("service_id=?", filter.getServiceId());
        conditions.put("region_id=?", filter.getRegionId());
        conditions.put("instance_id=?", filter.getInstanceId());
        conditions.put("operation=?", filter.getOperation());
        conditions.put("operator_id=?", filter.getOperatorId());
        if (complete != null) {
            conditions.put("complete=?", complete ? "1" : "0");
        }

        Set<String> removed = Sets.newHashSet();
        for (String key : conditions.keySet()) {
            if (StringValues.isNullOrWhitespace(conditions.get(key))) {
                removed.add(key);
            }
        }
        conditions.keySet().removeAll(removed);
        return query(Joiner.on(" and ").join(conditions.keySet()), Lists.newArrayList(conditions.values()));
    }

    public List<InstanceOperationLog> query(final String condition, final List<String> args) {
        final String sql = "select id, region_id, service_id, instance_id, operation, operator_id, token, extensions, complete, create_time, datachange_lasttime from instance_log";
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

        }, new RowMapper<InstanceOperationLog>() {
            @Override
            public InstanceOperationLog mapRow(final ResultSet rs, final int arg1) throws SQLException {
                final InstanceOperationLog log = new InstanceOperationLog();
                log.setId(rs.getLong(1));
                log.setRegionId(rs.getString(2));
                log.setServiceId(rs.getString(3));
                log.setInstanceId(rs.getString(4));
                log.setOperation(rs.getString(5));
                log.setOperatorId(rs.getString(6));
                log.setToken(rs.getString(7));
                log.setExtensions(rs.getString(8));
                log.setComplete(rs.getBoolean(9));
                log.setCreateTime(rs.getTimestamp(10));
                log.setUpdateTime(rs.getTimestamp(11));
                return log;
            }
        });
    }

    public void insert(final InstanceLogModel... logs) {
        if ((logs == null) || (logs.length == 0)) {
            return;
        }
        this.insert(Lists.newArrayList(logs));
    }

    public void insert(final List<InstanceLogModel> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        DataConfig.jdbcTemplate().batchUpdate(
                "insert into instance_log (region_id, service_id, instance_id, operation, operator_id, token, extensions, complete) values (?,?,?,?,?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public int getBatchSize() {
                        return logs.size();
                    }

                    @Override
                    public void setValues(final PreparedStatement ps, final int index) throws SQLException {
                        final InstanceLogModel log = logs.get(index);
                        ps.setString(1, log.getRegionId());
                        ps.setString(2, log.getServiceId());
                        ps.setString(3, log.getInstanceId());
                        ps.setString(4, log.getOperation());
                        ps.setString(5, log.getOperatorId());
                        ps.setString(6, log.getToken());
                        ps.setString(7, log.getExtensions());
                        ps.setBoolean(8, log.isComplete());
                    }
                });
    }
}
