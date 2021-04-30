package com.knu.ynortman.multitenancy.discriminator.config;

import javax.persistence.EntityManager;

import org.springframework.orm.jpa.JpaTransactionManager;

import com.knu.ynortman.multitenancy.discriminator.aop.TenantFilterAspect;

public class JpaTransactionManagerEnableFilter extends JpaTransactionManager {

	protected EntityManager createEntityManagerForTransaction() {
        EntityManager entityManager = super.createEntityManagerForTransaction();
        TenantFilterAspect.addTenantFilter(entityManager);
        return entityManager;
    }
}
