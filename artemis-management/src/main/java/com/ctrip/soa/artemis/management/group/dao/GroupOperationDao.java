package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.dao.DataConfig;
import com.ctrip.soa.artemis.management.group.model.GroupOperationModel;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.ValueCheckers;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import java.sql.*;
import java.util.List;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupOperationDao {
    public static final GroupOperationDao INSTANCE = new GroupOperationDao();

    private GroupOperationDao() {
    }

    public List<GroupOperationModel> query() {
        return DataConfig.jdbcTemplate().query("select group_id, operation from service_group_operation",
                new RowMapper<GroupOperationModel>() {
                    @Override
                    public GroupOperationModel mapRow(ResultSet rs, int i) throws SQLException {
                        GroupOperationModel groupOperation = new GroupOperationModel();
                        groupOperation.setGroupId(rs.getLong(1));
                        groupOperation.setOperation(rs.getString(2));
                        return groupOperation;
                    }
                });
    }

    protected void delete(GroupOperationModel groupOperation) {
        delete(Lists.newArrayList(groupOperation));
    }

    protected void delete(final List<GroupOperationModel> groupOperations) {
        ValueCheckers.notNullOrEmpty(groupOperations, "group operations");
        DataConfig.jdbcTemplate().batchUpdate("delete from service_group_operation where group_id =? and operation = ?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        GroupOperationModel groupOperation = groupOperations.get(i);
                        ps.setLong(1, groupOperation.getGroupId());
                        ps.setString(2, groupOperation.getOperation());
                    }

                    @Override
                    public int getBatchSize() {
                        return groupOperations.size();
                    }
                });
    }

    protected void insertOrUpdate(GroupOperationModel... operationModels) {
        insertOrUpdate(Lists.newArrayList(operationModels));
    }

    protected void insertOrUpdate(final List<GroupOperationModel> models) {
        checkInsertOrUpdateArgument(models);
        DataConfig.jdbcTemplate().batchUpdate("insert ignore into service_group_operation (group_id, operation) values (?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        GroupOperationModel groupOperation = models.get(i);
                        ps.setLong(1, groupOperation.getGroupId());
                        ps.setString(2, groupOperation.getOperation());
                    }

                    @Override
                    public int getBatchSize() {
                        return models.size();
                    }
                });
    }

    protected List<GroupOperationModel> query(final String condition, final List<String> args) {
        final String sql = "SELECT id, group_id,operation,create_time,DataChange_LastTime from service_group_operation";
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

        }, new RowMapper<GroupOperationModel>() {
            @Override
            public GroupOperationModel mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                GroupOperationModel groupOperation = new GroupOperationModel();
                groupOperation.setId(rs.getLong(1));
                groupOperation.setGroupId(rs.getLong(2));
                groupOperation.setOperation(rs.getString(3));
                groupOperation.setCreateTime(rs.getTimestamp(4));
                groupOperation.setUpdateTime(rs.getTimestamp(5));
                return groupOperation;
            }
        });
    }

    private void checkInsertOrUpdateArgument(List<GroupOperationModel> models) {
        ValueCheckers.notNullOrEmpty(models, "models");
        for (GroupOperationModel model : models) {
            ValueCheckers.notNull(model, "groupOperation");
            ValueCheckers.notNull(model.getGroupId(), "groupOperation.groupId");
            ValueCheckers.notNullOrWhiteSpace(model.getOperation(), "groupOperation.operation");
        }
    }
}
