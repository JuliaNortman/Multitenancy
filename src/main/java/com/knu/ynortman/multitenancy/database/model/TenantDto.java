package com.knu.ynortman.multitenancy.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantDto {
    private String tenantId;
    private String db;
    private String password;
}
