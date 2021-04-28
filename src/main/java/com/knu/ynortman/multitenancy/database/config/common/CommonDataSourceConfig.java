package com.knu.ynortman.multitenancy.database.config.common;

import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "multitenancy.common.datasource", name = "url")
public class CommonDataSourceConfig {

    @Bean
    @ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "database")
    @ConfigurationProperties("multitenancy.common.datasource")
    public DataSourceProperties commonDataSourceProperties() {
    	log.info("COMMON DATA SOURCE CONFIG");
        return new DataSourceProperties();
    }

    @Bean
    @ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "database")
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