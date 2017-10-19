package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.dao.DataConfig;
import com.ctrip.soa.artemis.management.group.log.GroupTagLog;
import com.ctrip.soa.artemis.management.group.model.GroupTagLogModel;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupTagLogDao {
    public static final GroupTagLogDao INSTANCE = new GroupTagLogDao();

    private GroupTagLogDao() {}

    public List<GroupTagLog> query(final String condition, final Object... args) {
        final String sql = "select id, group_id, operation, tag, value, operator_id, token, extensions, create_time, datachange_lasttime from service_group_tag_log";
        return DataConfig.jdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(final Connection conn) throws SQLException {
                final PreparedStatement ps;
                if (StringValues.isNullOrWhitespace(condition)) {
                    ps = conn.prepareStatement(sql);
                } else {
                    ps = conn.prepareStatement(Joiner.on(" ").join(sql, "where", condition));
                    for (int i = 0; i < args.length; i++) {
                        ps.setObject(i + 1, args[i]);
                    }
                }
                return ps;
            }

        }, new RowMapper<GroupTagLog>() {
            @Override
            public GroupTagLog mapRow(final ResultSet rs, final int arg1) throws SQLException {
                final GroupTagLog log = new GroupTagLog();
                log.setId(rs.getLong(1));
                log.setGroupId(rs.getLong(2));
                log.setOperation(rs.getString(3));
                log.setTag(rs.getString(4));
                log.setValue(rs.getString(5));
                log.setOperatorId(rs.getString(6));
                log.setToken(rs.getString(7));
                log.setExtensions(rs.getString(8));
                log.setCreateTime(rs.getTimestamp(9));
                log.setUpdateTime(rs.getTimestamp(10));
                return log;
            }
        });
    }

    public void insert(final GroupTagLogModel... logs) {
        if ((logs == null) || (logs.length == 0)) {
            return;
        }
        this.insert(Lists.newArrayList(logs));
    }

    public void insert(final List<GroupTagLogModel> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        DataConfig.jdbcTemplate().batchUpdate(
                "insert into service_group_tag_log (group_id, operation, tag, value, operator_id, token, extensions) values (?,?,?,?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public int getBatchSize() {
                        return logs.size();
                    }

                    @Override
                    public void setValues(final PreparedStatement ps, final int index) throws SQLException {
                        final GroupTagLogModel log = logs.get(index);
                        ps.setLong(1, log.getGroupId());
                        ps.setString(2, log.getOperation());
                        ps.setString(3, log.getTag());
                        ps.setString(4, log.getValue());
                        ps.setString(5, log.getOperatorId());
                        ps.setString(6, log.getToken());
                        ps.setString(7, log.getExtensions());
                    }
                });
    }
}
