package com.knu.ynortman.multitenancy.schema.repository;

import com.knu.ynortman.multitenancy.schema.entity.Tenant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "schema")
public interface TenantRepository extends JpaRepository<Tenant, String> {
    @Query("select t from Tenant t where t.tenantId = :tenantId")
    Optional<Tenant> findByTenantId(@Param("tenantId") String tenantId);
}