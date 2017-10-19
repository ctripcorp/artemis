package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.dao.DataConfig;
import com.ctrip.soa.artemis.management.group.log.GroupLog;
import com.ctrip.soa.artemis.management.group.model.GroupLogModel;
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
public class GroupLogDao {
    public static final GroupLogDao INSTANCE = new GroupLogDao();

    private GroupLogDao() {}

    public List<GroupLog> select(GroupLogModel filter) {
        ValueCheckers.notNull(filter, "filter");
        Map<String, Object> conditions = Maps.newHashMap();
        conditions.put("name=?", filter.getName());
        conditions.put("service_id=?", filter.getServiceId());
        conditions.put("region_id=?", filter.getRegionId());
        conditions.put("zone_id=?", filter.getZoneId());
        conditions.put("app_id=?", filter.getAppId());
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

    public List<GroupLog> query(final String condition, final List<Object> args) {
        final String sql = "select id, service_id, region_id, zone_id, name, app_id, status, operation, operator_id, token, extensions, reason, create_time, datachange_lasttime from service_group_log";
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

        }, new RowMapper<GroupLog>() {
            @Override
            public GroupLog mapRow(final ResultSet rs, final int arg1) throws SQLException {
                final GroupLog log = new GroupLog();
                log.setId(rs.getLong(1));
                log.setServiceId(rs.getString(2));
                log.setRegionId(rs.getString(3));
                log.setZoneId(rs.getString(4));
                log.setName(rs.getString(5));
                log.setAppId(rs.getString(6));
                log.setStatus(rs.getString(7));
                log.setOperation(rs.getString(8));
                log.setOperatorId(rs.getString(9));
                log.setToken(rs.getString(10));
                log.setExtensions(rs.getString(11));
                log.setReason(rs.getString(12));
                log.setCreateTime(rs.getTimestamp(13));
                log.setUpdateTime(rs.getTimestamp(14));
                return log;
            }
        });
    }

    public void insert(final GroupLogModel... logs) {
        if ((logs == null) || (logs.length == 0)) {
            return;
        }
        this.insert(Lists.newArrayList(logs));
    }

    public void insert(final List<GroupLogModel> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        DataConfig.jdbcTemplate().batchUpdate(
                "insert into service_group_log (service_id, region_id, zone_id, name, app_id, status, operation, operator_id, token, extensions, reason) values (?,?,?,?,?,?,?,?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public int getBatchSize() {
                        return logs.size();
                    }

                    @Override
                    public void setValues(final PreparedStatement ps, final int index) throws SQLException {
                        final GroupLogModel log = logs.get(index);
                        ps.setString(1, log.getServiceId());
                        ps.setString(2, log.getRegionId());
                        ps.setString(3, log.getZoneId());
                        ps.setString(4, log.getName());
                        ps.setString(5, log.getAppId());
                        ps.setString(6, log.getStatus());
                        ps.setString(7, log.getOperation());
                        ps.setString(8, log.getOperatorId());
                        ps.setString(9, log.getToken());
                        ps.setString(10, log.getExtensions());
                        ps.setString(11, log.getReason());
                    }
                });
    }
}
