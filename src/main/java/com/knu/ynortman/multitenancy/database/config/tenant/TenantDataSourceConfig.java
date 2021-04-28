package com.knu.ynortman.multitenancy.database.config.tenant;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class TenantDataSourceConfig {

	@Bean
	@ConfigurationProperties("multitenancy.tenant.datasource")
	public DataSourceProperties tenantDataSourceProperties() {
		return new DataSourceProperties();
	}
}
