package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.dao.DataConfig;
import com.ctrip.soa.artemis.management.group.model.RouteRuleGroupModel;
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
public class RouteRuleGroupDao {
    public static final RouteRuleGroupDao INSTANCE = new RouteRuleGroupDao();

    public List<RouteRuleGroupModel> query() {
        final String sql = "SELECT id, route_rule_id, group_id, weight, unreleased_weight from service_route_rule_group";
        return DataConfig.jdbcTemplate().query(sql, new RowMapper<RouteRuleGroupModel>() {
            @Override
            public RouteRuleGroupModel mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                RouteRuleGroupModel routeRuleGroup = new RouteRuleGroupModel();
                routeRuleGroup.setId(rs.getLong(1));
                routeRuleGroup.setRouteRuleId(rs.getLong(2));
                routeRuleGroup.setGroupId(rs.getLong(3));
                if (rs.getString(4) != null) {
                    routeRuleGroup.setWeight(rs.getInt(4));
                }
                if (rs.getString(5) != null) {
                    routeRuleGroup.setUnreleasedWeight(rs.getInt(5));
                }
                return routeRuleGroup;
            }
        });
    }

    public RouteRuleGroupModel generateGroup(RouteRuleGroupModel routeRuleGroup) {
        RouteRuleGroupModel filter = new RouteRuleGroupModel();
        filter.setGroupId(routeRuleGroup.getGroupId());
        List<RouteRuleGroupModel> newRouteRuleGroups = select(filter);
        if (newRouteRuleGroups.size() == 0) {
            insert(routeRuleGroup);
            newRouteRuleGroups = select(filter);
        }
        return newRouteRuleGroups.get(0);
    }

    protected void delete(final Long... ids) {
        delete(Lists.newArrayList(ids));
    }

    protected void delete(final List<Long> ids) {
        ValueCheckers.notNullOrEmpty(ids, "ids");
        DataConfig.jdbcTemplate().batchUpdate("delete from service_route_rule_group where id = ?", new BatchPreparedStatementSetter() {
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

    protected void insertOrUpdate(final RouteRuleGroupModel... models) {
        insertOrUpdate(Lists.newArrayList(models));
    }

    protected void insertOrUpdate(final List<RouteRuleGroupModel> models) {
        checkInsertOrUpdateArgument(models);
        DataConfig.jdbcTemplate().batchUpdate("insert into service_route_rule_group (route_rule_id, group_id, unreleased_weight) values (?,?,?)"
                + " on duplicate key update unreleased_weight=?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                RouteRuleGroupModel model = models.get(i);
                ps.setLong(1, model.getRouteRuleId());
                ps.setLong(2, model.getGroupId());
                Integer unreleasedWeight = model.getUnreleasedWeight();
                if (unreleasedWeight == null) {
                    ps.setString(3, null);
                    ps.setString(4, null);
                } else {
                    ps.setInt(3, unreleasedWeight);
                    ps.setInt(4, unreleasedWeight);
                }
            }

            @Override
            public int getBatchSize() {
                return models.size();
            }
        });
    }

    protected void insert(final RouteRuleGroupModel... models) {
        insert(Lists.newArrayList(models));
    }

    protected void insert(final List<RouteRuleGroupModel> models) {
        checkInsertOrUpdateArgument(models);
        DataConfig.jdbcTemplate().batchUpdate("insert ignore into service_route_rule_group (route_rule_id, group_id, unreleased_weight) values (?,?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                RouteRuleGroupModel model = models.get(i);
                ps.setLong(1, model.getRouteRuleId());
                ps.setLong(2, model.getGroupId());
                Integer unreleasedWeight = model.getUnreleasedWeight();
                if (unreleasedWeight == null) {
                    ps.setString(3, null);
                } else {
                    ps.setInt(3, unreleasedWeight);
                }
            }

            @Override
            public int getBatchSize() {
                return models.size();
            }
        });
    }

    protected void release(final List<RouteRuleGroupModel> models) {
        checkReleaseArgument(models);
        DataConfig.jdbcTemplate().batchUpdate("update service_route_rule_group set weight = unreleased_weight where route_rule_id=? and group_id=?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        RouteRuleGroupModel model = models.get(i);
                        ps.setLong(1, model.getRouteRuleId());
                        ps.setLong(2, model.getGroupId());
                    }

                    @Override
                    public int getBatchSize() {
                        return models.size();
                    }
                });
    }

    protected void publish(final RouteRuleGroupModel... models) {
        publish(Lists.newArrayList(models));
    }

    protected void publish(final List<RouteRuleGroupModel> models) {
        checkInsertOrUpdateArgument(models);
        DataConfig.jdbcTemplate().batchUpdate("insert into service_route_rule_group (route_rule_id, group_id, weight) values (?,?,?)"
                + " on duplicate key update weight=?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                RouteRuleGroupModel model = models.get(i);
                ps.setLong(1, model.getRouteRuleId());
                ps.setLong(2, model.getGroupId());
                Integer weight = model.getWeight();
                if (weight == null) {
                    ps.setString(3, null);
                    ps.setString(4, null);
                } else {
                    ps.setInt(3, weight);
                    ps.setInt(4, weight);
                }
            }

            @Override
            public int getBatchSize() {
                return models.size();
            }
        });
    }

    public List<RouteRuleGroupModel> select(RouteRuleGroupModel filter) {
        ValueCheckers.notNull(filter, "filter");
        Map<String, Long> conditions = Maps.newHashMap();
        conditions.put("id=?", filter.getId());
        conditions.put("route_rule_id=?", filter.getRouteRuleId());
        conditions.put("group_id=?", filter.getGroupId());

        Set<String> removed = Sets.newHashSet();
        for (String key : conditions.keySet()) {
            if (conditions.get(key) == null) {
                removed.add(key);
            }
        }
        conditions.keySet().removeAll(removed);
        return query(Joiner.on(" and ").join(conditions.keySet()), Lists.newArrayList(conditions.values()));
    }

    public List<RouteRuleGroupModel> select(List<Long> groupIds) {
        if (CollectionValues.isNullOrEmpty(groupIds)) {
            return Lists.newArrayList();
        }
        return query(String.format("id in (%s)", Joiner.on(',').join(groupIds)));
    }

    protected List<RouteRuleGroupModel> query(final String condition, final Long... args) {
        return query(condition, Lists.newArrayList(args));
    }

    protected List<RouteRuleGroupModel> query(final String condition, final List<Long> args) {
        final String sql = "SELECT id, route_rule_id, group_id, weight,unreleased_weight, create_time,DataChange_LastTime from service_route_rule_group";
        return DataConfig.jdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(final Connection conn) throws SQLException {
                final PreparedStatement ps;
                if (StringValues.isNullOrWhitespace(condition)) {
                    ps = conn.prepareStatement(sql);
                } else {
                    ps = conn.prepareStatement(Joiner.on(" ").join(sql, "where", condition));
                    for (int i = 0; i < args.size(); i++) {
                        ps.setLong(i + 1, args.get(i));
                    }
                }
                return ps;
            }

        }, new RowMapper<RouteRuleGroupModel>() {
            @Override
            public RouteRuleGroupModel mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                RouteRuleGroupModel routeRuleGroup = new RouteRuleGroupModel();
                routeRuleGroup.setId(rs.getLong(1));
                routeRuleGroup.setRouteRuleId(rs.getLong(2));
                routeRuleGroup.setGroupId(rs.getLong(3));
                if (rs.getString(4) != null) {
                    routeRuleGroup.setWeight(rs.getInt(4));
                }
                if (rs.getString(5) != null) {
                    routeRuleGroup.setUnreleasedWeight(rs.getInt(5));
                }
                routeRuleGroup.setCreateTime(rs.getTimestamp(6));
                routeRuleGroup.setUpdateTime(rs.getTimestamp(7));
                return routeRuleGroup;
            }
        });
    }

    private void checkInsertOrUpdateArgument(List<RouteRuleGroupModel> models) {
        ValueCheckers.notNullOrEmpty(models, "models");
        for (RouteRuleGroupModel model : models) {
            ValueCheckers.notNull(model, "routeRuleGroup");
            ValueCheckers.notNull(model.getRouteRuleId(), "routeRuleGroup.routeRuleId");
            ValueCheckers.notNull(model.getGroupId(), "routeRuleGroup.groupId");
        }
    }

    private void checkReleaseArgument(List<RouteRuleGroupModel> models) {
        ValueCheckers.notNullOrEmpty(models, "models");
        for (RouteRuleGroupModel model : models) {
            ValueCheckers.notNull(model, "routeRuleGroup");
            ValueCheckers.notNull(model.getRouteRuleId(), "routeRuleGroup.routeRuleId");
            ValueCheckers.notNull(model.getGroupId(), "routeRuleGroup.groupId");
        }
    }
}
