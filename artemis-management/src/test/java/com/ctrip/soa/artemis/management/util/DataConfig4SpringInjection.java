package com.ctrip.soa.artemis.management.util;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.ctrip.soa.artemis.cluster.ClusterManager;
import com.ctrip.soa.artemis.cluster.NodeInitializer;
import com.ctrip.soa.artemis.management.dao.DataConfig;

/**
 * Created by fang_j on 10/07/2016.
 */
@Configuration
public class DataConfig4SpringInjection {
    static {
        try {
            DataConfig.init();
            ClusterManager.INSTANCE.init(new ArrayList<NodeInitializer>());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public static JdbcTemplate jdbcTemplate() {
        return DataConfig.jdbcTemplate();
    }

    @Bean
    public static DataSourceTransactionManager dataSourceTransactionManager() {
        return DataConfig.dataSourceTransactionManager();
    }
}
