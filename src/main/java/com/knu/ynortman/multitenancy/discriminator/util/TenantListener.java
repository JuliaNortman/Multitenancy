package com.knu.ynortman.multitenancy.discriminator.util;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import com.knu.ynortman.multitenancy.discriminator.entity.TenantAware;
import com.knu.ynortman.multitenancy.util.TenantContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantListener {

    @PreUpdate
    @PreRemove
    @PrePersist
    public void setTenant(TenantAware entity) {
        final String tenantId = TenantContext.getTenantId();
        log.info("ENABLE LISTENER");
        entity.setTenantId(tenantId);
    }
}
