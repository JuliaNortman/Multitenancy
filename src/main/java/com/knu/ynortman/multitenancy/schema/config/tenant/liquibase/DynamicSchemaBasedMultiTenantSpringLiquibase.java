package com.knu.ynortman.multitenancy.schema.config.tenant.liquibase;

import com.knu.ynortman.multitenancy.schema.service.TenantManagementService;
import com.knu.ynortman.multitenancy.schema.config.tenant.liquibase.LiquibaseRunner;
import com.knu.ynortman.multitenancy.schema.entity.Tenant;
import com.knu.ynortman.multitenancy.schema.repository.TenantRepository;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;

/**
 * Based on MultiTenantSpringLiquibase, this class provides Liquibase support for
 * multi-tenancy based on a dynamic collection of DataSources.
 */
@Getter
@Setter
@Slf4j
@ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "schema")
public class DynamicSchemaBasedMultiTenantSpringLiquibase implements InitializingBean, ResourceLoaderAware {

    @Autowired
    private TenantManagementService tenantManagementService;
    //private TenantRepository masterTenantRepository;

    @Autowired
    @Qualifier("tenantDataSource")
    private DataSource dataSource;

    @Autowired
    @Qualifier("tenantLiquibaseProperties")
    private LiquibaseProperties liquibaseProperties;
    
    @Autowired
    private LiquibaseRunner liquibaseRunner;

    private ResourceLoader resourceLoader;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Schema based multitenancy enabled");
        liquibaseRunner.runAllSchemasSync(tenantManagementService.findAll(), resourceLoader);
    }

    /*protected void runOnAllSchemas(DataSource dataSource, List<Tenant> tenants) throws LiquibaseException {
		for (Tenant tenant : tenants) {
			log.info("Initializing Liquibase for tenant " + tenant.getTenantId());
			try {
				tenantManagementService.createSchema(tenant.getSchema());
			} catch (Exception e) {
				log.warn(e.getMessage());
			}
			SpringLiquibase liquibase = getSpringLiquibase(dataSource, tenant.getSchema());
			liquibase.afterPropertiesSet();
			log.info("Liquibase ran for tenant " + tenant.getTenantId());
		}
    }

    protected SpringLiquibase getSpringLiquibase(DataSource dataSource, String schema) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setResourceLoader(getResourceLoader());
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
    }*/

}
