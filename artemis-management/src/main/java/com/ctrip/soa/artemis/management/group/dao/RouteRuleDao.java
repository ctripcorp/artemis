package com.ctrip.soa.artemis.management.group.dao;

import com.ctrip.soa.artemis.management.dao.DataConfig;
import com.ctrip.soa.artemis.management.group.model.RouteRuleModel;
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
public class RouteRuleDao {
    public static final RouteRuleDao INSTANCE = new RouteRuleDao();

    public List<RouteRuleModel> query() {
        final String sql = "SELECT id, service_id, name, description, status, strategy from service_route_rule where deleted = false";
        return DataConfig.jdbcTemplate().query(sql, new RowMapper<RouteRuleModel>() {
            @Override
            public RouteRuleModel mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                RouteRuleModel routeRule = new RouteRuleModel();
                routeRule.setId(rs.getLong(1));
                routeRule.setServiceId(rs.getString(2));
                routeRule.setName(rs.getString(3));
                routeRule.setDescription(rs.getString(4));
                routeRule.setStatus(rs.getString(5));
                routeRule.setStrategy(rs.getString(6));
                return routeRule;
            }
        });
    }

    protected RouteRuleModel generateRouteRule(RouteRuleModel routeRule) {
        insert(routeRule);
        RouteRuleModel filter = new RouteRuleModel();
        filter.setServiceId(routeRule.getServiceId());
        filter.setName(routeRule.getName());
        List<RouteRuleModel> newRouteRules = select(filter);
        if (newRouteRules.size() == 0) {
            insert(routeRule);
            newRouteRules = select(filter);
        }
        return newRouteRules.get(0);
    }

    protected void delete(final Long... ids) {
        delete(Lists.newArrayList(ids));
    }

    protected void delete(final List<Long> ids) {
        ValueCheckers.notNullOrEmpty(ids, "ids");
        DataConfig.jdbcTemplate().batchUpdate("update service_route_rule set deleted = true where id = ?", new BatchPreparedStatementSetter() {
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

    protected void insertOrUpdate(RouteRuleModel... models) {
        insertOrUpdate(Lists.newArrayList(models));
    }

    protected void insertOrUpdate(final List<RouteRuleModel> models) {
        checkInsertOrUpdateArgument(models);
        DataConfig.jdbcTemplate().batchUpdate("insert into service_route_rule (service_id, name, description, status, strategy) values (?,?,?,?,?)"
                + " on duplicate key update description=?, status=?, strategy=?, deleted=false", new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                RouteRuleModel model = models.get(i);
                Object[] params = new Object[]{model.getServiceId(), model.getName(), model.getDescription(), model.getStatus(), model.getStrategy(),
                model.getDescription(), model.getStatus(), model.getStrategy()};

                for (int index= 0; index < params.length; index++) {
                    ps.setObject(index + 1, params[index]);
                }
            }

            @Override
            public int getBatchSize() {
                return models.size();
            }
        });
    }

    protected void insert(RouteRuleModel... models) {
        insert(Lists.newArrayList(models));
    }

    protected void insert(final List<RouteRuleModel> models) {
        checkInsertOrUpdateArgument(models);
        DataConfig.jdbcTemplate().batchUpdate("insert into service_route_rule (service_id, name, description, status, strategy) values (?,?,?,?,?)"
                + " on duplicate key update deleted=false", new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                RouteRuleModel model = models.get(i);
                ps.setString(1, model.getServiceId());
                ps.setString(2, model.getName());
                ps.setString(3, model.getDescription());
                ps.setString(4, model.getStatus());
                ps.setString(5, model.getStrategy());
            }

            @Override
            public int getBatchSize() {
                return models.size();
            }
        });
    }

    public List<RouteRuleModel> select(RouteRuleModel filter) {
        ValueCheckers.notNull(filter, "filter");
        Map<String, String> conditions = Maps.newHashMap();
        conditions.put("id=?", filter.getId() == null ? null : Long.toString(filter.getId()));
        conditions.put("service_id=?", filter.getServiceId());
        conditions.put("name=?", filter.getName());
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

    public List<RouteRuleModel> select(List<Long> groupIds) {
        if (CollectionValues.isNullOrEmpty(groupIds)) {
            return Lists.newArrayList();
        }
        return query(String.format("id in (%s)", Joiner.on(',').join(groupIds)));
    }

    protected List<RouteRuleModel> query(final String condition, final String... args) {
        return query(condition, Lists.newArrayList(args));
    }

    protected List<RouteRuleModel> query(final String condition, final List<String> args) {
        final String sql = "SELECT id, service_id, name, description,status, strategy, create_time,DataChange_LastTime from service_route_rule where deleted = false";
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

        }, new RowMapper<RouteRuleModel>() {
            @Override
            public RouteRuleModel mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                RouteRuleModel routeRule = new RouteRuleModel();
                routeRule.setId(rs.getLong(1));
                routeRule.setServiceId(rs.getString(2));
                routeRule.setName(rs.getString(3));
                routeRule.setDescription(rs.getString(4));
                routeRule.setStatus(rs.getString(5));
                routeRule.setStrategy(rs.getString(6));
                routeRule.setCreateTime(rs.getTimestamp(7));
                routeRule.setUpdateTime(rs.getTimestamp(8));
                return routeRule;
            }
        });
    }

    protected void checkInsertOrUpdateArgument(List<RouteRuleModel> models) {
        ValueCheckers.notNullOrEmpty(models, "models");
        for (RouteRuleModel model : models) {
            checkInsertOrUpdateArgument(model);
        }
    }

    protected void checkInsertOrUpdateArgument(RouteRuleModel model) {
        ValueCheckers.notNull(model, "routeRule");
        ValueCheckers.notNullOrWhiteSpace(model.getServiceId(), "routeRule.serviceId");
        ValueCheckers.notNullOrWhiteSpace(model.getName(), "routeRule.name");
        ValueCheckers.notNullOrWhiteSpace(model.getStatus(), "routeRule.status");
        ValueCheckers.notNullOrWhiteSpace(model.getStrategy(), "routeRule.strategy");
    }
}
