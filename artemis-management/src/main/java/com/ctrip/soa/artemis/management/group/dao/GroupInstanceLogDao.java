package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.dao.DataConfig;
import com.ctrip.soa.artemis.management.group.log.GroupInstanceLog;
import com.ctrip.soa.artemis.management.group.model.GroupInstanceLogModel;
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
public class GroupInstanceLogDao {
    public static final GroupInstanceLogDao INSTANCE = new GroupInstanceLogDao();

    private GroupInstanceLogDao() {}

    public List<GroupInstanceLog> select(GroupInstanceLogModel filter) {
        ValueCheckers.notNull(filter, "filter");
        Map<String, Object> conditions = Maps.newHashMap();
        conditions.put("group_id=?", filter.getGroupId());
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

    public List<GroupInstanceLog> query(final String condition, final List<Object> args) {
        final String sql = "select id, group_id, instance_id, operation, operator_id, token, reason, create_time, datachange_lasttime from service_group_instance_log";
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

        }, new RowMapper<GroupInstanceLog>() {
            @Override
            public GroupInstanceLog mapRow(final ResultSet rs, final int arg1) throws SQLException {
                final GroupInstanceLog log = new GroupInstanceLog();
                log.setId(rs.getLong(1));
                log.setGroupId(rs.getLong(2));
                log.setInstanceId(rs.getString(3));
                log.setOperation(rs.getString(4));
                log.setOperatorId(rs.getString(5));
                log.setToken(rs.getString(6));
                log.setReason(rs.getString(7));
                log.setCreateTime(rs.getTimestamp(8));
                log.setUpdateTime(rs.getTimestamp(9));
                return log;
            }
        });
    }

    protected void insert(final GroupInstanceLogModel... logs) {
        if ((logs == null) || (logs.length == 0)) {
            return;
        }
        this.insert(Lists.newArrayList(logs));
    }

    protected void insert(final List<GroupInstanceLogModel> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        DataConfig.jdbcTemplate().batchUpdate(
                "insert into service_group_instance_log (group_id, instance_id, operation, operator_id, token, reason) values (?,?,?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public int getBatchSize() {
                        return logs.size();
                    }

                    @Override
                    public void setValues(final PreparedStatement ps, final int index) throws SQLException {
                        final GroupInstanceLogModel log = logs.get(index);
                        ps.setLong(1, log.getGroupId());
                        ps.setString(2, log.getInstanceId());
                        ps.setString(3, log.getOperation());
                        ps.setString(4, log.getOperatorId());
                        ps.setString(5, log.getToken());
                        ps.setString(6, log.getReason());
                    }
                });
    }
}
