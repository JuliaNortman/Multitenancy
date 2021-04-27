package com.knu.ynortman.multitenancy.database.config.tenant.liquibase;

import com.knu.ynortman.multitenancy.database.entity.Tenant;
import com.knu.ynortman.multitenancy.exception.TenantCreationException;
//import com.knu.ynortman.multitenancy.database.repository.TenantRepositoryDb;
import com.knu.ynortman.multitenancy.service.EncryptionService;
import com.knu.ynortman.multitenancy.database.service.TenantManagementService;
import com.knu.ynortman.multitenancy.util.aop.TrackExecutionTime;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Based on MultiTenantSpringLiquibase, this class provides Liquibase support for
 * multi-tenancy based on a dynamic collection of DataSources.
 */
@Getter
@Setter
@Slf4j
//@ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "database")
public class DynamicDataSourceBasedMultiTenantSpringLiquibase implements InitializingBean, ResourceLoaderAware {

    @Autowired
    private EncryptionService encryptionService;

    /*@Autowired
    private TenantRepository tenantRepository;*/

    @Autowired
    private LiquibaseRunner LiquibaseRunner;
    
    @Autowired
    private AsyncLiquibase asyncLiquibase;

    @Autowired
    @Qualifier("tenantLiquibaseProperties")
    private LiquibaseProperties liquibaseProperties;

    @Value("${encryption.secret}")
    private String secret;

    @Value("${encryption.salt}")
    private String salt;

    private ResourceLoader resourceLoader;

    @Autowired
    private TenantManagementService tenantManagementService;

    @Override
    public void afterPropertiesSet() {
        log.info("DynamicDataSources based multitenancy enabled");
        LiquibaseRunner.runOnAllTenants(tenantManagementService.findAll(), resourceLoader);
    }

    //@TrackExecutionTime
    /*protected void runOnAllTenants(Collection<Tenant> tenants) {
        Collection<Future<String>> futures = new ArrayList<>(tenants.size());
        for(Tenant tenant : tenants) {
            String decryptedPassword = encryptionService.decrypt(tenant.getPassword(), secret, salt);
            try {
                tenantManagementService.createDatabase(tenant.getDb(), decryptedPassword);
            } catch (Exception e) {
                log.warn(e.getMessage());
            } 
            log.info("Initializing Liquibase for tenant " + tenant.getTenantId());
            long startTime = System.currentTimeMillis();
            try (Connection connection = DriverManager.getConnection(
                    tenant.getUrl(), tenant.getDb(), decryptedPassword)) {
                DataSource tenantDataSource = new SingleConnectionDataSource(connection, true);
                SpringLiquibase liquibase = this.getSpringLiquibase(tenantDataSource);
                liquibase.afterPropertiesSet();
            } catch (SQLException | LiquibaseException e) {
                log.error("Failed to run Liquibase for tenant " + tenant.getTenantId(), e);
            }
            long endtime = System.currentTimeMillis();
            log.info("Time taken for Execution is : " + (endtime-startTime) +"ms");

            /*try {
                futures.add(asyncLiquibase.runLiquibase(tenant.getUrl(), tenant.getDb(), decryptedPassword, resourceLoader));
            } catch (LiquibaseException e) {
                log.warn("Liquibase exception");
                log.warn(e.getMessage());
            }*/
            //log.info("Liquibase ran for tenant " + tenant.getTenantId());
        //}
        /*for(Future<String> future : futures) {
            try {
                log.info(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.warn(e.getMessage());
            }
        }*/
    //}

    /*protected SpringLiquibase getSpringLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setResourceLoader(getResourceLoader());
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
    }*/
    
    
    
}