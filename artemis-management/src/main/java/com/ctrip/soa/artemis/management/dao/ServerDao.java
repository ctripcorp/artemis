package com.ctrip.soa.artemis.management.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ctrip.soa.artemis.ServerKey;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;

import com.ctrip.soa.caravan.common.value.StringValues;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Created by fang_j on 10/07/2016.
 */
public class ServerDao {

    public static final ServerDao INSTANCE = new ServerDao();

    private final String queryServerSql = "select distinct region_Id, server_id, operation from server where region_id=? and server_id=?";

    private final String queryServersSql = "select distinct region_Id, server_id, operation from server where region_id=?";

    private final String queryServersSql2 = "select distinct region_Id, server_id, operation from server";

    private final RowMapper<ServerModel> queryServersRowMapper = new RowMapper<ServerModel>() {
        @Override
        public ServerModel mapRow(final ResultSet rs, final int arg1) throws SQLException {
            final ServerModel instance = new ServerModel();
            instance.setRegionId(rs.getString(1));
            instance.setServerId(rs.getString(2));
            instance.setOperation(rs.getString(3));
            return instance;
        }
    };

    private ServerDao() {

    }

    public List<ServerModel> queryServer(final String regionId, final String serverId) {
        return DataConfig.jdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(final Connection conn) throws SQLException {
                final PreparedStatement ps = conn.prepareStatement(queryServerSql);
                ps.setString(1, regionId);
                ps.setString(2, serverId);
                return ps;
            }
        }, queryServersRowMapper);
    }

    public List<ServerModel> queryServers(final String regionId) {
        return DataConfig.jdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(final Connection conn) throws SQLException {
                final PreparedStatement ps = conn.prepareStatement(queryServersSql);
                ps.setString(1, regionId);
                return ps;
            }
        }, queryServersRowMapper);
    }

    public List<ServerModel> queryServers() {
        return DataConfig.jdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(final Connection conn) throws SQLException {
                return conn.prepareStatement(queryServersSql2);
            }
        }, queryServersRowMapper);
    }

    public List<ServerModel> query(final String condition, final String... args) {
        final String sql = "select id, region_id, server_id, operation, operator_id, token, create_time, datachange_lasttime from server";
        return DataConfig.jdbcTemplate().query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(final Connection conn) throws SQLException {
                final PreparedStatement ps;
                if (StringValues.isNullOrWhitespace(condition)) {
                    ps = conn.prepareStatement(sql);
                } else {
                    ps = conn.prepareStatement(Joiner.on(" ").join(sql, "where", condition));
                    for (int i = 0; i < args.length; i++) {
                        ps.setString(i + 1, args[i]);
                    }
                }
                return ps;
            }

        }, new RowMapper<ServerModel>() {
            @Override
            public ServerModel mapRow(final ResultSet rs, final int arg1) throws SQLException {
                final ServerModel instance = new ServerModel();
                instance.setId(rs.getLong(1));
                instance.setRegionId(rs.getString(2));
                instance.setServerId(rs.getString(3));
                instance.setOperation(rs.getString(4));
                instance.setOperatorId(rs.getString(5));
                instance.setToken(rs.getString(6));
                instance.setCreateTime(rs.getDate(7));
                instance.setUpdateTime(rs.getDate(8));
                return instance;
            }
        });
    }

    public void delete(final Long... ids) {
        DataConfig.jdbcTemplate().batchUpdate("delete from server where id = ?", new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return ids.length;
            }

            @Override
            public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                ps.setLong(1, ids[i]);
            }
        });
    }

    public void delete(final ServerModel... servers) {
        if ((servers == null) || (servers.length == 0)) {
            return;
        }
        this.delete(Lists.newArrayList(servers));
    }

    public void delete(final List<ServerModel> serverList) {
        if (CollectionUtils.isEmpty(serverList)) {
            return;
        }
        DataConfig.jdbcTemplate().batchUpdate("delete from server where region_id=? and server_id=? and operation=?", new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return serverList.size();
            }

            @Override
            public void setValues(final PreparedStatement ps, final int index) throws SQLException {
                final ServerModel server = serverList.get(index);
                ps.setString(1, server.getRegionId());
                ps.setString(2, server.getServerId());
                ps.setString(3, server.getOperation());
            }
        });
    }

    public void destroyServers(final List<ServerKey> serverKeys) {
        if (CollectionUtils.isEmpty(serverKeys)) {
            return;
        }
        DataConfig.jdbcTemplate().batchUpdate("delete from server where region_id=? and server_id=?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public int getBatchSize() {
                        return serverKeys.size();
                    }

                    @Override
                    public void setValues(final PreparedStatement ps, final int index) throws SQLException {
                        ServerKey serverKey = serverKeys.get(index);
                        ps.setString(1, serverKey.getRegionId());
                        ps.setString(2, serverKey.getServerId());
                    }
                });
    }

    public void insert(final ServerModel... servers) {
        if ((servers == null) || (servers.length == 0)) {
            return;
        }
        this.insert(Lists.newArrayList(servers));
    }

    public void insert(final List<ServerModel> serverList) {
        if (CollectionUtils.isEmpty(serverList)) {
            return;
        }
        DataConfig.jdbcTemplate().batchUpdate("insert into server (region_id, server_id, operation, operator_id, token) values (?,?,?,?,?)"
                + " on duplicate key update operator_id=?, token=?", new BatchPreparedStatementSetter() {
                    @Override
                    public int getBatchSize() {
                        return serverList.size();
                    }

                    @Override
                    public void setValues(final PreparedStatement ps, final int index) throws SQLException {
                        final ServerModel server = serverList.get(index);
                        ps.setString(1, server.getRegionId());
                        ps.setString(2, server.getServerId());
                        ps.setString(3, server.getOperation());
                        ps.setString(4, server.getOperatorId());
                        ps.setString(5, server.getToken());

                        ps.setString(6, server.getOperatorId());
                        ps.setString(7, server.getToken());
                    }
                });
    }
}
