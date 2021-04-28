package com.knu.ynortman.multitenancy.database.service;
import com.knu.ynortman.multitenancy.database.entity.Tenant;
import com.knu.ynortman.multitenancy.database.model.TenantDto;
import com.knu.ynortman.multitenancy.exception.TenantCreationException;

import java.util.List;
import java.util.Optional;

public interface TenantManagementService {
    void createDatabase(String db, String password);
    void createTenant(TenantDto tenant) throws TenantCreationException;
    List<Tenant> findAll();
    Optional<Tenant> findByTenantId(String tenantId);
}