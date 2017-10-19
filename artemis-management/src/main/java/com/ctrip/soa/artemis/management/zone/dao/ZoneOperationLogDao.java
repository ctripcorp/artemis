package com.ctrip.soa.artemis.management.zone.dao;

import com.ctrip.soa.artemis.management.dao.DataConfig;
import com.ctrip.soa.artemis.management.log.ZoneOperationLog;
import com.ctrip.soa.artemis.management.zone.model.ZoneOperationLogModel;
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
public class ZoneOperationLogDao {
    public static final ZoneOperationLogDao INSTANCE = new ZoneOperationLogDao();

    private ZoneOperationLogDao() {}

    public List<ZoneOperationLog> select(ZoneOperationLogModel filter, Boolean complete) {
        ValueCheckers.notNull(filter, "filter");
        Map<String, String> conditions = Maps.newHashMap();
        conditions.put("service_id=?", filter.getServiceId());
        conditions.put("region_id=?", filter.getRegionId());
        conditions.put("zone_id=?", filter.getZoneId());
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

    public List<ZoneOperationLog> query(final String condition, final List<String> args) {
        final String sql = "select id, region_id, service_id, zone_id, operation, operator_id, token, complete, create_time, datachange_lasttime from service_zone_log";
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

        }, new RowMapper<ZoneOperationLog>() {
            @Override
            public ZoneOperationLog mapRow(final ResultSet rs, final int arg1) throws SQLException {
                final ZoneOperationLog log = new ZoneOperationLog();
                log.setId(rs.getLong(1));
                log.setRegionId(rs.getString(2));
                log.setServiceId(rs.getString(3));
                log.setZoneId(rs.getString(4));
                log.setOperation(rs.getString(5));
                log.setOperatorId(rs.getString(6));
                log.setToken(rs.getString(7));
                log.setComplete(rs.getBoolean(8));
                log.setCreateTime(rs.getTimestamp(9));
                log.setUpdateTime(rs.getTimestamp(10));
                return log;
            }
        });
    }

    public void insert(final ZoneOperationLogModel... logs) {
        if ((logs == null) || (logs.length == 0)) {
            return;
        }
        this.insert(Lists.newArrayList(logs));
    }

    public void insert(final List<ZoneOperationLogModel> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        DataConfig.jdbcTemplate().batchUpdate(
                "insert into service_zone_log (region_id, service_id, zone_id, operation, operator_id, token, complete) values (?,?,?,?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public int getBatchSize() {
                        return logs.size();
                    }

                    @Override
                    public void setValues(final PreparedStatement ps, final int index) throws SQLException {
                        final ZoneOperationLogModel log = logs.get(index);
                        ps.setString(1, log.getRegionId());
                        ps.setString(2, log.getServiceId());
                        ps.setString(3, log.getZoneId());
                        ps.setString(4, log.getOperation());
                        ps.setString(5, log.getOperatorId());
                        ps.setString(6, log.getToken());
                        ps.setBoolean(7, log.isComplete());
                    }
                });
    }
}
