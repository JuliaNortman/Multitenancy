package com.knu.ynortman.multitenancy.schema.config.tenant.liquibase;

import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.knu.ynortman.multitenancy.schema.service.TenantManagementService;
import com.knu.ynortman.multitenancy.schema.entity.Tenant;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableAsync
@Component
@ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "schema")
public class AsyncLiquibase {
	
	@Autowired
	private TenantManagementService tenantManagementService;
	
	@Autowired
	@Qualifier("tenantDataSource")
	private DataSource dataSource;
	
	@Autowired
    @Qualifier("tenantLiquibaseProperties")
    private LiquibaseProperties liquibaseProperties;
	
	@Async
	public Future<String> runLiquibase(Tenant tenant, ResourceLoader resourceLoader) throws LiquibaseException {
		log.info("AsyncWorkerFuture: start current thread [" + Thread.currentThread().getName() + "] ");
		
		//log.info("Initializing Liquibase for tenant " + tenant.getTenantId());
		try {
			tenantManagementService.createSchema(tenant.getSchema());
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
		SpringLiquibase liquibase = getSpringLiquibase(dataSource, tenant.getSchema(), resourceLoader);
		liquibase.afterPropertiesSet();
		//log.info("Liquibase ran for tenant " + tenant.getTenantId());
		log.info("AsyncWorkerFuture: end current thread [" + Thread.currentThread().getName() + "]");
        return new AsyncResult<>(Thread.currentThread().getName());
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
