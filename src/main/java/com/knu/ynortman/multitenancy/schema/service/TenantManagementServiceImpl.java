package com.knu.ynortman.multitenancy.schema.service;

import com.knu.ynortman.multitenancy.schema.entity.Tenant;
import com.knu.ynortman.multitenancy.exception.TenantCreationException;
import com.knu.ynortman.multitenancy.schema.repository.TenantRepository;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "schema")
public class TenantManagementServiceImpl implements TenantManagementService {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final LiquibaseProperties liquibaseProperties;
    private final ResourceLoader resourceLoader;
    private final TenantRepository tenantRepository;

    @Autowired
    public TenantManagementServiceImpl(
                                        @Qualifier("tenantDataSource")
                                        DataSource dataSource,
                                       JdbcTemplate jdbcTemplate,
                                       @Qualifier("tenantLiquibaseProperties")
                                               LiquibaseProperties liquibaseProperties,
                                       ResourceLoader resourceLoader,
                                                  TenantRepository tenantRepository) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.liquibaseProperties = liquibaseProperties;
        this.resourceLoader = resourceLoader;
        this.tenantRepository = tenantRepository;
    }

    private static final String VALID_SCHEMA_NAME_REGEXP = "[A-Za-z0-9_]*";

    @Override
    public void createTenant(String tenantId, String schema) throws TenantCreationException {

        // Verify schema string to prevent SQL injection
        if (!schema.matches(VALID_SCHEMA_NAME_REGEXP)) {
            throw new TenantCreationException("Invalid schema name: " + schema);
        }

        try {
            createSchema(schema);
            runLiquibase(dataSource, schema);
        } catch (DataAccessException e) {
            throw new TenantCreationException("Error when creating schema: " + schema, e);
        } catch (LiquibaseException e) {
            throw new TenantCreationException("Error when populating schema: ", e);
        }
        Tenant tenant = Tenant.builder()
                .tenantId(tenantId)
                .schema(schema)
                .build();
        try {
            log.info("CURRENT SCHEMA " + dataSource.getConnection().getSchema());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        tenantRepository.save(tenant);
    }
    
    @Override
    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }
    
    @Override
    public Optional<Tenant> findByTenantId(String id) {
    	return tenantRepository.findById(id);
    }
    
    @Override
    public void createSchema(String schema) {
        jdbcTemplate.execute((StatementCallback<Boolean>) stmt -> stmt.execute("CREATE SCHEMA " + schema));
    }

    private void runLiquibase(DataSource dataSource, String schema) throws LiquibaseException {
        SpringLiquibase liquibase = getSpringLiquibase(dataSource, schema);
        liquibase.afterPropertiesSet();
    }

    protected SpringLiquibase getSpringLiquibase(DataSource dataSource, String schema) {
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
