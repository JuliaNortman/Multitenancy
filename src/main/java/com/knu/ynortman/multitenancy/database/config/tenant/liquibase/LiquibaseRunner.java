package com.knu.ynortman.multitenancy.database.config.tenant.liquibase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Component;

import com.knu.ynortman.multitenancy.database.entity.Tenant;
import com.knu.ynortman.multitenancy.database.service.TenantManagementService;
import com.knu.ynortman.multitenancy.service.EncryptionService;
import com.knu.ynortman.multitenancy.util.aop.TrackExecutionTime;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LiquibaseRunner {
	
	@Autowired
    private TenantManagementService tenantManagementService;
	
	@Autowired
    private EncryptionService encryptionService;
	
	@Autowired
    @Qualifier("tenantLiquibaseProperties")
    private LiquibaseProperties liquibaseProperties;
	
	@Value("${encryption.secret}")
    private String secret;

    @Value("${encryption.salt}")
    private String salt;
    
    @Autowired
    private AsyncLiquibase asyncLiquibase;
	
	@TrackExecutionTime
	public void runAllTenantsSync(Collection<Tenant> tenants, ResourceLoader resourceLoader) {
		for (Tenant tenant : tenants) {
			String decryptedPassword = encryptionService.decrypt(tenant.getPassword(), secret, salt);
			try {
				tenantManagementService.createDatabase(tenant.getDb(), decryptedPassword);
			} catch (Exception e) {
				log.warn(e.getMessage());
			}
			log.info("Initializing Liquibase for tenant " + tenant.getTenantId());
			try (Connection connection = DriverManager.getConnection(tenant.getUrl(), tenant.getDb(),
					decryptedPassword)) {
				DataSource tenantDataSource = new SingleConnectionDataSource(connection, true);
				SpringLiquibase liquibase = getSpringLiquibase(tenantDataSource, resourceLoader);
				liquibase.afterPropertiesSet();
			} catch (SQLException | LiquibaseException e) {
				log.error("Failed to run Liquibase for tenant " + tenant.getTenantId(), e);
			}
			log.info("Liquibase ran for tenant " + tenant.getTenantId());
		}
	}
	
	@TrackExecutionTime
	public void runAllTenantsAsync(Collection<Tenant> tenants, ResourceLoader resourceLoader) {
		Collection<Future<String>> futures = new ArrayList<>(tenants.size());
		for (Tenant tenant : tenants) {
			try {
				futures.add(asyncLiquibase.runLiquibase(tenant, resourceLoader));
			} catch (LiquibaseException e) {
				log.error("Failed to run Liquibase for tenant " + tenant.getTenantId(), e);
			}
			log.info("Liquibase ran for tenant " + tenant.getTenantId());
		}
		
		for(Future<String> future : futures) {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				log.warn(e.getMessage());
			}
		}
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
