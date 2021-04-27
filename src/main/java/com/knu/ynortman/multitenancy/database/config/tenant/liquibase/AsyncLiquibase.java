package com.knu.ynortman.multitenancy.database.config.tenant.liquibase;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Future;

@Slf4j
@EnableAsync
@Component
public class AsyncLiquibase {

    @Autowired
    @Qualifier("tenantLiquibaseProperties")
    private LiquibaseProperties liquibaseProperties;

    @Async 
    public Future<String> runLiquibase(String url, String db, String pswrd, ResourceLoader resourceLoader)
            throws LiquibaseException {
        log.info("AsyncWorkerFuture: start current thread [" + Thread.currentThread().getName() + "]");
        try {
            Thread.sleep(3000);
            log.info(Thread.currentThread().getName() + " do not sleep anymore");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(url, db, pswrd)) {
            DataSource tenantDataSource = new SingleConnectionDataSource(connection, false);
            //SpringLiquibase liquibase = getSpringLiquibase(tenantDataSource);

            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setResourceLoader(resourceLoader);
            liquibase.setDataSource(tenantDataSource);
            liquibase.setChangeLog(liquibaseProperties.getChangeLog());
            liquibase.setContexts(liquibaseProperties.getContexts());
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

            liquibase.afterPropertiesSet();


        } catch (SQLException e) {
            log.warn(e.getMessage());
        }
        log.info("AsyncWorkerFuture: end current thread [" + Thread.currentThread().getName() + "]");
        return new AsyncResult<>(Thread.currentThread().getName());
    }
}
