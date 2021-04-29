package com.knu.ynortman.multitenancy.schema.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "schema_tenant")
@Table(name = "tenant")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "schema")
public class Tenant {

    @Id
    private String tenantId;

    @Column(name = "schma")
    private String schema;

}