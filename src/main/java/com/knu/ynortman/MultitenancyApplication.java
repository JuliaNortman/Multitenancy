package com.knu.ynortman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, LiquibaseAutoConfiguration.class })
@EnableTransactionManagement
@EnableAsync
public class MultitenancyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultitenancyApplication.class, args);
	}

}
