package com.knu.ynortman.multitenancy.schema.config.tenant.liquibase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.knu.ynortman.multitenancy.schema.entity.Tenant;
import com.knu.ynortman.multitenancy.schema.service.TenantManagementService;
import com.knu.ynortman.multitenancy.util.aop.TrackExecutionTime;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "schema")
public class LiquibaseRunner {
	
	@Autowired
    @Qualifier("tenantLiquibaseProperties")
    private LiquibaseProperties liquibaseProperties;
	
	@Autowired
    private TenantManagementService tenantManagementService;
	
	@Autowired
    @Qualifier("tenantDataSource")
    private DataSource dataSource;
	
	@Autowired
    private AsyncLiquibase asyncLiquibase;
	
	@TrackExecutionTime
	public void runAllSchemasSync(List<Tenant> tenants, ResourceLoader resourceLoader) {
		for (Tenant tenant : tenants) {
			log.info("Initializing Liquibase for tenant " + tenant.getTenantId());
			try {
				tenantManagementService.createSchema(tenant.getSchema());
			} catch (Exception e) {
				log.warn(e.getMessage());
			}
			SpringLiquibase liquibase = getSpringLiquibase(dataSource, tenant.getSchema(), resourceLoader);
			try {
				liquibase.afterPropertiesSet();
			} catch (LiquibaseException e) {
				log.warn(e.getMessage());
			}
			log.info("Liquibase ran for tenant " + tenant.getTenantId());
		}
	}
	
	@TrackExecutionTime
	public void runAllSchemasAsync(List<Tenant> tenants, ResourceLoader resourceLoader) {
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
	
	protected SpringLiquibase getSpringLiquibase(DataSource dataSource, String schema, ResourceLoader resourceLoader) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setResourceLoader(resourceLoader);
        liquibase.setDataSource(dataSource);
        liquibase.setDefaultSchema(schema);
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
