package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.dao.DataConfig;
import com.ctrip.soa.artemis.management.group.model.GroupTagModel;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.ValueCheckers;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
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
public class GroupTagDao {
    public static final GroupTagDao INSTANCE = new GroupTagDao();

    private GroupTagDao(){}

    public List<GroupTagModel> query() {
        return DataConfig.jdbcTemplate().query("select group_id, tag, value from service_group_tag",
                new RowMapper<GroupTagModel>() {
                    @Override
                    public GroupTagModel mapRow(ResultSet rs, int i) throws SQLException {
                        GroupTagModel tag = new GroupTagModel();
                        tag.setGroupId(rs.getLong(1));
                        tag.setTag(rs.getString(2));
                        tag.setValue(rs.getString(3));
                        return tag;
                    }
                });
    }

    public void delete(GroupTagModel filter) {
        ValueCheckers.notNull(filter, "filter");
        final Map<String, String> conditions = Maps.newHashMap();
        conditions.put("group_id=?", filter.getGroupId() == null ? null : Long.toString(filter.getGroupId()));
        conditions.put("tag=?", filter.getTag());
        conditions.put("value=?", filter.getValue());
        Set<String> removed = Sets.newHashSet();
        for (String key : conditions.keySet()) {
            if (StringValues.isNullOrWhitespace(conditions.get(key))) {
                removed.add(key);
            }
        }
        conditions.keySet().removeAll(removed);
        if (conditions.size() == 0) {
            throw new IllegalStateException("forbidden operation.");
        }
        String sql = "delete from service_group_tag where " + Joiner.on(" and ").join(conditions.keySet());
        DataConfig.jdbcTemplate().update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                List<String> args = Lists.newArrayList(conditions.values());
                for (int i = 0; i < args.size(); i++) {
                    ps.setString(i + 1, args.get(i));
                }
            }
        });
    }

    public void insertOrUpdate(GroupTagModel... models) {
        insertOrUpdate(Lists.newArrayList(models));
    }

    public void insertOrUpdate(final List<GroupTagModel> models) {
        checkInsertOrUpdateArgument(models);
        DataConfig.jdbcTemplate().batchUpdate("insert into service_group_tag (group_id, tag, value) values (?,?,?) on duplicate key update value = ?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        GroupTagModel model = models.get(i);
                        ps.setLong(1, model.getGroupId());
                        ps.setString(2, model.getTag());
                        ps.setString(3, model.getValue());
                        ps.setString(4, model.getValue());
                    }

                    @Override
                    public int getBatchSize() {
                        return models.size();
                    }
                });
    }

    protected List<GroupTagModel> query(final String condition, final List<String> args) {
        final String sql = "SELECT id, group_id, tag, value, create_time, DataChange_LastTime from service_group_tag";
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

        }, new RowMapper<GroupTagModel>()
        {
            @Override
            public GroupTagModel mapRow ( final ResultSet rs, final int rowNum)throws SQLException {
                GroupTagModel groupTag = new GroupTagModel();
                groupTag.setId(rs.getLong(1));
                groupTag.setGroupId(rs.getLong(2));
                groupTag.setTag(rs.getString(3));
                groupTag.setValue(rs.getString(4));
                groupTag.setCreateTime(rs.getTimestamp(5));
                groupTag.setUpdateTime(rs.getTimestamp(6));
                return groupTag;
            }
        });
    }

    private void checkInsertOrUpdateArgument(List<GroupTagModel> models) {
        ValueCheckers.notNullOrEmpty(models, "models");
        for (GroupTagModel model : models) {
            ValueCheckers.notNull(model, "groupTag");
            ValueCheckers.notNull(model.getGroupId(), "groupTag.groupId");
            ValueCheckers.notNullOrWhiteSpace(model.getTag(), "groupTag.tag");
            ValueCheckers.notNullOrWhiteSpace(model.getValue(), "groupTag.value");
        }
    }
}
