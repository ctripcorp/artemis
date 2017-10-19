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

import com.ctrip.soa.artemis.management.log.ServerOperationLog;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServerLogDao {
    public static final ServerLogDao INSTANCE = new ServerLogDao();

    private ServerLogDao() {}

    public List<ServerOperationLog> select(ServerLogModel filter, Boolean complete) {
        ValueCheckers.notNull(filter, "filter");
        Map<String, String> conditions = Maps.newHashMap();
        conditions.put("region_id=?", filter.getRegionId());
        conditions.put("server_id=?", filter.getServerId());
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

    public List<ServerOperationLog> query(final String condition, final List<String> args) {
        final String sql = "select id, region_id, server_id, operation, operator_id, token, extensions, complete, create_time, datachange_lasttime from server_log";
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

        }, new RowMapper<ServerOperationLog>() {
            @Override
            public ServerOperationLog mapRow(final ResultSet rs, final int arg1) throws SQLException {
                final ServerOperationLog log = new ServerOperationLog();
                log.setId(rs.getLong(1));
                log.setRegionId(rs.getString(2));
                log.setServerId(rs.getString(3));
                log.setOperation(rs.getString(4));
                log.setOperatorId(rs.getString(5));
                log.setToken(rs.getString(6));
                log.setExtensions(rs.getString(7));
                log.setComplete(rs.getBoolean(8));
                log.setCreateTime(rs.getTimestamp(9));
                log.setUpdateTime(rs.getTimestamp(10));
                return log;
            }
        });
    }

    public void insert(final ServerLogModel... logs) {
        if ((logs == null) || (logs.length == 0)) {
            return;
        }
        this.insert(Lists.newArrayList(logs));
    }

    public void insert(final List<ServerLogModel> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        DataConfig.jdbcTemplate().batchUpdate(
                "insert into server_log (region_id, server_id, operation, operator_id, token, extensions, complete) values (?,?,?,?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public int getBatchSize() {
                        return logs.size();
                    }

                    @Override
                    public void setValues(final PreparedStatement ps, final int index) throws SQLException {
                        final ServerLogModel log = logs.get(index);
                        ps.setString(1, log.getRegionId());
                        ps.setString(2, log.getServerId());
                        ps.setString(3, log.getOperation());
                        ps.setString(4, log.getOperatorId());
                        ps.setString(5, log.getToken());
                        ps.setString(6, log.getExtensions());
                        ps.setBoolean(7, log.isComplete());
                    }
                });
    }
}
