package com.knu.ynortman.multitenancy.database.config.common;

import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;

@Slf4j
@Lazy(false)
@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
@ConditionalOnProperty(prefix = "multitenancy.common.liquibase", name = "enabled", havingValue = "true", matchIfMissing = false)
public class CommonLiquibaseConfig {
    @Bean
    @ConfigurationProperties("multitenancy.common.liquibase")
    @ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "database")
    public LiquibaseProperties commonLiquibaseProperties() {
    	log.info("COMMON LIQUIBASE");
        return new LiquibaseProperties();
    }

    @Bean(name = "commonLiquibase")
    @ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "database")
    public SpringLiquibase liquibase(@Qualifier("commonDataSource") DataSource liquibaseDataSource) {
        LiquibaseProperties liquibaseProperties = commonLiquibaseProperties();
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(liquibaseDataSource);
        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setLiquibaseSchema(liquibaseProperties.getLiquibaseSchema());
        liquibase.setLiquibaseTablespace(liquibaseProperties.getLiquibaseTablespace());
        liquibase.setDatabaseChangeLogTable(liquibaseProperties.getDatabaseChangeLogTable());
        liquibase.setDatabaseChangeLogLockTable(liquibaseProperties.getDatabaseChangeLogLockTable());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setShouldRun(liquibaseProperties.isEnabled());
        liquibase.setLabels(liquibaseProperties.getLabels());
        liquibase.setChangeLogParameters(liquibaseProperties.getParameters());
        liquibase.setRollbackFile(liquibaseProperties.getRollbackFile());
        liquibase.setTestRollbackOnUpdate(liquibaseProperties.isTestRollbackOnUpdate());
        return liquibase;
    }
}
