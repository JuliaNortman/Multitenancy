package com.knu.ynortman.multitenancy.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "database")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tenant {

    @Id
    //@Column(name = "tenant_id")
    private String tenantId;

    //@Column(name = "db")
    private String db;

    //@Column(name = "password")
    private String password;

    //@Column(name = "url")
    private String url;
    
    private String driver;

}