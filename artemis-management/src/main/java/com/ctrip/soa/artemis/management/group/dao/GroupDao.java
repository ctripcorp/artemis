package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.dao.DataConfig;
import com.ctrip.soa.artemis.management.group.model.GroupModel;
import com.ctrip.soa.caravan.common.value.CollectionValues;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctrip.soa.caravan.common.value.checker.ValueCheckers;
import com.google.common.base.Joiner;
import com.google.common.collect.*;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by fang_j on 10/07/2016.
 */
public class GroupDao {
    public static final GroupDao INSTANCE = new GroupDao();

    private GroupDao() {
    }

    protected GroupModel generateGroup(GroupModel group) {
        GroupModel filter = new GroupModel();
        filter.setServiceId(group.getServiceId());
        filter.setRegionId(group.getRegionId());
        filter.setZoneId(group.getZoneId());
        filter.setName(group.getName());
        List<GroupModel> newGroups = select(filter);
        if (newGroups.size() == 0) {
            insert(group);
            newGroups = select(filter);
        }
        return newGroups.get(0);
    }

    public List<GroupModel> query() {
        final String sql = "SELECT id, service_id, region_id, zone_id, name, app_id, description,status from service_group where deleted = false";
        return DataConfig.jdbcTemplate().query(sql, new RowMapper<GroupModel>() {
            @Override
            public GroupModel mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                GroupModel group = new GroupModel();
                group.setId(rs.getLong(1));
                group.setServiceId(rs.getString(2));
                group.setRegionId(rs.getString(3));
                group.setZoneId(rs.getString(4));
                group.setName(rs.getString(5));
                group.setAppId(rs.getString(6));
                group.setDescription(rs.getString(7));
                group.setStatus(rs.getString(8));
                return group;
            }
        });
    }

    protected void delete(final Long... ids) {
        delete(Lists.newArrayList(ids));
    }

    protected void delete(final List<Long> ids) {
        ValueCheckers.notNullOrEmpty(ids, "ids");
        DataConfig.jdbcTemplate().batchUpdate("update service_group set deleted = true where id = ?", new BatchPreparedStatementSetter() {
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

    protected void insertOrUpdate(final GroupModel... groups) {
        insertOrUpdate(Lists.newArrayList(groups));
    }

    protected void insertOrUpdate(final List<GroupModel> models) {
        checkInsertOrUpdateArgument(models);
        DataConfig.jdbcTemplate().batchUpdate("insert into service_group (service_id, region_id, zone_id, name, app_id, description,status) values (?,?,?,?,?,?,?)"
                + " on duplicate key update app_id=?, description=?, status=?, deleted=false", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                GroupModel model = models.get(i);
                ps.setString(1, model.getServiceId());
                ps.setString(2, model.getRegionId());
                ps.setString(3, model.getZoneId());
                ps.setString(4, model.getName());
                ps.setString(5, model.getAppId());
                ps.setString(6, model.getDescription());
                ps.setString(7, model.getStatus());
                ps.setString(8, model.getAppId());
                ps.setString(9, model.getDescription());
                ps.setString(10, model.getStatus());
            }

            @Override
            public int getBatchSize() {
                return models.size();
            }
        });
    }

    protected void insert(final GroupModel... groups) {
        insert(Lists.newArrayList(groups));
    }

    protected void insert(final List<GroupModel> models) {
        checkInsertOrUpdateArgument(models);
        DataConfig.jdbcTemplate().batchUpdate("insert into service_group (service_id, region_id, zone_id, name, app_id, description,status) values (?,?,?,?,?,?,?)"
                + " on duplicate key update deleted=false", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                GroupModel model = models.get(i);
                ps.setString(1, model.getServiceId());
                ps.setString(2, model.getRegionId());
                ps.setString(3, model.getZoneId());
                ps.setString(4, model.getName());
                ps.setString(5, model.getAppId());
                ps.setString(6, model.getDescription());
                ps.setString(7, model.getStatus());
            }

            @Override
            public int getBatchSize() {
                return models.size();
            }
        });
    }

    public List<GroupModel> select(GroupModel filter) {
        ValueCheckers.notNull(filter, "filter");
        Map<String, String> conditions = Maps.newHashMap();
        conditions.put("id=?", filter.getId() == null ? null : Long.toString(filter.getId()));
        conditions.put("service_id=?", filter.getServiceId());
        conditions.put("region_id=?", filter.getRegionId());
        conditions.put("zone_id=?", filter.getZoneId());
        conditions.put("name=?", filter.getName());
        conditions.put("app_id=?", filter.getAppId());
        conditions.put("status=?", filter.getStatus());

        Set<String> removed = Sets.newHashSet();
        for (String key : conditions.keySet()) {
            if (StringValues.isNullOrWhitespace(conditions.get(key))) {
                removed.add(key);
            }
        }
        conditions.keySet().removeAll(removed);
        return query(Joiner.on(" and ").join(conditions.keySet()), Lists.newArrayList(conditions.values()));
    }

    public List<GroupModel> select(List<Long> groupIds) {
        if (CollectionValues.isNullOrEmpty(groupIds)) {
            return Lists.newArrayList();
        }
        return query(String.format("id in (%s)", Joiner.on(',').join(groupIds)));
    }

    protected List<GroupModel> query(final String condition, final String... args) {
        return query(condition, Lists.newArrayList(args));
    }

    protected List<GroupModel> query(final String condition, final List<String> args) {
        final String sql = "SELECT id, service_id, region_id, zone_id, name, app_id, description, status, create_time,DataChange_LastTime from service_group where deleted = false";
        return DataConfig.jdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(final Connection conn) throws SQLException {
                final PreparedStatement ps;
                if (StringValues.isNullOrWhitespace(condition)) {
                    ps = conn.prepareStatement(sql);
                } else {
                    ps = conn.prepareStatement(Joiner.on(" ").join(sql, "and", condition));
                    for (int i = 0; i < args.size(); i++) {
                        ps.setString(i + 1, args.get(i));
                    }
                }
                return ps;
            }

        }, new RowMapper<GroupModel>() {
            @Override
            public GroupModel mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                GroupModel group = new GroupModel();
                group.setId(rs.getLong(1));
                group.setServiceId(rs.getString(2));
                group.setRegionId(rs.getString(3));
                group.setZoneId(rs.getString(4));
                group.setName(rs.getString(5));
                group.setAppId(rs.getString(6));
                group.setDescription(rs.getString(7));
                group.setStatus(rs.getString(8));
                group.setCreateTime(rs.getTimestamp(9));
                group.setUpdateTime(rs.getTimestamp(10));
                return group;
            }
        });
    }

    protected void checkInsertOrUpdateArgument(List<GroupModel> models) {
        ValueCheckers.notNullOrEmpty(models, "models");
        for (GroupModel model : models) {
            ValueCheckers.notNull(model, "group");
            ValueCheckers.notNullOrWhiteSpace(model.getServiceId(), "group.serviceId");
            ValueCheckers.notNullOrWhiteSpace(model.getRegionId(), "group.regionId");
            ValueCheckers.notNullOrWhiteSpace(model.getZoneId(), "group.zoneId");
            ValueCheckers.notNullOrWhiteSpace(model.getName(), "group.name");
            ValueCheckers.notNullOrWhiteSpace(model.getAppId(), "group.appId");
            ValueCheckers.notNullOrWhiteSpace(model.getStatus(), "group.status");
        }
    }
}
