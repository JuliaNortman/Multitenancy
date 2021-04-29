package com.knu.ynortman.multitenancy.schema.config.common;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Configuration
@ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "schema")
public class CommonSchemaDataSourceConfig {

    @Value("${multitenancy.common.schema:public}")
    private String commonSchema;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Bean
    public DataSource commonDataSource() {
        HikariDataSource dataSource = dataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        dataSource.setSchema(commonSchema);
        dataSource.setPoolName("commonDataSource");
        return dataSource;
    }

}