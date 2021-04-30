package com.knu.ynortman.multitenancy.schema.service;

import java.util.List;
import java.util.Optional;

import com.knu.ynortman.multitenancy.exception.TenantCreationException;
import com.knu.ynortman.multitenancy.schema.entity.Tenant;

public interface TenantManagementService {
    void createTenant(String tenantId, String schema) throws TenantCreationException;
    List<Tenant> findAll();
    Optional<Tenant> findByTenantId(String id);
    void createSchema(String schema);
}