package com.ctrip.soa.artemis.management.dao;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * Created by fang_j on 10/07/2016.
 */
public class DataConfig {

    private static final String DB_CONFIG_FILE = "data-source.properties";

    private static DataSource _dataSource;
    private static DataSourceTransactionManager _dataSourceTransactionManager;
    private static JdbcTemplate _jdbcTemplate;

    private static AtomicBoolean _inited = new AtomicBoolean();

    public static void init() throws Exception {
        if (!_inited.compareAndSet(false, true))
            return;

        initDataSource();

        _dataSourceTransactionManager = new DataSourceTransactionManager(_dataSource);
        _jdbcTemplate = new JdbcTemplate(_dataSource);
    }

    private static void initDataSource() throws Exception {
        Properties prop = new Properties();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(DB_CONFIG_FILE)) {
            if (is == null)
                throw new RuntimeException("No " + DB_CONFIG_FILE + " in class path.");
            prop.load(is);
        }

        _dataSource = BasicDataSourceFactory.createDataSource(prop);
    }

    public static JdbcTemplate jdbcTemplate() {
        return _jdbcTemplate;
    }

    public static DataSourceTransactionManager dataSourceTransactionManager() {
        return _dataSourceTransactionManager;
    }

    public DataSource dataSource() {
        return _dataSource;
    }

}