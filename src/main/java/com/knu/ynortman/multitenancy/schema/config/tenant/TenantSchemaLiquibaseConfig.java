package com.knu.ynortman.multitenancy.schema.config.tenant;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.knu.ynortman.multitenancy.schema.config.tenant.liquibase.DynamicSchemaBasedMultiTenantSpringLiquibase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Lazy(false)
@Configuration
@ConditionalOnProperty(name = "multitenancy.tenant.liquibase.enabled", havingValue = "true",
        matchIfMissing = true)
@EnableConfigurationProperties(LiquibaseProperties.class)
public class TenantSchemaLiquibaseConfig {

    @Value("${multitenancy.strategy}")
    private String multitenantStrategy;

    @Bean
    @ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "schema")
    @ConfigurationProperties("multitenancy.tenant.liquibase")
    public LiquibaseProperties tenantLiquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean
    @ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "schema")
    public InitializingBean tenantLiquibase() {
        log.info("Strategy " + multitenantStrategy); 
        return new DynamicSchemaBasedMultiTenantSpringLiquibase();
        /*if(multitenantStrategy.equals("database")) {
            return new DynamicDataSourceBasedMultiTenantSpringLiquibase();
        } else { // schema
            return new DynamicSchemaBasedMultiTenantSpringLiquibase();
        }*/
    }

}
