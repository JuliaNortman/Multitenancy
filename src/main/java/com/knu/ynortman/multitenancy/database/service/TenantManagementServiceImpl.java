package com.knu.ynortman.multitenancy.database.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Service;

import com.knu.ynortman.multitenancy.database.entity.Tenant;
import com.knu.ynortman.multitenancy.database.repository.TenantRepository;
import com.knu.ynortman.multitenancy.exception.TenantCreationException;
import com.knu.ynortman.multitenancy.service.EncryptionService;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@EnableConfigurationProperties(LiquibaseProperties.class)
//@ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "database")
public class TenantManagementServiceImpl implements TenantManagementService {

    private final EncryptionService encryptionService;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final LiquibaseProperties liquibaseProperties;
    private final ResourceLoader resourceLoader;
    private final TenantRepository tenantRepository;

    //private final String urlPrefix;
    private final String secret;
    private final String salt;

    @Autowired
    public TenantManagementServiceImpl(EncryptionService encryptionService,
                                       @Qualifier("masterDataSource") DataSource dataSource,
                                       @Qualifier("masterJdbcTemplate") JdbcTemplate jdbcTemplate,
                                       @Qualifier("tenantLiquibaseProperties")
                                               LiquibaseProperties liquibaseProperties,
                                       ResourceLoader resourceLoader,
                                       //@Qualifier("tenantRepository")
                                       TenantRepository tenantRepository,
                                       @Value("${encryption.secret}")
                                               String secret,
                                       @Value("${encryption.salt}")
                                               String salt
    ) {
        this.encryptionService = encryptionService;
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.liquibaseProperties = liquibaseProperties;
        this.resourceLoader = resourceLoader;
        this.tenantRepository = tenantRepository;
        //this.urlPrefix = "jdbc:postgresql://localhost:5432/";
        this.secret = secret;
        this.salt = salt;
        log.info("TENANT MANAGEMENT SERVICE"); 
    }

    private static final String VALID_DATABASE_NAME_REGEXP = "[A-Za-z0-9_]*";

    @Override
    public void createTenant(String tenantId, String url, String password) throws TenantCreationException {

    	String db = url.substring(url.lastIndexOf('/')+1, url.length());   	
        // Verify db string to prevent SQL injection
        if (!db.matches(VALID_DATABASE_NAME_REGEXP)) {
            throw new TenantCreationException("Invalid db name: " + db);
        }
        
        String encryptedPassword = encryptionService.encrypt(password, secret, salt);
        try {
            createDatabase(db, password);
        } catch (DataAccessException e) {
            //log.error("Error when creating db: " + db);
            throw new TenantCreationException("Error when creating db: " + db, e);
        }
        try (Connection connection = DriverManager.getConnection(url, db, password)) {
            DataSource tenantDataSource = new SingleConnectionDataSource(connection, false);
            runLiquibase(tenantDataSource);

            log.info("Tenant Management Service Impl Liquibase ran for tenant " + tenantId);
        } catch (SQLException | LiquibaseException e) {
            throw new TenantCreationException ("Error when populating db: ", e);
        }
        Tenant tenant = Tenant.builder()
                .tenantId(tenantId)
                .db(db)
                .url(url)
                .password(encryptedPassword)
                .build();
        tenantRepository.save(tenant);
    }

    @Override
    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    @Override 
    public Optional<Tenant> findByTenantId(String tenantId) {
        return tenantRepository.findByTenantId(tenantId);
    }

    @Override
    public void createDatabase(String db, String password) {
        log.info("BEFORE DATABASE CREATE");
        jdbcTemplate.execute((StatementCallback<Boolean>) stmt ->
                stmt.execute("CREATE DATABASE " + db));
        log.info("AFTER DATABASE CREATE");
        jdbcTemplate.execute((StatementCallback<Boolean>) stmt ->
                stmt.execute("CREATE USER " + db + " WITH ENCRYPTED PASSWORD '" + password + "'"));
        jdbcTemplate.execute((StatementCallback<Boolean>) stmt ->
                stmt.execute("GRANT ALL PRIVILEGES ON DATABASE " + db + " TO " + db));

    }

    private void runLiquibase(DataSource dataSource) throws LiquibaseException {
        SpringLiquibase liquibase = getSpringLiquibase(dataSource);
        liquibase.afterPropertiesSet();
    }

    protected SpringLiquibase getSpringLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setResourceLoader(resourceLoader);
        liquibase.setDataSource(dataSource);
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