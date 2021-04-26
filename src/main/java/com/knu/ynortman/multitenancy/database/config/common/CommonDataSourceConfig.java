package com.knu.ynortman.multitenancy.database.config.common;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Configuration
//@ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "datasource")
public class CommonDataSourceConfig {

    @Bean
    @ConfigurationProperties("multitenancy.default-tenant.datasource")
    public DataSourceProperties commonDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    //@ConfigurationProperties("multitenancy.default-tenant.datasource.hikari")
    public DataSource commonDataSource() {
        HikariDataSource dataSource = commonDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        dataSource.setPoolName("commonDataSource");
        return dataSource;
    }

}