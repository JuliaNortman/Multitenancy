package com.knu.ynortman.multitenancy.database.config.tenant.liquibase;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.knu.ynortman.multitenancy.database.entity.Tenant;
import com.knu.ynortman.multitenancy.database.service.TenantManagementService;
import com.knu.ynortman.multitenancy.service.EncryptionService;
import com.knu.ynortman.multitenancy.service.EncryptionServiceImpl;

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
    private TenantManagementService tenantManagementService;
	
    private EncryptionService encryptionService;
	
	@Autowired
    @Qualifier("tenantLiquibaseProperties")
    private LiquibaseProperties liquibaseProperties;
	
	@Value("${encryption.secret}")
    private String secret;

    @Value("${encryption.salt}")
    private String salt;

    @Async 
    public Future<String> runLiquibase(Tenant tenant, ResourceLoader resourceLoader)
            throws LiquibaseException {
        log.info("AsyncWorkerFuture: start current thread [" + Thread.currentThread().getName() + "] " + secret);
        encryptionService = new EncryptionServiceImpl();
        String decryptedPassword = encryptionService.decrypt(tenant.getPassword(), secret, salt);
        try {
            tenantManagementService.createDatabase(tenant.getDb(), decryptedPassword);
        } catch (Exception e) {
            log.warn(e.getMessage());
        } 
        log.info("Initializing Liquibase for tenant " + tenant.getTenantId());
        try (Connection connection = DriverManager.getConnection(
                tenant.getUrl(), tenant.getDb(), decryptedPassword)) {
            DataSource tenantDataSource = new SingleConnectionDataSource(connection, true);
            SpringLiquibase liquibase = getSpringLiquibase(tenantDataSource, resourceLoader);
            liquibase.afterPropertiesSet();
        } catch (SQLException | LiquibaseException e) {
            log.error("Failed to run Liquibase for tenant " + tenant.getTenantId(), e);
        }
        //log.info("Liquibase ran for tenant " + tenant.getTenantId());
        log.info("AsyncWorkerFuture: end current thread [" + Thread.currentThread().getName() + "]");
        return new AsyncResult<>(Thread.currentThread().getName());
    }
    
    protected SpringLiquibase getSpringLiquibase(DataSource dataSource, ResourceLoader resourceLoader) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setResourceLoader(resourceLoader);
        liquibase.setDataSource(dataSource);
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
        return liquibase;
    }
}
