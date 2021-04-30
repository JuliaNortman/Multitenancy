package com.knu.ynortman.multitenancy.discriminator.entity;

public interface TenantAware {
    void setTenantId(String tenantId);
}
