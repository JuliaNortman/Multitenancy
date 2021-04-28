package com.knu.ynortman.multitenancy.database.repository;

import com.knu.ynortman.multitenancy.database.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

//@ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "database")
public interface TenantRepository extends JpaRepository<Tenant, String> {

    Optional<Tenant> findByTenantId(@Param("tenantId") String tenantId);
}