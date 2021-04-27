package com.knu.ynortman.multitenancy.controller;

import com.knu.ynortman.multitenancy.exception.TenantCreationException;
import com.knu.ynortman.multitenancy.database.entity.Tenant;
import com.knu.ynortman.multitenancy.database.model.TenantDto;
import com.knu.ynortman.multitenancy.database.repository.TenantRepositoryDb;
import com.knu.ynortman.multitenancy.database.service.TenantManagementService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tenants")
@Slf4j 
public class TenantsApiController {

    @Autowired(required = false)
    private TenantManagementService tenantManagementService;
    
    @Autowired
    private TenantRepositoryDb tenantRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

   /*@Autowired(required = false)
    private com.knu.ynortman.multitenancy.schema.service.TenantManagementService
            schemaBasedTenantManagementService;*/


    /*@PostMapping(path = "/schema", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createTenant(
            @RequestBody com.knu.ynortman.multitenancy.schema.model.TenantDto tenant) {
        try {
            schemaBasedTenantManagementService.createTenant(tenant.getTenantId(), tenant.getSchema());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (TenantCreationException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createTenant(@RequestBody TenantDto tenant){
        try {
            tenantManagementService.createTenant(tenant.getTenantId(), tenant.getDb(), tenant.getPassword());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (TenantCreationException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    } 
    
    @PostMapping(path = "/test/database/drop/{n}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteDatabases(@PathVariable Integer n){
    	
		for (int i = 0; i < n; ++i) {
			String tenantId = "tenant" + (i + 1);
			jdbcTemplate.execute((StatementCallback<Boolean>) stmt -> stmt.execute("DROP DATABASE " + tenantId));
			jdbcTemplate.execute((StatementCallback<Boolean>) stmt -> stmt.execute("DROP USER " + tenantId));
			log.info(tenantId);
		}
		return new ResponseEntity<>(HttpStatus.OK);
    } 
    
    @PostMapping(path = "/test/database/populate/{n}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> populateMasterDatabase(@PathVariable Integer n) {
    	
		final String urlPrefix = "jdbc:postgresql://localhost:5432/";
		for (int i = 0; i < n; ++i) {
			String tenantId = "tenant" + (i + 1);
			Tenant tenant = Tenant.builder().tenantId(tenantId).db(tenantId).url(urlPrefix + tenantId)
					.password("r513hsugCU6I6C+vMT/gqQ==").build();
			tenantRepository.save(tenant);
			
		}
		return new ResponseEntity<>(HttpStatus.OK);
		
		/*jdbcTemplate.execute((StatementCallback<Boolean>) stmt ->
                stmt.execute("CREATE DATABASE " + db));
        log.info("AFTER DATABASE CREATE");
        jdbcTemplate.execute((StatementCallback<Boolean>) stmt ->
                stmt.execute("CREATE USER " + db + " WITH ENCRYPTED PASSWORD '" + password + "'"));
        jdbcTemplate.execute((StatementCallback<Boolean>) stmt ->
                stmt.execute("GRANT ALL PRIVILEGES ON DATABASE " + db + " TO " + db));*/
    }
}
