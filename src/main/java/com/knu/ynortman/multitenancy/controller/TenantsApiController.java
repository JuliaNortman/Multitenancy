package com.knu.ynortman.multitenancy.controller;

import com.knu.ynortman.multitenancy.exception.TenantCreationException;
import com.knu.ynortman.multitenancy.database.model.TenantDto;
import com.knu.ynortman.multitenancy.database.service.TenantManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tenants")
@Slf4j
public class TenantsApiController {

    @Autowired(required = false)
    private TenantManagementService tenantManagementService;

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
        //log.info("tenantID" + tenant.getTenantId());
        try {
            tenantManagementService.createTenant(tenant.getTenantId(), tenant.getDb(), tenant.getPassword());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (TenantCreationException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
