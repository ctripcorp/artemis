package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.dao.DataConfig;
import com.ctrip.soa.artemis.management.group.model.GroupInstanceModel;
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
public class GroupInstanceDao {
    public static final GroupInstanceDao INSTANCE = new GroupInstanceDao();

    private GroupInstanceDao() {
    }

    public List<GroupInstanceModel> query() {
        final String sql = "SELECT id, group_id, instance_id from service_group_instance";
        return DataConfig.jdbcTemplate().query(sql, new RowMapper<GroupInstanceModel>() {
            @Override
            public GroupInstanceModel mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                GroupInstanceModel groupInstance = new GroupInstanceModel();
                groupInstance.setId(rs.getLong(1));
                groupInstance.setGroupId(rs.getLong(2));
                groupInstance.setInstanceId(rs.getString(3));
                return groupInstance;
            }
        });
    }

    protected void delete(final Long... ids) {
        delete(Lists.newArrayList(ids));
    }

    protected void delete(final List<Long> ids) {
        ValueCheckers.notNullOrEmpty(ids, "ids");
        DataConfig.jdbcTemplate().batchUpdate("delete from service_group_instance where id = ?", new BatchPreparedStatementSetter() {
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

    protected void deleteByFilters(final List<GroupInstanceModel> filters){
        checkInsertOrUpdateArgument(filters);
        DataConfig.jdbcTemplate().batchUpdate("delete from service_group_instance where group_id = ? and instance_id=?", new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return filters.size();
            }

            @Override
            public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                final GroupInstanceModel filter = filters.get(i);
                ps.setLong(1, filter.getGroupId());
                ps.setString(2, filter.getInstanceId());
            }
        });
    }

    protected void insert(final GroupInstanceModel... groupInstances) {
        insert(Lists.newArrayList(groupInstances));
    }

    protected void insert(final List<GroupInstanceModel> models) {
        checkInsertOrUpdateArgument(models);
        DataConfig.jdbcTemplate().batchUpdate("insert ignore into service_group_instance (group_id, instance_id) values (?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        GroupInstanceModel model = models.get(i);
                        ps.setLong(1, model.getGroupId());
                        ps.setString(2, model.getInstanceId());
                    }

                    @Override
                    public int getBatchSize() {
                        return models.size();
                    }
                });
    }

    public List<GroupInstanceModel> select(GroupInstanceModel filter) {
        ValueCheckers.notNull(filter, "filter");
        Map<String, String> conditions = Maps.newHashMap();
        conditions.put("id=?", filter.getId() == null ? null : Long.toString(filter.getId()));
        conditions.put("group_id=?", filter.getGroupId() == null ? null : Long.toString(filter.getGroupId()));
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

    public List<GroupInstanceModel> select(List<Long> groupIds) {
        if (CollectionValues.isNullOrEmpty(groupIds)) {
            return Lists.newArrayList();
        }
        return query(String.format("id in (%s)", Joiner.on(',').join(groupIds)));
    }

    protected List<GroupInstanceModel> query(final String condition, final String... args) {
        return query(condition, Lists.newArrayList(args));
    }

    protected List<GroupInstanceModel> query(final String condition, final List<String> args) {
        final String sql = "SELECT id, group_id, instance_id, create_time,DataChange_LastTime from service_group_instance";
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

        }, new RowMapper<GroupInstanceModel>() {
            @Override
            public GroupInstanceModel mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                GroupInstanceModel group = new GroupInstanceModel();
                group.setId(rs.getLong(1));
                group.setGroupId(rs.getLong(2));
                group.setInstanceId(rs.getString(3));
                group.setCreateTime(rs.getTimestamp(4));
                group.setUpdateTime(rs.getTimestamp(5));
                return group;
            }
        });
    }

    protected void checkInsertOrUpdateArgument(List<GroupInstanceModel> models) {
        ValueCheckers.notNullOrEmpty(models, "models");
        for (GroupInstanceModel model : models) {
            ValueCheckers.notNull(model, "groupInstance");
            ValueCheckers.notNull(model.getGroupId(), "groupInstance.groupId");
            ValueCheckers.notNullOrWhiteSpace(model.getInstanceId(), "groupInstance.instanceId");
        }
    }
}
