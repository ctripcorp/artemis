package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.dao.DataConfig;
import com.ctrip.soa.artemis.management.group.log.RouteRuleGroupLog;
import com.ctrip.soa.artemis.management.group.model.RouteRuleGroupLogModel;
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
public class RouteRuleGroupLogDao {
    public static final RouteRuleGroupLogDao INSTANCE = new RouteRuleGroupLogDao();

    private RouteRuleGroupLogDao() {}

    public List<RouteRuleGroupLog> select(RouteRuleGroupLogModel filter) {
        ValueCheckers.notNull(filter, "filter");
        Map<String, Object> conditions = Maps.newHashMap();
        conditions.put("group_id=?", filter.getGroupId());
        conditions.put("route_rule_id=?", filter.getRouteRuleId());
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

    public List<RouteRuleGroupLog> query(final String condition, final List<Object> args) {
        final String sql = "select id, group_id, route_rule_id, weight, operation, operator_id, token, extensions, reason, create_time, datachange_lasttime from service_route_rule_group_log";
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

        }, new RowMapper<RouteRuleGroupLog>() {
            @Override
            public RouteRuleGroupLog mapRow(final ResultSet rs, final int arg1) throws SQLException {
                final RouteRuleGroupLog log = new RouteRuleGroupLog();
                log.setId(rs.getLong(1));
                log.setGroupId(rs.getLong(2));
                log.setRouteRuleId(rs.getLong(3));
                if (rs.getString(4) != null) {
                    log.setWeight(rs.getInt(4));
                }
                log.setOperation(rs.getString(5));
                log.setOperatorId(rs.getString(6));
                log.setToken(rs.getString(7));
                log.setExtensions(rs.getString(8));
                log.setReason(rs.getString(9));
                log.setCreateTime(rs.getTimestamp(10));
                log.setUpdateTime(rs.getTimestamp(11));
                return log;
            }
        });
    }

    public void insert(final RouteRuleGroupLogModel... logs) {
        if ((logs == null) || (logs.length == 0)) {
            return;
        }
        this.insert(Lists.newArrayList(logs));
    }

    public void insert(final List<RouteRuleGroupLogModel> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        DataConfig.jdbcTemplate().batchUpdate(
                "insert into service_route_rule_group_log (group_id, route_rule_id, weight, operation, operator_id, token, extensions, reason) values (?,?,?,?,?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public int getBatchSize() {
                        return logs.size();
                    }

                    @Override
                    public void setValues(final PreparedStatement ps, final int index) throws SQLException {
                        final RouteRuleGroupLogModel log = logs.get(index);
                        ps.setLong(1, log.getGroupId());
                        ps.setLong(2, log.getRouteRuleId());
                        if (log.getUnreleasedWeight() == null) {
                            ps.setString(3, null);
                        } else {
                            ps.setLong(3, log.getUnreleasedWeight());
                        }
                        ps.setString(4, log.getOperation());
                        ps.setString(5, log.getOperatorId());
                        ps.setString(6, log.getToken());
                        ps.setString(7, log.getExtensions());
                        ps.setString(8, log.getReason());
                    }
                });
    }
}
