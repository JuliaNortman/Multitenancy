package com.knu.ynortman.multitenancy.database.repository;

import com.knu.ynortman.multitenancy.database.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

//@ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "database")
public interface TenantRepositoryDb extends JpaRepository<Tenant, String> {

    @Query("select t from Tenant t where t.tenantId = :tenantId")
    Optional<Tenant> findByTenantId(@Param("tenantId") String tenantId);
}